package redcrafter07.processed.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.item.WrenchItem;
import redcrafter07.processed.item.WrenchMode;

public record WrenchModeChangePacket(WrenchMode state) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WrenchModeChangePacket> TYPE = new CustomPacketPayload.Type<>(ProcessedMod.rl("p_wrench_change_mode"));
    public static final StreamCodec<ByteBuf, WrenchModeChangePacket> CODEC = StreamCodec.composite(WrenchMode.STREAM_CODEC, WrenchModeChangePacket::state, WrenchModeChangePacket::new);

    public void handleServer(IPayloadContext context) {
        final var item = context.player().getMainHandItem();
        if (item.getItem() instanceof WrenchItem)
            WrenchItem.setMode(item, state);
    }

    @Override
    public CustomPacketPayload.Type<WrenchModeChangePacket> type() {
        return TYPE;
    }
}