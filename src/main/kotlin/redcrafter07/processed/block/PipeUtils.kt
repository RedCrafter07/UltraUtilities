package redcrafter07.processed.block

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

enum class PipeState : StringRepresentable {
    Normal, Push, Pull, None;

    override fun getSerializedName(): String {
        return when (this) {
            Normal -> "normal"
            Pull -> "pull"
            Push -> "push"
            None -> "none"
        }
    }

    fun next(): PipeState {
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

        val formattedString = Component.translatable("$base.$addition.${this.serializedName}")

        return formattedString
    }

    fun save(): UShort {
        return when (this) {
            Normal -> 0.toUShort()
            Push -> 1.toUShort()
            Pull -> 2.toUShort()
            None -> 3.toUShort()
        }
    }

    companion object {
        fun load(short: UShort): PipeState {
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

class DirectionalPipeState(val blockEntity: BlockEntity) {
    private var stateNorth: PipeState = PipeState.Normal
    private var stateSouth: PipeState = PipeState.Normal
    private var stateWest: PipeState = PipeState.Normal
    private var stateEast: PipeState = PipeState.Normal
    private var stateUp: PipeState = PipeState.Normal
    private var stateDown: PipeState = PipeState.Normal

    fun getState(direction: Direction): PipeState {
        return when (direction) {
            Direction.UP -> stateUp
            Direction.DOWN -> stateDown
            Direction.NORTH -> stateNorth
            Direction.SOUTH -> stateSouth
            Direction.WEST -> stateWest
            Direction.EAST -> stateEast
        }
    }

    fun setState(direction: Direction, value: PipeState) {
        when (direction) {
            Direction.UP -> stateUp = value
            Direction.DOWN -> stateDown = value
            Direction.NORTH -> stateNorth = value
            Direction.SOUTH -> stateSouth = value
            Direction.WEST -> stateWest = value
            Direction.EAST -> stateEast = value
        }
        blockEntity.setChanged()
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
        stateUp = PipeState.load(ushort.rotateRight(10) and utwo)
        stateDown = PipeState.load(ushort.rotateRight(8) and utwo)
        stateWest = PipeState.load(ushort.rotateRight(6) and utwo)
        stateEast = PipeState.load(ushort.rotateRight(4) and utwo)
        stateNorth = PipeState.load(ushort.rotateRight(2) and utwo)
        stateSouth = PipeState.load(ushort and utwo)
    }
}

val SHAPE_CORE = Block.box(3.0, 3.0, 3.0, 13.0, 13.0, 13.0)
val SHAPE_NORTH = Block.box(3.0, 3.0, 0.0, 13.0, 13.0, 3.0)
val SHAPE_SOUTH = Block.box(3.0, 3.0, 13.0, 13.0, 13.0, 16.0)
val SHAPE_WEST = Block.box(0.0, 3.0, 3.0, 3.0, 13.0, 13.0)
val SHAPE_EAST = Block.box(13.0, 3.0, 3.0, 16.0, 13.0, 13.0)
val SHAPE_TOP = Block.box(3.0, 13.0, 3.0, 13.0, 16.0, 13.0)
val SHAPE_BOTTOM = Block.box(3.0, 0.0, 3.0, 13.0, 3.0, 13.0)

fun getShape(
    state: BlockState,
): VoxelShape {
    var shape = SHAPE_CORE
    if (state.getValue(PIPE_STATE_NORTH) != PipeState.None) shape =
        Shapes.join(shape, SHAPE_NORTH, BooleanOp.OR)
    if (state.getValue(PIPE_STATE_SOUTH) != PipeState.None) shape =
        Shapes.join(shape, SHAPE_SOUTH, BooleanOp.OR)
    if (state.getValue(PIPE_STATE_WEST) != PipeState.None) shape = Shapes.join(shape, SHAPE_WEST, BooleanOp.OR)
    if (state.getValue(PIPE_STATE_EAST) != PipeState.None) shape = Shapes.join(shape, SHAPE_EAST, BooleanOp.OR)
    if (state.getValue(PIPE_STATE_TOP) != PipeState.None) shape = Shapes.join(shape, SHAPE_TOP, BooleanOp.OR)
    if (state.getValue(PIPE_STATE_BOTTOM) != PipeState.None) shape =
        Shapes.join(shape, SHAPE_BOTTOM, BooleanOp.OR)
    return shape
}

val PIPE_STATE_TOP = EnumProperty.create("pipe_state_top", PipeState::class.java)
val PIPE_STATE_BOTTOM = EnumProperty.create("pipe_state_bottom", PipeState::class.java)
val PIPE_STATE_NORTH = EnumProperty.create("pipe_state_north", PipeState::class.java)
val PIPE_STATE_SOUTH = EnumProperty.create("pipe_state_south", PipeState::class.java)
val PIPE_STATE_WEST = EnumProperty.create("pipe_state_west", PipeState::class.java)
val PIPE_STATE_EAST = EnumProperty.create("pipe_state_east", PipeState::class.java)

fun propertyForDirection(direction: Direction): EnumProperty<PipeState> {
    return when (direction) {
        Direction.UP -> PIPE_STATE_TOP
        Direction.DOWN -> PIPE_STATE_BOTTOM
        Direction.EAST -> PIPE_STATE_EAST
        Direction.WEST -> PIPE_STATE_WEST
        Direction.NORTH -> PIPE_STATE_NORTH
        Direction.SOUTH -> PIPE_STATE_SOUTH
    }
}

fun actualDirection(originalX: Int, originalY: Int, originalZ: Int, direction: Direction): Direction {
    var x = originalX
    var y = originalY
    var z = originalZ
    when (direction) {
        Direction.WEST -> x = 7
        Direction.EAST -> x = 7
        Direction.DOWN -> y = 7
        Direction.UP -> y = 7
        Direction.NORTH -> z = 7
        Direction.SOUTH -> z = 7
    }
    if (x < 3) return Direction.WEST
    if (x >= 13) return Direction.EAST
    if (y < 3) return Direction.DOWN
    if (y >= 13) return Direction.UP
    if (z < 3) return Direction.NORTH
    if (z >= 13) return Direction.SOUTH
    return direction
}