package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class BlockEntityBlock<T: BlockEntity>(properties: Properties, private val blockEntityProvider: (BlockPos, BlockState) -> T?) : Block(properties), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        return blockEntityProvider.invoke(blockPos, blockState)
    }
}