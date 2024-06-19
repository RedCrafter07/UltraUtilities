package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.tile_entities.ProcessedMachine
import redcrafter07.processed.block.tile_entities.TieredProcessedMachine
import redcrafter07.processed.block.tile_entities.capabilities.SimpleDroppingContainer
import redcrafter07.processed.item.ModItems

abstract class TieredProcessedBlock(
    properties: Properties,
    private val baseName: String,
    val tier: ProcessedTier,
    private val blockEntity: BlockEntitySupplier<TieredProcessedMachine>
) : ProcessedBlock(properties) {
    override fun getName(): MutableComponent {
        return Component.translatable(baseName, tier.translated())
    }

    open fun getShiftDescription(
        tooltips: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltips.add(Component.translatable("$baseName.tooltip"))
    }

    open fun getDescription(
        tooltips: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltips.add(Component.translatable("processed.tiered_machine_info", tier.colored()))
    }


    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        val entity = blockEntity.create(pos, state)
        entity.tier = tier
        return entity
    }
}

abstract class ProcessedBlock(properties: Properties) : Block(properties), EntityBlock {
    companion object {
        val STATE_FACING: DirectionProperty = BlockStateProperties.FACING
        val STATE_HORIZONTAL_FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    }

    override fun useItemOn(
        item: ItemStack,
        block: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (item.`is`(ModItems.WRENCH)) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
        return super.useItemOn(
            item,
            block,
            level,
            blockPos,
            player,
            hand,
            hitResult
        )
    }

    protected open fun addBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {}

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(STATE_HORIZONTAL_FACING)
        addBlockStateDefinition(stateDefinition)
    }

    open fun getBlockState(context: BlockPlaceContext): BlockState? {
        return null
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return (getBlockState(context) ?: defaultBlockState() ?: stateDefinition.any()).setValue(
            STATE_HORIZONTAL_FACING,
            context.horizontalDirection.opposite
        )
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return BlockEntityTicker { lv, pos, tickState, ticker ->
            if (ticker is ProcessedMachine) {
                if (lv.isClientSide) ticker.clientTick(lv, pos, tickState)
                else ticker.serverTick(lv, pos, tickState)
                ticker.tick(lv, pos, tickState)
            }
        }
    }

    open fun addDrops(drops: SimpleDroppingContainer, blockEntity: BlockEntity?) {}

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        isMoving: Boolean
    ) {
        if (state.block != newState.block) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is ProcessedMachine) blockEntity.dropItems()
            val container = SimpleDroppingContainer()
            addDrops(container, blockEntity)
            if (container.containerSize > 0) Containers.dropContents(level, pos, container)
        }

        super.onRemove(state, level, pos, newState, isMoving)
    }
}

abstract class AllDirectionsProcessedBlock(properties: Properties) : ProcessedBlock(properties) {
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return (getBlockState(context) ?: stateDefinition.any()).setValue(
            STATE_FACING,
            context.clickedFace
        )
    }

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(STATE_FACING)
        addBlockStateDefinition(stateDefinition)
    }
}

abstract class SingleDirectionalProcessedBlock(properties: Properties) : ProcessedBlock(properties) {
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState()
    }

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        addBlockStateDefinition(stateDefinition)
    }
}