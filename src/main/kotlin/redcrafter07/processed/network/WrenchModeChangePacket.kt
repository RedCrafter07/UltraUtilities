package redcrafter07.processed.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.PlayPayloadContext
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.item.WrenchItem
import redcrafter07.processed.item.WrenchMode
import kotlin.jvm.optionals.getOrNull

@JvmRecord
data class WrenchModeChangePacket(val state: WrenchMode) : CustomPacketPayload {
    companion object {
        val ID = ResourceLocation(ProcessedMod.ID, "p_wrench_mode_change")
    }

    fun handle_server(context: PlayPayloadContext) {
        val player = context.player.getOrNull() ?: return
        val item = player.mainHandItem
        if (item.item is WrenchItem) {
            WrenchItem.setMode(item, state)
        }
    }

    constructor(buffer: FriendlyByteBuf) : this(WrenchMode.load(buffer.readByte()))

    override fun write(buf: FriendlyByteBuf) {
        buf.writeByte(state.save())
    }

    override fun id(): ResourceLocation {
        return ID
    }
}