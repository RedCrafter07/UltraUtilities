package redcrafter07.processed.multiblock;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.BlockHitResult;
import redcrafter07.processed.network.MultiblockDestroyPacket;

import java.util.List;

public class CasingBlock extends Block {
    public CasingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!(neighborState.getBlock() instanceof MultiblockBlock) && !(neighborState.getBlock() instanceof CasingBlock) && !pos.equals(neighborPos)) {
            BlockPos machineBlock = MultiblockCasingCache.getControllerForCasing(level, pos);

            if (machineBlock != null && machineBlock != neighborPos && level.hasChunk(SectionPos.blockToSectionCoord(machineBlock.getX()), SectionPos.blockToSectionCoord(machineBlock.getZ()))
                    && level.getBlockEntity(machineBlock) instanceof MultiblockBlockEntity multiblock)
                multiblock.recheck();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        var chunk = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        var attachment = chunk != null ? MultiblockCasingCache.get(chunk) : null;
        if (attachment != null && attachment.multiblockMap().containsKey(pos)) {
            BlockPos machineBlock = attachment.multiblockMap().get(pos);

            if (level.hasChunk(SectionPos.blockToSectionCoord(machineBlock.getX()), SectionPos.blockToSectionCoord(machineBlock.getZ()))
                    && level.getBlockEntity(machineBlock) instanceof MultiblockBlockEntity multiblock)
                multiblock.recheck();

            if (attachment.multiblockMap().containsKey(pos)) {
                attachment.multiblockMap().remove(pos);
                attachment.set(chunk);

                if (level instanceof ServerLevel serverLevel && !level.isClientSide()) {
                    MultiblockDestroyPacket packet = new MultiblockDestroyPacket(List.of(pos), machineBlock);
                    for (ServerPlayer player : serverLevel.players()) player.connection.send(packet);
                }
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int chunkX = SectionPos.blockToSectionCoord(hitResult.getBlockPos().getX());
        int chunkZ = SectionPos.blockToSectionCoord(hitResult.getBlockPos().getZ());
        ChunkAccess chunk = level.getChunk(chunkX, chunkZ);
        var attachment = MultiblockCasingCache.get(chunk);
        if (player instanceof ServerPlayer sp) {
            Component component = attachment == null ? Component.literal("Server-Side: Attachment: null") : Component.literal("Server-Side: Attachment: " + attachment);

            sp.sendSystemMessage(component);
        } else {
            Component component = attachment == null ? Component.literal("Client-Side: Attachment: null") : Component.literal("Client-Side: Attachment: " + attachment);

            Minecraft.getInstance().getChatListener().handleSystemMessage(component, false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
