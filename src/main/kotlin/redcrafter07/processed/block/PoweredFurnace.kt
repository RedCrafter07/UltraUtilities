package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity

class PoweredFurnace(private val tier: ProcessedTier): Block(Properties.of().sound(SoundType.STONE)), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val entity = PoweredFurnaceBlockEntity(pos, state);
        entity.tier = tier;
        return entity;
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        isMoving: Boolean
    ) {
        if (state.block != newState.block) {
            val blockEntity = level.getBlockEntity(pos);
            if (blockEntity is PoweredFurnaceBlockEntity) blockEntity.dropItems()

        }

        return super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { lv, pos, state, ticker ->
            if (ticker is PoweredFurnaceBlockEntity) ticker.tick(lv, pos, state)
        }
    }

    override fun use(
        _state: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        hand: InteractionHand,
        _hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            val blockEntity = level.getBlockEntity(blockPos)
            if (blockEntity !is PoweredFurnaceBlockEntity) return InteractionResult.PASS;
            player.openMenu(blockEntity) { data -> data.writeBlockPos(blockPos) };
            return InteractionResult.CONSUME;
        }
    }
}