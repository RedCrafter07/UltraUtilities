package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import redcrafter07.processed.block.PipeLikeState
import java.util.*

class PipePressurizerBlockEntity(pos: BlockPos, state: BlockState) :
    ProcessedMachine(ModTileEntities.PIPE_PRESSURIZER_BLOCK_ENTITY.get(), pos, state) {
    val connectedPipes: Stack<BlockPos> = Stack()
    private var pushingTo: Stack<DirectionalPosition> = Stack()
    private var pullingFrom: Stack<DirectionalPosition> = Stack()
    private var tickCooldown = 10

    private class DirectionalPosition(val blockPos: BlockPos, val direction: Direction) {
        override fun toString(): String {
            return buildString {
                append("DirectionalPosition { x = ")
                append(blockPos.x)
                append(" y = ")
                append(blockPos.y)
                append(" x = ")
                append(blockPos.z)
                append(" dir = ")
                append(direction)
                append(" }")
            }
        }
    }

    fun unlink(level: LevelAccessor) {
        this.setChanged()
        pushingTo.clear()
        pullingFrom.clear()
        for (pipe in connectedPipes) {
            val blockEntity = level.getBlockEntity(pipe)
            if (blockEntity is ItemPipeBlockEntity) blockEntity.pipePressurizerPos = null
        }
    }

    fun markPipe(pipe: BlockPos) {
        val level = getLevel() ?: return
        val blockEntity = level.getBlockEntity(pipe)
        if (blockEntity !is ItemPipeBlockEntity) return
        for (dir in Direction.stream()) {
            val newBlockPos = pipe.relative(dir)
            val pipeState = blockEntity.pipeState.getState(dir)
            val hasCapability = level.getCapability(Capabilities.ItemHandler.BLOCK, newBlockPos, dir.opposite) != null
            pushingTo.removeIf { value -> value.blockPos == newBlockPos && value.direction == dir.opposite }
            pullingFrom.removeIf { value -> value.blockPos == newBlockPos && value.direction == dir.opposite }
            if (pipeState == PipeLikeState.Push && hasCapability) {
                pushingTo.push(DirectionalPosition(newBlockPos, dir.opposite))
            } else if (pipeState == PipeLikeState.Pull && hasCapability) {
                pullingFrom.push(DirectionalPosition(newBlockPos, dir.opposite))
            }
        }
        this.setChanged()
    }

    fun scanNetwork(level: LevelAccessor, myBlockPos: BlockPos) {
        unlink(level)
        connectedPipes.clear()
        val blocksToScan: Stack<DirectionalPosition> = Stack()
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.NORTH), Direction.NORTH))
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.SOUTH), Direction.SOUTH))
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.WEST), Direction.WEST))
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.EAST), Direction.EAST))

        while (!blocksToScan.isEmpty()) {
            val directionalPosition = blocksToScan.pop() ?: continue
            if (connectedPipes.contains(directionalPosition.blockPos)) continue
            val blockEntity = level.getBlockEntity(directionalPosition.blockPos) ?: continue
            if (blockEntity !is ItemPipeBlockEntity) continue
            if (blockEntity.pipeState.getState(directionalPosition.direction.opposite) == PipeLikeState.None) continue

            markPipe(directionalPosition.blockPos)
            blockEntity.pipePressurizerPos = myBlockPos
            connectedPipes.push(directionalPosition.blockPos)
            for (direction in Direction.stream()) {
                val newBlockPos = DirectionalPosition(directionalPosition.blockPos.relative(direction), direction)
                if (blockEntity.pipeState.getState(direction) == PipeLikeState.None) continue
                if (connectedPipes.contains(newBlockPos.blockPos)) continue
                if (blocksToScan.contains(newBlockPos)) continue
                blocksToScan.push(newBlockPos)
            }
        }
        this.setChanged()
    }

    companion object {
        private fun directionToInt(direction: Direction): Int {
            return when (direction) {
                Direction.UP -> 0
                Direction.DOWN -> 1
                Direction.NORTH -> 2
                Direction.SOUTH -> 3
                Direction.WEST -> 4
                Direction.EAST -> 5
            }
        }

        private fun intToDirection(int: Int): Direction {
            return when (int) {
                0 -> Direction.UP
                1 -> Direction.DOWN
                2 -> Direction.NORTH
                3 -> Direction.SOUTH
                4 -> Direction.WEST
                5 -> Direction.EAST
                else -> Direction.NORTH
            }
        }

        private fun saveStackOfDirectionalPosition(nbt: CompoundTag, name: String, stack: Stack<DirectionalPosition>) {
            val ints: Stack<Int> = Stack()

            for (value in stack) {
                ints.push(value.blockPos.x)
                ints.push(value.blockPos.y)
                ints.push(value.blockPos.z)
                ints.push(directionToInt(value.direction))
            }

            nbt.putIntArray(name, ints.toList())
        }

        private fun loadStackOfDirectionalPosition(nbt: CompoundTag, name: String): Stack<DirectionalPosition> {
            val ints = nbt.getIntArray(name)
            val stack: Stack<DirectionalPosition> = Stack()
            var idx = 0
            while (idx + 4 <= ints.count()) {
                stack.push(
                    DirectionalPosition(
                        BlockPos(ints[idx], ints[idx + 1], ints[idx + 2]),
                        intToDirection(ints[idx + 3])
                    )
                )
                idx += 4
            }

            return stack
        }
    }

    override fun saveAdditional(nbt: CompoundTag, provider: Provider) {
        super.saveAdditional(nbt, provider)
        val ints: Stack<Int> = Stack()

        for (pipe in connectedPipes) {
            ints.push(pipe.x)
            ints.push(pipe.y)
            ints.push(pipe.z)
        }

        nbt.putIntArray("blocks", ints.toList())
        saveStackOfDirectionalPosition(nbt, "pushing", pushingTo)
        saveStackOfDirectionalPosition(nbt, "pulling", pullingFrom)
    }

    override fun loadAdditional(nbt: CompoundTag, provider: Provider) {
        super.loadAdditional(nbt, provider)
        val integers = nbt.getIntArray("blocks")
        var idx = 0
        while (idx + 3 <= integers.count()) {
            connectedPipes.push(BlockPos(integers[idx], integers[idx + 1], integers[idx + 2]))
            idx += 3
        }

        pushingTo = loadStackOfDirectionalPosition(nbt, "pushing")
        pullingFrom = loadStackOfDirectionalPosition(nbt, "pulling")
    }

    override fun tick(level: Level, pos: BlockPos, state: BlockState) {
        if (tickCooldown > 0) {
            --tickCooldown
            return
        }
        var capability: IItemHandler? = null
        var slot = 0

        for (pullingPos in pullingFrom) {
            val localCapability =
                level.getCapability(Capabilities.ItemHandler.BLOCK, pullingPos.blockPos, pullingPos.direction)
                    ?: continue
            for (localSlot in 0..<localCapability.slots) {
                val item = localCapability.extractItem(localSlot, 1, true)
                if (item.isEmpty) continue
                capability = localCapability
                slot = localSlot
                break
            }
            if (capability != null) break
        }
        if (capability == null) return
        for (pushingPos in pushingTo) {
            val localCapability =
                level.getCapability(Capabilities.ItemHandler.BLOCK, pushingPos.blockPos, pushingPos.direction)
                    ?: continue
            for (localSlot in 0..<localCapability.slots) {
                val item = capability.extractItem(slot, 1, true)
                if (localCapability.insertItem(localSlot, item, true).isEmpty) {
                    localCapability.insertItem(localSlot, item, false)
                    capability.extractItem(slot, 1, false)
                    tickCooldown = 10
                    return
                }
            }
        }
    }
}