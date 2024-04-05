package redcrafter07.processed.block

import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty

enum class PipeLikeState(name: String) : StringRepresentable {
    Normal("Normal"),
    Push("Push"),
    Pull("Pull");

    override fun getSerializedName(): String {
        return this.name;
    }

    fun next(): PipeLikeState {
        if (this == Normal) {
            return Push;
        } else if (this == Push) {
            return Pull;
        } else if (this == Pull) {
            return Normal;
        } else {
            // should be unreachable
            return Normal;
        }
    }

    fun translation(): Component {
        return when(this) {
            //wait
            Normal -> Component.translatable("processed.pipe_like_state.normal")
            Pull -> Component.translatable("processed.pipe_like_state.pull")
            Push -> Component.translatable("processed.pipe_like_state.push")
            else -> Component.literal("Err: Unreachable pipe state")
        }

        //where does the state translation go to? Like, where do you see it?
    }
}

class BlockPipe() : Block(
    Properties.of()
        .sound(SoundType.STONE)
        .isRedstoneConductor(StatePredicate { _, _, _ -> false })
), WrenchInteractableBlock {
    private val PIPE_STATE = EnumProperty.create("state", PipeLikeState::class.java);

    init {
        val defaultState = this.defaultBlockState()
        defaultState.setValue(PIPE_STATE, PipeLikeState.Normal)
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(PIPE_STATE);
        super.createBlockStateDefinition(pBuilder);
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val oldPipeState = state.getValue(PIPE_STATE);
        val newPipeState = oldPipeState.next();
        state.setValue(PIPE_STATE, newPipeState);
        context.player?.sendSystemMessage(
            Component.translatable("processed.pipe_like_state.update",
                oldPipeState.translation(),
                newPipeState.translation()));
    }
}