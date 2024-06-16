package redcrafter07.processed.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.item.WrenchItem
import redcrafter07.processed.item.WrenchMode

@JvmRecord
data class WrenchModeChangePacket(val state: WrenchMode) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<WrenchModeChangePacket>(ProcessedMod.rl("p_wrench_change_mode"))
        val CODEC: StreamCodec<ByteBuf, WrenchModeChangePacket> = StreamCodec.composite(
            WrenchMode.STREAM_CODEC,
            WrenchModeChangePacket::state,
            ::WrenchModeChangePacket
        )
    }

    fun handleServer(context: IPayloadContext) {
        val player = context.player()
        val item = player.mainHandItem
        if (item.item is WrenchItem) {
            WrenchItem.setMode(item, state)
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}