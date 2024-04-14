package redcrafter07.processed.network

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent
import redcrafter07.processed.ProcessedMod

@Mod.EventBusSubscriber(modid = ProcessedMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object NetworkHandler {
    @SubscribeEvent
    fun registerNetworkHandlers(event: RegisterPayloadHandlerEvent) {
        val register = event.registrar(ProcessedMod.ID)
        ProcessedMod.LOGGER.info("Registering packets!")

        register.play(
            WrenchModeChangePacket.ID,
            ::WrenchModeChangePacket
        ) { payload, context -> if (context.flow.isClientbound) payload.handle_client(payload, context) else payload.handle_server(payload, context) }
    }
}