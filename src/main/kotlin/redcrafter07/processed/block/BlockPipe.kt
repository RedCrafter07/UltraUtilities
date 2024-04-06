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
    fun save(): UShort {
        return when (this) {
            Normal -> 0.toUShort()
            Pull -> 1.toUShort()
            Push -> 2.toUShort()
            None -> 3.toUShort()
        }
    }

    companion object {
        fun load(short: UShort): PipeLikeState {
            val int = (short and 3.toUShort()).toInt();
            return when (int) {  /* 0b11, the last 2 bits of a number, 0-3 */
                0 -> Normal
                1 -> Pull
                2 -> Push
                3 -> None
                else -> Normal // unreachable
            }
        }
    }
}

class BlockPipe : Block(Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }), EntityBlock,
    WrenchInteractableBlock {
    companion object {

        val PIPE_STATE_UP = EnumProperty.create("pipe_state_top", PipeLikeState::class.java)
        val PIPE_STATE_DOWN = EnumProperty.create("pipe_state_bottom", PipeLikeState::class.java)
        val PIPE_STATE_NORTH = EnumProperty.create("pipe_state_north", PipeLikeState::class.java)
        val PIPE_STATE_SOUTH = EnumProperty.create("pipe_state_south", PipeLikeState::class.java)
        val PIPE_STATE_WEST = EnumProperty.create("pipe_state_west", PipeLikeState::class.java)
        val PIPE_STATE_EAST = EnumProperty.create("pipe_state_east", PipeLikeState::class.java)
        fun propertyForDirection(direction: Direction): EnumProperty<PipeLikeState> {
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
        val blockEntity = context.level.getBlockEntity(context.clickedPos);
        if (blockEntity !is PipeBlockEntity) return;
        val newPipeState = blockEntity.getState(context.clickedFace).next();
        blockEntity.setState(context.clickedFace, newPipeState);
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

    fun connectionType(
        my_block_entity: BlockEntity?,
        other_block_entity: BlockEntity?,
        direction: Direction,
    ): PipeLikeState {
        if (my_block_entity !is PipeBlockEntity) return PipeLikeState.None;
        if (other_block_entity !is PipeBlockEntity) return PipeLikeState.None;
        if (other_block_entity.getState(direction.opposite) == PipeLikeState.None) return PipeLikeState.None;
        return my_block_entity.getState(direction);
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
            connectionType(level.getBlockEntity(blockPosA), level.getBlockEntity(blockPosB), direction)
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val block_pos = context.clickedPos
        val level = context.level;
        var defaultBlockState = stateDefinition.any();
        for (direction in Direction.stream()) {
            val other_block_pos = block_pos.relative(direction);
            var connection_type =
                connectionType(level.getBlockEntity(block_pos), level.getBlockEntity(other_block_pos), direction);
            defaultBlockState = defaultBlockState.setValue(propertyForDirection(direction), connection_type)
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
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipeBlockEntity(pos, state)
    }
}