package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.Containers
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import redcrafter07.processed.block.tile_entities.ProcessedMachine
import redcrafter07.processed.block.tile_entities.capabilities.SimpleDroppingContainer

abstract class ProcessedBlock(properties: Properties) : Block(properties), EntityBlock {
    companion object {
        val STATE_FACING = BlockStateProperties.FACING
        val STATE_HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING
    }

    protected open fun addBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {}

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(STATE_HORIZONTAL_FACING)
        addBlockStateDefinition(stateDefinition)
    }

    open fun getBlockstate(context: BlockPlaceContext): BlockState? {
        return null
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return (getBlockstate(context) ?: defaultBlockState() ?: stateDefinition.any()).setValue(
            STATE_HORIZONTAL_FACING,
            context.horizontalDirection.opposite
        )
    }

    override fun <T : BlockEntity?> getTicker(
        _level: Level,
        _state: BlockState,
        _blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return BlockEntityTicker { lv, pos, state, ticker ->
            if (ticker is ProcessedMachine) {
                if (lv.isClientSide) ticker.clientTick(lv, pos, state)
                else ticker.serverTick(lv, pos, state)
                ticker.tick(lv, pos, state)
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
        return (getBlockstate(context) ?: stateDefinition.any()).setValue(
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