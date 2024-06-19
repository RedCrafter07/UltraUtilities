package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity

class PoweredFurnaceBlock(tier: ProcessedTier) :
    TieredProcessedBlock(Properties.of().sound(SoundType.STONE), "block.processed.powered_furnace", tier, ::PoweredFurnaceBlockEntity), EntityBlock {

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

