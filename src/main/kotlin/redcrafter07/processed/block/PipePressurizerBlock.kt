package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.tile_entities.PipePressurizerBlockEntity

class PipePressurizerBlock : Block(
    BlockBehaviour.Properties.of().isRedstoneConductor { _, _, _ -> false }.sound(
        SoundType.STONE
    )
), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return PipePressurizerBlockEntity(pos, state)
    }

    override fun playerWillDestroy(
        level: Level,
        blockPos: BlockPos,
        p_49854_: BlockState,
        p_49855_: Player
    ): BlockState {
        val blockEntity = level.getBlockEntity(blockPos)
        if (blockEntity is PipePressurizerBlockEntity) blockEntity.unlink(level)
        return super.playerWillDestroy(level, blockPos, p_49854_, p_49855_)
    }

    override fun updateShape(
        myBlockState: BlockState,
        direction: Direction,
        otherBlockState: BlockState,
        level: LevelAccessor,
        myBlockPos: BlockPos,
        otherBlockPos: BlockPos
    ): BlockState {
        val blockEntity = level.getBlockEntity(myBlockPos)
        if (blockEntity !is PipePressurizerBlockEntity) return myBlockState
        blockEntity.scanNetwork(level, myBlockPos)
        return myBlockState
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return BlockEntityTicker { a, b, c, d ->
            if (d is PipePressurizerBlockEntity) d.tick(a, b, c)
        }
    }
}