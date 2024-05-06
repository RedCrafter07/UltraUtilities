package redcrafter07.processed.network

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.PlayPayloadContext
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.BlockSide
import redcrafter07.processed.block.tile_entities.IoState
import redcrafter07.processed.block.tile_entities.ProcessedMachine
import kotlin.jvm.optionals.getOrNull

@JvmRecord
data class IOChangePacket(val block: BlockPos, val state: IoState, val side: BlockSide, val itemOrFluid: Boolean) :
    CustomPacketPayload {
    companion object {
        val ID = ResourceLocation(ProcessedMod.ID, "p_io_change")
    }

    fun handle_server(context: PlayPayloadContext) {
        context.workHandler.execute {
            val level = context.level().getOrNull()
            if (level != null) {
                val blockEntity = level.getBlockEntity(block)
                if (blockEntity is ProcessedMachine) {
                    blockEntity.setSide(itemOrFluid, side, state)
                    blockEntity.invalidateCapabilities()
                }
            }
        }
    }

    constructor(buffer: FriendlyByteBuf) : this(
        buffer.readBlockPos(),
        IoState.load(buffer.readByte()),
        BlockSide.load(buffer.readByte()),
        buffer.readBoolean()
    )

    override fun write(buf: FriendlyByteBuf) {
        buf.writeBlockPos(block)
        buf.writeByte(state.save())
        buf.writeByte(side.save())
        buf.writeBoolean(itemOrFluid)
    }

    override fun id(): ResourceLocation {
        return ID
    }
}