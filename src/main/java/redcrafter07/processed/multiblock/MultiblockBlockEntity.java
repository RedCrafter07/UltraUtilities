package redcrafter07.processed.multiblock;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.items.IItemHandler;
import redcrafter07.processed.block.machine_abstractions.BlockSide;
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine;
import redcrafter07.processed.network.MultiblockDestroyPacket;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static redcrafter07.processed.ProcessedUtil.*;

public abstract class MultiblockBlockEntity extends ProcessedMachine {
    public MultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected abstract MultiblockValidator validator();

    private int timeUntilNextCheck = 40;
    private boolean isAssembled = false;
    private @Nullable Set<BlockPos> blocks = null;
    private @Nullable Set<BlockPos> blocksOld = null;

    private void synchroniseWithCache() {
        if (blocksOld == null) return;
        Level level = getLevel();
        if (level == null) return;

        for (BlockPos block : blocksOld) {
            if (blocks != null && blocks.contains(block)) continue;
            ChunkAccess chunk = level.getChunk(SectionPos.blockToSectionCoord(block.getX()), SectionPos.blockToSectionCoord(block.getZ()), ChunkStatus.FULL, false);
            if (chunk == null) return;
            MultiblockCasingCache cache = MultiblockCasingCache.get(chunk);
            if (cache == null) continue;
            if (!cache.multiblockMap().containsKey(block)) continue;
            if (!cache.multiblockMap().get(block).equals(getBlockPos())) continue;
            cache.multiblockMap().remove(block);
            cache.set(chunk);
        }

        if (blocks == null) {
            blocksOld = null;
            return;
        }
        for (BlockPos block : blocks) {
            if (blocksOld.contains(block)) continue;
            ChunkAccess chunk = level.getChunk(SectionPos.blockToSectionCoord(block.getX()), SectionPos.blockToSectionCoord(block.getZ()), ChunkStatus.FULL, false);
            if (chunk == null) return;
            MultiblockCasingCache cache = MultiblockCasingCache.getOrDefault(chunk);
            cache.multiblockMap().put(block, getBlockPos());
            cache.set(chunk);
        }
        blocksOld = null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        isAssembled = tag.getBoolean("isAssembled");
        if (tag.contains("blocks", 11)) {
            if (blocks == null) blocksOld = Set.of();
            else blocksOld = blocks;
            blocks = new HashSet<>();
            blocks.addAll(loadBlockPositions(tag.getIntArray("blocks")));
        } else {
            blocksOld = blocks == null ? Set.of() : blocks;
            blocks = null;
            isAssembled = false;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("isAssembled", isAssembled);
        if (blocks != null) tag.put("blocks", saveBlockPositions(blocks.stream().toList()));
    }

    public void onRemove(Level level) {
        if (blocks == null) return;
        if (level instanceof ServerLevel serverLevel && !level.isClientSide()) {
            MultiblockDestroyPacket packet = new MultiblockDestroyPacket(blocks.stream().toList(), getBlockPos());
            for (ServerPlayer player : serverLevel.players()) player.connection.send(packet);
        }
        for (BlockPos pos : blocks) {
            MultiblockCasingCache.removeCasing(level, pos);
            level.invalidateCapabilities(pos);
        }
    }

    public void tileTickCommon(Level level, BlockPos pos, BlockState state) {
    }

    public void tileTickServer(ServerLevel level, BlockPos pos, BlockState state) {
    }

    public void tileTickClient(ClientLevel level, BlockPos pos, BlockState state) {
    }

    @Override
    public final void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
    }

    @Override
    public final void clientTick(ClientLevel level, BlockPos pos, BlockState state) {
    }

    @Override
    public final void commonTick(Level level, BlockPos pos, BlockState state) {
        if (!isAssembled || !isRunning()) return;
        tileTickCommon(level, pos, state);
        if (level instanceof ServerLevel serverLevel && !level.isClientSide()) tileTickServer(serverLevel, pos, state);
        else if (level instanceof ClientLevel clientLevel && level.isClientSide())
            tileTickClient(clientLevel, pos, state);
    }

    @Override
    public final void tickNoProcessing(Level level, BlockPos pos, BlockState state) {
        synchroniseWithCache();

        if (level instanceof ServerLevel && !level.isClientSide()) {
            if (isAssembled) return;
            if (timeUntilNextCheck > 0) {
                timeUntilNextCheck -= 1;
                return;
            }
            timeUntilNextCheck = 40;

            recheck();
        }
    }

    @Nullable
    @Override
    public final IItemHandler itemCapabilityForSide(@Nullable BlockSide side, BlockState state) {
        if (isAssembled) return super.itemCapabilityForSide(side, state);
        return null;
    }

    private @Nullable ServerLevel getLevelServer() {
        if (getLevel() instanceof ServerLevel sp && !sp.isClientSide) return sp;
        return null;
    }

    public void recheck() {
        ServerLevel serverLevel = getLevelServer();
        if (serverLevel == null) return;

        @Nullable Set<BlockPos> affectedBlocks = validator().getBlocks(serverLevel, getBlockPos(), getFacingDirection(getBlockState()));
        if (affectedBlocks != null) {
            for (BlockPos block : affectedBlocks) {
                @Nullable BlockPos controllerPos = MultiblockCasingCache.getControllerForCasing(serverLevel, block);
                if (controllerPos != null && controllerPos != getBlockPos()) {
                    affectedBlocks = null;
                    break;
                }
            }
        }

        if (blocks == null) blocks = Set.of();
        Set<BlockPos> old = blocks;
        blocks = null;
        var wasPreviouslyAssembled = isAssembled;
        isAssembled = affectedBlocks != null && !affectedBlocks.isEmpty();
        if (affectedBlocks == null || affectedBlocks.isEmpty()) {
            for (BlockPos pos : old) {
                MultiblockCasingCache.removeCasing(serverLevel, pos);
                serverLevel.invalidateCapabilities(pos);
            }
            invalidateCapabilities();
            if (!old.isEmpty()) sync();
            return;
        }
        for (BlockPos pos : old) {
            if (pos.equals(getBlockPos())) continue;
            if (affectedBlocks.contains(pos)) continue;
            MultiblockCasingCache.removeCasing(serverLevel, pos);
            serverLevel.invalidateCapabilities(pos);
        }

        for (BlockPos pos : affectedBlocks) {
            if (pos.equals(getBlockPos())) continue;

            MultiblockCasingCache.setControllerForCasing(serverLevel, pos, getBlockPos());
            serverLevel.invalidateCapabilities(pos);
        }
        invalidateCapabilities();
        blocks = affectedBlocks;
        sync();

        @Nullable BlockPos.MutableBlockPos min = null;
        @Nullable BlockPos.MutableBlockPos max = null;

        for (BlockPos pos : affectedBlocks) {
            if (min == null) min = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
            else {
                if (pos.getX() < min.getX()) min.setX(pos.getX());
                if (pos.getY() < min.getY()) min.setY(pos.getY());
                if (pos.getZ() < min.getZ()) min.setZ(pos.getZ());
            }
            if (max == null) max = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
            else {
                if (pos.getX() > max.getX()) max.setX(pos.getX());
                if (pos.getY() > max.getY()) max.setY(pos.getY());
                if (pos.getZ() > max.getZ()) max.setZ(pos.getZ());
            }
        }
        if (min == null) return;
        max.setX(max.getX() + 1);
        max.setY(max.getY() + 1);
        max.setZ(max.getZ() + 1);

        min.setX(min.getX() - 1);
        min.setY(min.getY() - 1);
        min.setZ(min.getZ() - 1);

        if (wasPreviouslyAssembled) return;

        // redstone particles
        // front + back
        for (int y = min.getY(); y <= max.getY(); ++y) {
            for (int z = min.getZ(); z <= max.getZ(); ++z) {
                // front
                spawnAssembledParticle(min.getX(), y, z, serverLevel);
                // back
                spawnAssembledParticle(max.getX(), y, z, serverLevel);
            }
        }

        // bottom + top
        for (int x = min.getX(); x <= max.getX(); ++x) {
            for (int z = min.getZ(); z <= max.getZ(); ++z) {
                // bottom
                spawnAssembledParticle(x, min.getY(), z, serverLevel);
                // top
                spawnAssembledParticle(x, max.getY(), z, serverLevel);
            }
        }

        // left + right
        for (int y = min.getY(); y <= max.getY(); ++y) {
            for (int x = min.getX(); x <= max.getX(); ++x) {
                // left
                spawnAssembledParticle(x, y, min.getZ(), serverLevel);
                // right
                spawnAssembledParticle(x, y, max.getZ(), serverLevel);
            }
        }
    }

    private void spawnAssembledParticle(int x, int y, int z, ServerLevel level) {
        level.sendParticles(ParticleTypes.END_ROD, x + .5, y + .5, z + .5, 1, 0d, 0d, 0d, 0d);
    }

    public boolean isAssembled() {
        return isAssembled;
    }
}