package redcrafter07.processed.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.machine_abstractions.BlockSide;
import redcrafter07.processed.block.machine_abstractions.IoState;
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine;

import java.io.IOException;


public record IOChangePacket(BlockPos block, IoState state, BlockSide side, boolean itemOrFluid) implements
        CustomPacketPayload {
    public static final CustomPacketPayload.Type<IOChangePacket> TYPE = new CustomPacketPayload.Type<>(ProcessedMod.rl("p_io_change"));
    public static final StreamCodec<ByteBuf, IOChangePacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            IOChangePacket::block,
            IoState.STREAM_CODEC,
            IOChangePacket::state,
            BlockSide.STREAM_CODEC,
            IOChangePacket::side,
            ByteBufCodecs.BOOL,
            IOChangePacket::itemOrFluid,
            IOChangePacket::new
    );

    public void handleServer(IPayloadContext context) {
        try (var level = context.player().level()) {
            if (level.getBlockEntity(block) instanceof ProcessedMachine blockEntity) {
                blockEntity.setSide(itemOrFluid, side, state);
                blockEntity.invalidateCapabilities();
            }
        } catch (IOException ignored) {}
    }

    @Override
    public CustomPacketPayload.Type<IOChangePacket> type() {
        return TYPE;
    }
}