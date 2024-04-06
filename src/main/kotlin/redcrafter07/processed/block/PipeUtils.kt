package redcrafter07.processed.block

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable

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
