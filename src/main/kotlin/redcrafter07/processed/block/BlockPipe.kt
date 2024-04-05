package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.PipeBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.neoforged.neoforge.capabilities.Capabilities
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.PipeBlockEntity

class BlockPipe : Block(Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }), EntityBlock {
    companion object {
        val PIPE_STATE_UP = BooleanProperty.create("pipe_connection_top")
        val PIPE_STATE_DOWN = BooleanProperty.create("pipe_connection_bottom")
        val PIPE_STATE_NORTH = BooleanProperty.create("pipe_connection_north")
        val PIPE_STATE_SOUTH = BooleanProperty.create("pipe_connection_south")
        val PIPE_STATE_WEST = BooleanProperty.create("pipe_connection_west")
        val PIPE_STATE_EAST = BooleanProperty.create("pipe_connection_east")
        fun propertyForDirection(direction: Direction): BooleanProperty {
            return when (direction) {
                Direction.UP -> PIPE_STATE_UP
                Direction.DOWN -> PIPE_STATE_DOWN
                Direction.EAST -> PIPE_STATE_EAST
                Direction.WEST -> PIPE_STATE_WEST
                Direction.NORTH -> PIPE_STATE_NORTH
                Direction.SOUTH -> PIPE_STATE_SOUTH
            }
        }
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val block_pos = context.clickedPos
        val level = context.level
        val default_block_state = stateDefinition.any();
//        ProcessedMod.LOGGER.info(level.get)

        for (direction in Direction.stream()) {
            val blockState = context.level.getBlockState(block_pos.relative(direction))
            var is_connected = false
            if (blockState.block is PipeBlock) {
                is_connected = true
            } else {
                is_connected = (level.getCapability(
                    Capabilities.ItemHandler.BLOCK,
                    block_pos.relative(direction),
                    direction.opposite
                ) != null)
            }
            default_block_state.setValue(propertyForDirection(direction), is_connected)
        }

        return default_block_state
    }

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(
            PIPE_STATE_UP, PIPE_STATE_DOWN, PIPE_STATE_NORTH, PIPE_STATE_EAST, PIPE_STATE_SOUTH, PIPE_STATE_WEST
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipeBlockEntity(pos, state)
    }
}