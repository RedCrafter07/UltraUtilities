package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.items.IItemHandler
import redcrafter07.processed.block.tile_entities.PipeBlockEntity

enum class PipeLikeState : StringRepresentable {
    Normal, Push, Pull, None;

    override fun getSerializedName(): String {
        return when (this) {
            Normal -> "normal"
            Pull -> "pull"
            Push -> "push"
            None -> "none"
        }
    }

    fun next(): PipeLikeState {
        return when (this) {
            Normal -> Pull
            Pull -> Push
            Push -> None
            None -> Normal
        }
    }

    fun translation(withColor: Boolean = false): Component {
        val base = "processed.pipe_state"
        val addition = when (withColor) {
            true -> "title."
            false -> ""
        }
        val state = when (this) {
            Normal -> "normal"
            Pull -> "pull"
            Push -> "push"
            None -> "none"
        }

        val formattedString = Component.translatable("${base}.${addition}${state}")

        return formattedString
    }
}

class BlockPipe : Block(Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }), EntityBlock,
    WrenchInteractableBlock {
    companion object {
        val PIPE_CONNECTION_UP = BooleanProperty.create("pipe_connection_top")
        val PIPE_CONNECTION_DOWN = BooleanProperty.create("pipe_connection_bottom")
        val PIPE_CONNECTION_NORTH = BooleanProperty.create("pipe_connection_north")
        val PIPE_CONNECTION_SOUTH = BooleanProperty.create("pipe_connection_south")
        val PIPE_CONNECTION_WEST = BooleanProperty.create("pipe_connection_west")
        val PIPE_CONNECTION_EAST = BooleanProperty.create("pipe_connection_east")

        val PIPE_STATE_UP = EnumProperty.create("pipe_state_top", PipeLikeState::class.java)
        val PIPE_STATE_DOWN = EnumProperty.create("pipe_state_bottom", PipeLikeState::class.java)
        val PIPE_STATE_NORTH = EnumProperty.create("pipe_state_north", PipeLikeState::class.java)
        val PIPE_STATE_SOUTH = EnumProperty.create("pipe_state_south", PipeLikeState::class.java)
        val PIPE_STATE_WEST = EnumProperty.create("pipe_state_west", PipeLikeState::class.java)
        val PIPE_STATE_EAST = EnumProperty.create("pipe_state_east", PipeLikeState::class.java)
        fun propertyForDirection(direction: Direction): BooleanProperty {
            return when (direction) {
                Direction.UP -> PIPE_CONNECTION_UP
                Direction.DOWN -> PIPE_CONNECTION_DOWN
                Direction.EAST -> PIPE_CONNECTION_EAST
                Direction.WEST -> PIPE_CONNECTION_WEST
                Direction.NORTH -> PIPE_CONNECTION_NORTH
                Direction.SOUTH -> PIPE_CONNECTION_SOUTH
            }
        }

        fun propertyStateForDirection(direction: Direction): EnumProperty<PipeLikeState> {
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

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val newPipeState = state.getValue(propertyStateForDirection(context.clickedFace)).next();
        context.level.setBlock(
            context.clickedPos,
            state.setValue(propertyStateForDirection(context.clickedFace), newPipeState),
            UPDATE_CLIENTS or UPDATE_NEIGHBORS
        );
        val player = context.player

        val pitch = when (newPipeState) {
            PipeLikeState.Pull -> 1.0f
            PipeLikeState.Push -> 1.2f
            PipeLikeState.None -> 1.5f
            PipeLikeState.Normal -> 1.8f
        }

        player?.playSound(NoteBlockInstrument.BELL.soundEvent.value(), 1.0f, pitch)

        if (player is ServerPlayer) {
            player.connection.send(
                ClientboundSetActionBarTextPacket(
                    Component.translatable(
                        "processed.pipe_state", newPipeState.translation(true)
                    )
                )
            )
        }
    }

    fun connectsTo(
        my_blockstate: BlockState,
        other_blockstate: BlockState,
        direction: Direction,
        other_block_entity: BlockEntity?
    ): Boolean {
        if (my_blockstate.block !is BlockPipe) return false;
        if (my_blockstate.getValue(propertyStateForDirection(direction)) == PipeLikeState.None) return false
        if (other_blockstate.block is BlockPipe) {
            return other_blockstate.getValue(propertyStateForDirection(direction.opposite)) != PipeLikeState.None;
        }

        return other_block_entity is IItemHandler
    }

    override fun updateShape(
        blockStateA: BlockState,
        direction: Direction,
        blockStateB: BlockState,
        level: LevelAccessor,
        blockPosA: BlockPos,
        blockPosB: BlockPos
    ): BlockState {
        return blockStateA.setValue(
            propertyForDirection(direction),
            connectsTo(blockStateA, blockStateB, direction, level.getBlockEntity(blockPosB))
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val block_pos = context.clickedPos
        val level = context.level;
        var defaultBlockState = stateDefinition.any();
        for (direction in Direction.stream()) {
            val other_block_pos = block_pos.relative(direction);
            val blockState = context.level.getBlockState(other_block_pos)
            var is_connected =
                connectsTo(defaultBlockState, blockState, direction, level.getBlockEntity(other_block_pos));
            defaultBlockState = defaultBlockState.setValue(propertyForDirection(direction), is_connected).setValue(
                propertyStateForDirection(direction), PipeLikeState.Normal
            )
        }

        return defaultBlockState
    }

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(
            PIPE_STATE_UP,
            PIPE_STATE_DOWN,
            PIPE_STATE_NORTH,
            PIPE_STATE_EAST,
            PIPE_STATE_SOUTH,
            PIPE_STATE_WEST,
            PIPE_CONNECTION_UP,
            PIPE_CONNECTION_DOWN,
            PIPE_CONNECTION_NORTH,
            PIPE_CONNECTION_EAST,
            PIPE_CONNECTION_SOUTH,
            PIPE_CONNECTION_WEST,
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipeBlockEntity(pos, state)
    }
}