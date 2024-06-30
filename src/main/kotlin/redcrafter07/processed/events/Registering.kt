package redcrafter07.processed.events

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.*
import redcrafter07.processed.gui.GenericMachineMenuScreen
import redcrafter07.processed.gui.ModMenuTypes
import redcrafter07.processed.network.IOChangePacket
import redcrafter07.processed.network.WrenchModeChangePacket

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
object Registering {
    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        val blocks = ModTileEntities.BLOCK_TYPES.entries.map { entry -> entry.get().validBlocks }.flatten().toTypedArray()

        event.registerBlock(
            Capabilities.ItemHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is ItemCapableBlockEntity) blockEntity.itemCapabilityForSide(BlockSide.translateDirection(side, state), state)
                else null
            },
            *blocks
        )
        event.registerBlock(
            Capabilities.FluidHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is FluidCapableBlockEntity) blockEntity.fluidCapabilityForSide(BlockSide.translateDirection(side, state), state)
                else null
            },
            *blocks
        )
        event.registerBlock(
            Capabilities.EnergyStorage.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is EnergyCapableBlockEntity) blockEntity.energyCapabilityForSide(BlockSide.translateDirection(side, state), state)
                else null
            },
            *blocks
        )
    }

    @SubscribeEvent
    fun registerNetworkHandlers(event: RegisterPayloadHandlersEvent) {
        val register = event.registrar(ProcessedMod.ID)
        ProcessedMod.LOGGER.info("Registering packets!")

        register.playToServer(
            WrenchModeChangePacket.TYPE,
            WrenchModeChangePacket.CODEC,
            WrenchModeChangePacket::handleServer
        )

        register.playToServer(
            IOChangePacket.TYPE,
            IOChangePacket.CODEC,
            IOChangePacket::handleServer
        )
    }

    @SubscribeEvent
    fun onRegisterMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(
            ModMenuTypes.POWERED_FURNACE_MENU.get(),
            ::GenericMachineMenuScreen
        )
    }
}