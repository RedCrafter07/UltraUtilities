package redcrafter07.processed.block

import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import redcrafter07.processed.ProcessedMod

enum class PipeLikeState : StringRepresentable {
    None,
    Push,
    Pull;

    override fun getSerializedName(): String {
        return when (this) {
            None -> "none"
            Pull -> "pull"
            Push -> "push"
        }
    }

    fun next(): PipeLikeState {
        return when (this) {
            None -> Pull
            Pull -> Push
            Push -> None
        }
    }

    fun translation(): Component {
        return when (this) {
            None -> Component.translatable("processed.pipe_like_state.none")
            Pull -> Component.translatable("processed.pipe_like_state.pull")
            Push -> Component.translatable("processed.pipe_like_state.push")
        }
    }
}

class BlockPipe : Block(
    Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }
), WrenchInteractableBlock {
    companion object {
        private val pipeState = EnumProperty.create("pipe_state", PipeLikeState::class.java)
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(pipeState, PipeLikeState.None))
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(pipeState)
    }

    override fun getStateForPlacement(p_49820_: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(p_49820_)
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val oldPipeState = state.getValue(pipeState)
        val newPipeState = oldPipeState.next()
        state.setValue(pipeState, newPipeState)
        context.level.setBlock(context.clickedPos, state, UPDATE_CLIENTS or UPDATE_NEIGHBORS)
        ProcessedMod.LOGGER.info("Pos: " + context.clickedPos)

        context.player?.sendSystemMessage(
            Component.translatable(
                "processed.pipe_like_state.update", oldPipeState.translation(), newPipeState.translation()
            )
        )
    }
}