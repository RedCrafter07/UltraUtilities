package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.Capabilities
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.PipeBlockEntity
import kotlin.math.floor

class BlockPipe : Block(Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }.noOcclusion()
    .lightLevel { 4 }),
    EntityBlock, WrenchInteractableBlock {

    override fun hasDynamicShape(): Boolean {
        return true;
    }

    override fun getShape(
        state: BlockState,
        p_60556_: BlockGetter,
        p_60557_: BlockPos,
        p_60558_: CollisionContext
    ): VoxelShape {
        var shape = SHAPE_CORE;
        if (state.getValue(PIPE_STATE_NORTH) != PipeLikeState.None) shape =
            Shapes.join(shape, SHAPE_NORTH, BooleanOp.OR);
        if (state.getValue(PIPE_STATE_SOUTH) != PipeLikeState.None) shape =
            Shapes.join(shape, SHAPE_SOUTH, BooleanOp.OR);
        if (state.getValue(PIPE_STATE_WEST) != PipeLikeState.None) shape = Shapes.join(shape, SHAPE_WEST, BooleanOp.OR);
        if (state.getValue(PIPE_STATE_EAST) != PipeLikeState.None) shape = Shapes.join(shape, SHAPE_EAST, BooleanOp.OR);
        if (state.getValue(PIPE_STATE_TOP) != PipeLikeState.None) shape = Shapes.join(shape, SHAPE_TOP, BooleanOp.OR);
        if (state.getValue(PIPE_STATE_BOTTOM) != PipeLikeState.None) shape =
            Shapes.join(shape, SHAPE_BOTTOM, BooleanOp.OR);
        return shape;
    }

    override fun propagatesSkylightDown(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): Boolean {
        return true
    }

    override fun getShadeBrightness(p_60472_: BlockState, p_60473_: BlockGetter, p_60474_: BlockPos): Float {
        return 1f
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val x = floor((context.clickLocation.x - floor(context.clickLocation.x)) * 16).toInt()
        val y = floor((context.clickLocation.y - floor(context.clickLocation.y)) * 16).toInt()
        val z = floor((context.clickLocation.z - floor(context.clickLocation.z)) * 16).toInt()
        val direction = actualDirection(x, y, z, context.clickedFace);

        val blockEntity = context.level.getBlockEntity(context.clickedPos)
        if (blockEntity !is PipeBlockEntity) return
        val newPipeState = blockEntity.pipeState.getState(direction).next()
        blockEntity.pipeState.setState(direction, newPipeState)
        val player = context.player

        val otherBlockPos = context.clickedPos.relative(direction);
        context.level.setBlock(context.clickedPos, state.setValue(
            propertyForDirection(direction),
            connectionType(null, context.level.getBlockEntity(otherBlockPos), otherBlockPos, direction, newPipeState)
        ), UPDATE_CLIENTS or UPDATE_NEIGHBORS);

        val pitch = when (newPipeState) {
            PipeLikeState.Pull -> 1.0f
            PipeLikeState.Push -> 1.2f
            PipeLikeState.None -> 1.5f
            PipeLikeState.Normal -> 1.8f
        }

        player?.playSound(NoteBlockInstrument.BELL.soundEvent.value(), 1.0f, pitch)

        if (player is ServerPlayer) {
            player.connection.send(
                ClientboundSetActionBarTextPacket(
                    Component.translatable(
                        "processed.pipe_state", newPipeState.translation(true)
                    )
                )
            )
        }
    }

    private fun connectionType(
        myBlockEntity: BlockEntity?,
        otherBlockEntity: BlockEntity?,
        otherBlockPos: BlockPos,
        direction: Direction,
        myPipeStateDefault: PipeLikeState?
    ): PipeLikeState {
        var myPipeState = myPipeStateDefault
        if (myBlockEntity is PipeBlockEntity) {
            myPipeState = myBlockEntity.pipeState.getState(direction)
        }
        if (myPipeState == null || myPipeState == PipeLikeState.None) return PipeLikeState.None
        val level = otherBlockEntity?.level ?: myBlockEntity?.level ?: return PipeLikeState.None
        if (level.getCapability(
                Capabilities.ItemHandler.BLOCK, otherBlockPos, direction.opposite
            ) != null
        ) return myPipeState
        if (otherBlockEntity !is PipeBlockEntity) return PipeLikeState.None
        if (otherBlockEntity.pipeState.getState(direction.opposite) == PipeLikeState.None) return PipeLikeState.None
        return PipeLikeState.Normal
    }

    override fun updateShape(
        blockStateA: BlockState,
        direction: Direction,
        blockStateB: BlockState,
        level: LevelAccessor,
        blockPosA: BlockPos,
        blockPosB: BlockPos
    ): BlockState {
        return blockStateA.setValue(
            propertyForDirection(direction),
            connectionType(level.getBlockEntity(blockPosA), level.getBlockEntity(blockPosB), blockPosB, direction, null)
        )
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val blockPos = context.clickedPos
        val level = context.level
        var defaultBlockState = stateDefinition.any()
        for (direction in Direction.stream()) {
            val otherBlockPos = blockPos.relative(direction)
            defaultBlockState = defaultBlockState.setValue(
                propertyForDirection(direction), connectionType(
                    null, level.getBlockEntity(otherBlockPos), otherBlockPos, direction, PipeLikeState.Normal
                )
            )
        }

        return defaultBlockState
    }

    override fun createBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(
            PIPE_STATE_TOP,
            PIPE_STATE_BOTTOM,
            PIPE_STATE_NORTH,
            PIPE_STATE_EAST,
            PIPE_STATE_SOUTH,
            PIPE_STATE_WEST,
        )
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PipeBlockEntity(pos, state)
    }
}