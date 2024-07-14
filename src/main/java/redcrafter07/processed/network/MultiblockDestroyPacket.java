package redcrafter07.processed.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.multiblock.MultiblockCasingCache;

import java.io.IOException;
import java.util.List;

public record MultiblockDestroyPacket(List<BlockPos> positions, BlockPos controllerPosition) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MultiblockDestroyPacket> TYPE = new CustomPacketPayload.Type<>(ProcessedMod.rl("multiblock_destroy"));

    public static final StreamCodec<ByteBuf, MultiblockDestroyPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            MultiblockDestroyPacket::positions,
            BlockPos.STREAM_CODEC,
            MultiblockDestroyPacket::controllerPosition,
            MultiblockDestroyPacket::new
    );

    public void handleClient(IPayloadContext context) {
        try (var level = context.player().level()) {
            for (BlockPos position : positions) {
                ChunkAccess chunk = level.getChunk(SectionPos.blockToSectionCoord(position.getX()), SectionPos.blockToSectionCoord(position.getZ()), ChunkStatus.FULL, false);
                if (chunk == null) continue;
                MultiblockCasingCache cache = MultiblockCasingCache.get(chunk);
                if (cache == null) continue;
                if (!cache.multiblockMap().containsKey(position)) continue;
                if (!cache.multiblockMap().get(position).equals(controllerPosition)) continue;
                cache.multiblockMap().remove(position);
                level.invalidateCapabilities(position);
                cache.set(chunk);
            }
        } catch (IOException | UnsupportedOperationException ignored) {}
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
