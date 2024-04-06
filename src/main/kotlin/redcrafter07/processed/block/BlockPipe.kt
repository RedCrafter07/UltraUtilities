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
import net.neoforged.neoforge.capabilities.Capabilities
import redcrafter07.processed.block.tile_entities.PipeBlockEntity

class BlockPipe : Block(Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }.noOcclusion()),
    EntityBlock, WrenchInteractableBlock {
    companion object {

        val PIPE_STATE_UP = EnumProperty.create("pipe_state_top", PipeLikeState::class.java)
        val PIPE_STATE_DOWN = EnumProperty.create("pipe_state_bottom", PipeLikeState::class.java)
        val PIPE_STATE_NORTH = EnumProperty.create("pipe_state_north", PipeLikeState::class.java)
        val PIPE_STATE_SOUTH = EnumProperty.create("pipe_state_south", PipeLikeState::class.java)
        val PIPE_STATE_WEST = EnumProperty.create("pipe_state_west", PipeLikeState::class.java)
        val PIPE_STATE_EAST = EnumProperty.create("pipe_state_east", PipeLikeState::class.java)

        fun propertyForDirection(direction: Direction): EnumProperty<PipeLikeState> {
            return when (direction) {
                Direction.UP -> PIPE_STATE_UP
                Direction.DOWN -> PIPE_STATE_DOWN
                Direction.EAST -> PIPE_STATE_EAST
                Direction.WEST -> PIPE_STATE_WEST
                Direction.NORTH -> PIPE_STATE_NORTH
                Direction.SOUTH -> PIPE_STATE_SOUTH
            }
        }
    }

    override fun propagatesSkylightDown(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): Boolean {
        return true
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val blockEntity = context.level.getBlockEntity(context.clickedPos)
        if (blockEntity !is PipeBlockEntity) return
        val newPipeState = blockEntity.pipeState.getState(context.clickedFace).next()
        blockEntity.pipeState.setState(context.clickedFace, newPipeState)
        val player = context.player

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
        if(otherBlockEntity is PipeBlockEntity && otherBlockEntity.pipeState.getState(direction.opposite) != PipeLikeState.None) return PipeLikeState.Normal
        return myPipeState
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
            PIPE_STATE_UP,
            PIPE_STATE_DOWN,
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