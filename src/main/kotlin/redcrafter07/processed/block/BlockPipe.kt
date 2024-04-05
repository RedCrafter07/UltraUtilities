package redcrafter07.processed.block

import net.minecraft.client.resources.sounds.Sound
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.neoforged.neoforge.client.event.sound.SoundEvent

enum class PipeLikeState : StringRepresentable {
    None, Push, Pull;

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

    fun translation(withColor: Boolean = false): Component {
        val base = "processed.pipe_state"
        val addition = when (withColor) {
            true -> "title."
            false -> ""
        }
        val state = when (this) {
            None -> "none"
            Pull -> "pull"
            Push -> "push"
        }

        val formattedString = Component.translatable("${base}.${addition}${state}")

        return formattedString
    }
}

class BlockPipe : Block(Properties.of().sound(SoundType.STONE).isRedstoneConductor { _, _, _ -> false }),
    WrenchInteractableBlock {
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

    override fun onWrenchInfo(player: Player, state: BlockState) {
        val pipeState = state.getValue(pipeState)

        //ui.button.click
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), .25f, 1.0f)

        if (player is ServerPlayer) {
            player.connection.send(
                ClientboundSetActionBarTextPacket(
                    Component.translatable(
                        "processed.pipe_state", pipeState.translation(true)
                    )
                )
            )
        }
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val newPipeState = state.getValue(pipeState).next()
        val newState = state.setValue(pipeState, newPipeState)
        context.level.setBlock(context.clickedPos, newState, UPDATE_CLIENTS or UPDATE_NEIGHBORS)

        val player = context.player

        val pitch = when (newPipeState) {
            PipeLikeState.Pull -> 1.0f
            PipeLikeState.Push -> 1.2f
            PipeLikeState.None -> 1.5f
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
}