package redcrafter07.processed.events

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.gui.ModMenuTypes
import redcrafter07.processed.gui.GenericMachineMenuScreen

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
object MenuScreens {
    @SubscribeEvent
    fun onRegisterMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.POWERED_FURNACE_MENU.get(), ::GenericMachineMenuScreen)
    }
}