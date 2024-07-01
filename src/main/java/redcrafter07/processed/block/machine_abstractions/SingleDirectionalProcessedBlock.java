package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class SingleDirectionalProcessedBlock extends ProcessedBlock {
    public SingleDirectionalProcessedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement (BlockPlaceContext context) {
        return defaultBlockState();
    }

    @Override
    public void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> stateDefinition){
        addBlockStateDefinition(stateDefinition);
    }
}
