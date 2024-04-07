package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.DirectionalPipeLikeState

class ItemPipeBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModTileEntities.PIPE_BLOCK_ENTITY.get(), pos, state) {
    val pipeState = DirectionalPipeLikeState()
    var pipePressurizerPos: BlockPos? = null

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        pipeState.saveToNBT("state", nbt)
        val pipePressurizerPos = pipePressurizerPos
        if (pipePressurizerPos == null) {
            nbt.putIntArray("pipePressurizerPos", listOf())
        } else {
            nbt.putIntArray("pipePressurizerPos", listOf(pipePressurizerPos.x, pipePressurizerPos.y, pipePressurizerPos.z))
        }
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        pipeState.loadFromNBT("state", nbt)
        val arr = nbt.getIntArray("pipePressurizerPos")
        if (arr.count() == 3) {
            pipePressurizerPos = BlockPos(arr[0], arr[1], arr[2])
        }
    }
}