package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.tile_entities.PipePressurizerBlockEntity

class PipePressurizerBlock : ProcessedBlock(
    Properties.of().isRedstoneConductor { _, _, _ -> false }.sound(
        SoundType.STONE
    )
) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipePressurizerBlockEntity(pos, state)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        isMoving: Boolean
    ) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is PipePressurizerBlockEntity) blockEntity.unlink(level)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun playerWillDestroy(
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState,
        player: Player
    ): BlockState {
        val blockEntity = level.getBlockEntity(blockPos)
        if (blockEntity is PipePressurizerBlockEntity) blockEntity.unlink(level)
        return super.playerWillDestroy(level, blockPos, blockState, player)
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
}