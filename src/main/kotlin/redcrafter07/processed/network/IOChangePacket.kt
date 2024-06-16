package redcrafter07.processed.network

import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.BlockSide
import redcrafter07.processed.block.tile_entities.IoState
import redcrafter07.processed.block.tile_entities.ProcessedMachine

@JvmRecord
data class IOChangePacket(val block: BlockPos, val state: IoState, val side: BlockSide, val itemOrFluid: Boolean) :
    CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<IOChangePacket>(ProcessedMod.rl("p_io_change"))
        val CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            IOChangePacket::block,
            IoState.STREAM_CODEC,
            IOChangePacket::state,
            BlockSide.STREAM_CODEC,
            IOChangePacket::side,
            ByteBufCodecs.BOOL,
            IOChangePacket::itemOrFluid,
            ::IOChangePacket
        )
    }

    fun handleServer(context: IPayloadContext) {
        context.enqueueWork {
            val level = context.player().level()
            val blockEntity = level.getBlockEntity(block)
            if (blockEntity is ProcessedMachine) {
                blockEntity.setSide(itemOrFluid, side, state)
                blockEntity.invalidateCapabilities()
            }

        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}