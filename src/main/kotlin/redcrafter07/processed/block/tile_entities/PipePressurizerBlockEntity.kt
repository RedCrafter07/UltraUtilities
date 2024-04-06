package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.DirectionalPipeLikeState

class PipePressurizerBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(ModTileEntities.PIPE_BLOCK_ENTITY.get(), pos, state) {
    val connectedPipes: List<BlockPos> = listOf()

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
    }
}