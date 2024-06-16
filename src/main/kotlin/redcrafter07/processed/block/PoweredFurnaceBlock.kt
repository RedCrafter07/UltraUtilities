package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity

class PoweredFurnaceBlock(private val tier: ProcessedTier) :
    AllDirectionsProcessedBlock(Properties.of().sound(SoundType.STONE)), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val entity = PoweredFurnaceBlockEntity(pos, state)
        entity.tier = tier
        return entity
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        } else {
            val blockEntity = level.getBlockEntity(blockPos)
            if (blockEntity !is PoweredFurnaceBlockEntity) return InteractionResult.PASS
            player.openMenu(blockEntity) { data -> data.writeBlockPos(blockPos) }
            return InteractionResult.CONSUME
        }
    }
}

