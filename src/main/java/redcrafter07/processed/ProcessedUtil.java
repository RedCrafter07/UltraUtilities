package redcrafter07.processed;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import java.util.ArrayList;
import java.util.List;

public final class ProcessedUtil {
    public static final List<DirectionProperty> facingProperties = List.of(DirectionalBlock.FACING,
            HorizontalDirectionalBlock.FACING,
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.FACING);

    public static Direction getFacingDirection(BlockState state) {
        for (var property : facingProperties) if (state.hasProperty(property)) return state.getValue(property);
        return Direction.NORTH;
    }

    public static IntArrayTag saveBlockPositions(List<BlockPos> blocks) {
        List<Integer> list = new ArrayList<>();

        for (BlockPos block : blocks) {
            list.add(block.getX());
            list.add(block.getY());
            list.add(block.getZ());
        }

        return new IntArrayTag(list);
    }

    public static List<BlockPos> loadBlockPositions(int[] blocks) {
        List<BlockPos> list = new ArrayList<>();

        for (int i = 0; i < blocks.length / 3; ++i) {
            int offset = i * 3;
            list.add(new BlockPos(blocks[offset], blocks[offset + 1], blocks[offset + 2]));
        }

        return list;
    }
}
