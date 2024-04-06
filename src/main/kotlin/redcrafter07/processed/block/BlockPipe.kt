package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
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
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.neoforged.neoforge.capabilities.Capabilities
import redcrafter07.processed.ProcessedMod
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
            val int = (short and 0b11.toUShort()).toInt()
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
class DirectionalPipeLikeState {
    private var stateNorth: PipeLikeState = PipeLikeState.Normal
    private var stateSouth: PipeLikeState = PipeLikeState.Normal
    private var stateWest: PipeLikeState = PipeLikeState.Normal
    private var stateEast: PipeLikeState = PipeLikeState.Normal
    private var stateUp: PipeLikeState = PipeLikeState.Normal
    private var stateDown: PipeLikeState = PipeLikeState.Normal

    fun getState(direction: Direction): PipeLikeState {
        return when (direction) {
            Direction.UP -> stateUp
            Direction.DOWN -> stateDown
            Direction.NORTH -> stateNorth
            Direction.SOUTH -> stateSouth
            Direction.WEST -> stateWest
            Direction.EAST -> stateEast
        }
    }

    fun setState(direction: Direction, value: PipeLikeState) {
        when (direction) {
            Direction.UP -> stateUp = value
            Direction.DOWN -> stateDown = value
            Direction.NORTH -> stateNorth = value
            Direction.SOUTH -> stateSouth = value
            Direction.WEST -> stateWest = value
            Direction.EAST -> stateEast = value
        }
    }

    fun saveToNBT(name: String, nbt: CompoundTag) {
        val ushort = stateUp.save().rotateLeft(10) or
                stateDown.save().rotateLeft(8) or
                stateWest.save().rotateLeft(6) or
                stateEast.save().rotateLeft(4) or
                stateNorth.save().rotateLeft(2) or
                stateSouth.save()
        nbt.putShort(name, ushort.toShort())
    }

    fun loadFromNBT(name: String, nbt: CompoundTag) {
        val ushort = nbt.getShort(name).toUShort()
        val utwo = 0b11.toUShort()
        stateUp = PipeLikeState.load(ushort.rotateRight(10) and utwo)
        stateDown = PipeLikeState.load(ushort.rotateRight(8) and utwo)
        stateWest = PipeLikeState.load(ushort.rotateRight(6) and utwo)
        stateEast = PipeLikeState.load(ushort.rotateRight(4) and utwo)
        stateNorth = PipeLikeState.load(ushort.rotateRight(2) and utwo)
        stateSouth = PipeLikeState.load(ushort and utwo)
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
        val blockEntity = context.level.getBlockEntity(context.clickedPos)
        if (blockEntity !is PipeBlockEntity) return
        val newPipeState = blockEntity.pipeState.getState(context.clickedFace).next()
        blockEntity.pipeState.setState(context.clickedFace, newPipeState)
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
        other_block_pos: BlockPos,
        direction: Direction,
        myPipeStateDefault: PipeLikeState?
    ): PipeLikeState {
        var myPipeState = myPipeStateDefault;
        if (my_block_entity is PipeBlockEntity) {
            myPipeState = my_block_entity.pipeState.getState(direction);
        }
        if (myPipeState == null || myPipeState == PipeLikeState.None) return PipeLikeState.None
        val level = other_block_entity?.level ?: my_block_entity?.level ?: return PipeLikeState.None
        if (level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                other_block_pos,
                direction.opposite
            ) != null
        ) return myPipeState
        if (other_block_entity !is PipeBlockEntity) return PipeLikeState.None
        if (other_block_entity.pipeState.getState(direction.opposite) == PipeLikeState.None) return PipeLikeState.None
        return myPipeState
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
            connectionType(level.getBlockEntity(blockPosA), level.getBlockEntity(blockPosB), blockPosB, direction, null)
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val blockPos = context.clickedPos
        val level = context.level
        var defaultBlockState = stateDefinition.any()
        for (direction in Direction.stream()) {
            val otherBlockPos = blockPos.relative(direction)
            defaultBlockState = defaultBlockState.setValue(
                propertyForDirection(direction),
                connectionType(null, level.getBlockEntity(otherBlockPos), otherBlockPos, direction, PipeLikeState.Normal)
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
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipeBlockEntity(pos, state)
    }
}