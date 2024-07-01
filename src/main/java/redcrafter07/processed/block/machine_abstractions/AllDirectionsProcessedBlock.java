package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class AllDirectionsProcessedBlock extends ProcessedBlock {
    public AllDirectionsProcessedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        var blockState = this.getBlockState(context);
        if (blockState == null) blockState = defaultBlockState();
        return blockState.setValue(ProcessedBlock.STATE_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> stateDefinition){
        stateDefinition.add(ProcessedBlock.STATE_FACING);
        addBlockStateDefinition(stateDefinition);
    }
}
