package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import redcrafter07.processed.block.WrenchInteractableBlock

enum class PipeLikeState : StringRepresentable {
    Normal, Push, Pull, None;

    override fun getSerializedName(): String {
        return when (this) {
            Normal -> "normal"
            Pull -> "pull"
            Push -> "push"
            None -> "none"
        }
    }

    fun next(): PipeLikeState {
        return when (this) {
            Normal -> Pull
            Pull -> Push
            Push -> None
            None -> Normal
        }
    }

    fun translation(withColor: Boolean = false): Component {
        val base = "processed.pipe_state"
        val addition = when (withColor) {
            true -> "title."
            false -> ""
        }
        val state = when (this) {
            Normal -> "normal"
            Pull -> "pull"
            Push -> "push"
            None -> "none"
        }

        val formattedString = Component.translatable("${base}.${addition}${state}")

        return formattedString
    }

    fun save(name: String, nbt: CompoundTag) {
        when (this) {
            Normal -> nbt.putByte(name, 0.toByte())
            Push -> nbt.putByte(name, 1.toByte())
            Pull -> nbt.putByte(name, 2.toByte())
            None -> nbt.putByte(name, 3.toByte())
        }
    }

    companion object {
        fun load(name: String, nbt: CompoundTag): PipeLikeState {
            return when (nbt.getByte(name).toInt()) {
                0 -> Normal
                1 -> Push
                2 -> Pull
                3 -> None
                else -> Normal
            }
        }
    }
}

class PipeBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(ModTileEntities.PIPE_BLOCK_ENTITY.get(), pos, state), WrenchInteractableBlock {
    val pipeDirectionalStates: HashMap<Direction, PipeLikeState> = HashMap(6)

    init {
        for (direction in Direction.stream()) {
            pipeDirectionalStates[direction] = PipeLikeState.Normal;
        }
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt);
        for (direction in Direction.stream()) {
            pipeDirectionalStates[direction] = PipeLikeState.load("pipe_state_" + direction.getName(), nbt);
        }
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        for (direction in Direction.stream()) {
            pipeDirectionalStates[direction]?.save("pipe_state_" + direction.getName(), nbt);
        }
    }

    override fun onWrenchUse(context: UseOnContext, state: BlockState) {
        val direction = context.clickedFace
        val newPipeState = this.pipeDirectionalStates[direction]?.next() ?: return
        this.pipeDirectionalStates[direction] = newPipeState
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
}