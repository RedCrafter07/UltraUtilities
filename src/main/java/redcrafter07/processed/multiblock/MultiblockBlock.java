package redcrafter07.processed.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import redcrafter07.processed.block.machine_abstractions.ProcessedBlock;

public abstract class MultiblockBlock extends ProcessedBlock {
    public MultiblockBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!(neighborState.getBlock() instanceof CasingBlock) && !pos.equals(neighborPos)) {
            if (level.getBlockEntity(pos) instanceof MultiblockBlockEntity be) be.recheck();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof MultiblockBlockEntity be) be.onRemove(level);
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
