package redcrafter07.processed.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;

/*

- z
| x
\n y

fff
fff
fff

fff
c f
fff

fff
fff
fff

 */

public final class BlockmapMultiblockValidator implements MultiblockValidator {
    final Map<Block, MultiblockPart> blockMap;
    final List<List<MultiblockPart>> blockData;
    final Vector2i size;
    final BlockPos controllerPosition;

    public BlockmapMultiblockValidator(Map<Block, MultiblockPart> blockMap, List<List<MultiblockPart>> blockData, Vector2i size) {
        this.blockMap = blockMap;
        this.blockData = blockData;
        this.size = size;
        this.controllerPosition = this.findControllerPosition();
    }

    public MultiblockPart getBlockAt(BlockPos pos) {
        return blockData.get(pos.getY()).get(pos.getX() * size.x + pos.getZ());
    }

    public BlockPos findControllerPosition() {
        for (int y = 0; y < blockData.size(); ++y) {
            for (int x = 0; x < size.x; ++x) {
                for (int z = 0; z < size.y; ++z) {
                    if (getBlockAt(new BlockPos(x, y, z)) == MultiblockPart.Controller) {
                        return new BlockPos(x, y, z);
                    }
                }
            }
        }

        return BlockPos.ZERO;
    }

    private boolean isStructureLoaded(LevelAccessor level, BlockPos controller, Direction facing) {
        BlockPos start = controller.relative(facing, controllerPosition.getX()).relative(Direction.DOWN, controllerPosition.getY()).relative(facing.getClockWise(), controllerPosition.getZ());
        BlockPos end = controller.relative(facing, size.x - controllerPosition.getX()).relative(Direction.DOWN, blockData.size() - controllerPosition.getY()).relative(facing.getClockWise(), size.y - controllerPosition.getZ());

        int startChunkX = SectionPos.blockToSectionCoord(start.getX());
        int startChunkZ = SectionPos.blockToSectionCoord(start.getZ());
        int endChunkX = SectionPos.blockToSectionCoord(end.getX());
        int endChunkZ = SectionPos.blockToSectionCoord(end.getZ());

        for (int x = startChunkX; x <= endChunkX; ++x) {
            for (int z = startChunkZ; z <= endChunkZ; ++z) {
                if (!level.hasChunk(x, z)) return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public Set<BlockPos> getBlocks(LevelAccessor level, BlockPos controller, Direction facing) {
        // can't scan for the multiblock if it isn't entirely loaded
        if (!isStructureLoaded(level, controller, facing)) return null;
        final HashSet<BlockPos> positions = new HashSet<>();

        for (int x = -controllerPosition.getX(); x < size.x - controllerPosition.getX(); ++x) {
            BlockPos offsetX = controller.relative(facing, x);
            for (int z = -controllerPosition.getZ(); z < size.y - controllerPosition.getZ(); ++z) {
                BlockPos offsetZ = offsetX.relative(facing.getClockWise(), z);
                for (int y = -controllerPosition.getY(); y < blockData.size() - controllerPosition.getY(); ++y) {
                    MultiblockPart expected = getBlockAt(new BlockPos(x + controllerPosition.getX(), y + controllerPosition.getY(), z + controllerPosition.getZ()));
                    if (expected == MultiblockPart.Ignored) continue;

                    BlockPos pos = offsetZ.offset(0, y, 0);
                    var foundState = level.getBlockState(pos);
                    MultiblockPart found = blockMap.get(foundState.getBlock());
                    if (found == null || found != expected) return null;
                    @Nullable BlockPos controllerPos = MultiblockCasingCache.getControllerForCasing(level, pos);
                    if (controllerPos != null && controllerPos != controller) return null;
                    positions.add(pos);
                }
            }
        }

        return positions;
    }
}