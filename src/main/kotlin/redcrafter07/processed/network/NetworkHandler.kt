package redcrafter07.processed.network

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import redcrafter07.processed.ProcessedMod

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object NetworkHandler {
    @SubscribeEvent
    fun registerNetworkHandlers(event: RegisterPayloadHandlersEvent) {
        val register = event.registrar(ProcessedMod.ID)
        ProcessedMod.LOGGER.info("Registering packets!")

        register.playToServer(
            WrenchModeChangePacket.TYPE,
            WrenchModeChangePacket.CODEC,
            WrenchModeChangePacket::handleServer // note: you need DirectionalPayloadHandler if you handle more than 1 direction
        )

        register.playToServer(
            IOChangePacket.TYPE,
            IOChangePacket.CODEC,
            IOChangePacket::handleServer
        )
    }
}