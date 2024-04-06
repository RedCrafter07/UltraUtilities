package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.PipeLikeState
import java.util.Stack

class PipePressurizerBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModTileEntities.PIPE_BLOCK_ENTITY.get(), pos, state) {
    val connectedPipes: Stack<BlockPos> = Stack()

    private class DirectionalPosition(val blockPos: BlockPos, val direction: Direction) {}

    fun scanNetwork(level: LevelAccessor, myBlockPos: BlockPos) {
        connectedPipes.clear()
        val blocksToScan: Stack<DirectionalPosition> = Stack();
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.NORTH), Direction.NORTH))
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.SOUTH), Direction.SOUTH))
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.WEST), Direction.WEST))
        blocksToScan.push(DirectionalPosition(myBlockPos.relative(Direction.EAST), Direction.EAST))

        while (!blocksToScan.isEmpty()) {
            val directionalPosition = blocksToScan.pop() ?: continue
            if (connectedPipes.contains(directionalPosition.blockPos)) continue
            val blockEntity = level.getBlockEntity(directionalPosition.blockPos) ?: continue
            if (blockEntity !is PipeBlockEntity) continue
            if (blockEntity.pipeState.getState(directionalPosition.direction.opposite) == PipeLikeState.None) continue

            blockEntity.pipePressurizerPos = myBlockPos
            connectedPipes.push(directionalPosition.blockPos);
            for (direction in Direction.stream()) {
                val newBlockPos = DirectionalPosition(directionalPosition.blockPos.relative(direction), direction)
                if (connectedPipes.contains(newBlockPos.blockPos)) continue
                if (blocksToScan.contains(newBlockPos)) continue
                blocksToScan.push(newBlockPos)
            }
        }
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)

    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
    }
}