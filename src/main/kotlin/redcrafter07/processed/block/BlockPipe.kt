package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import redcrafter07.processed.block.tile_entities.PipeBlockEntity

class BlockPipe : Block(Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }), EntityBlock {
    companion object {
        val PIPE_STATE_TOP = BooleanProperty.create("PipeConnectionTop")
        val PIPE_STATE_BOTTOM = BooleanProperty.create("PipeConnectionBottom")
        val PIPE_STATE_NORTH = BooleanProperty.create("PipeConnectionNorth")
        val PIPE_STATE_SOUTH = BooleanProperty.create("PipeConnectionSouth")
        val PIPE_STATE_WEST = BooleanProperty.create("PipeConnectionWest")
        val PIPE_STATE_EAST = BooleanProperty.create("PipeConnectionEast")
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val block_pos = context.clickedPos
        val level = context.level;
        
        val default_block_state = defaultBlockState();

        return null;
    }

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(
            PIPE_STATE_TOP,
            PIPE_STATE_BOTTOM,
            PIPE_STATE_NORTH,
            PIPE_STATE_EAST,
            PIPE_STATE_SOUTH,
            PIPE_STATE_WEST
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipeBlockEntity(pos, state)
    }
}