package redcrafter07.processed.events

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.gui.ModMenuTypes
import redcrafter07.processed.gui.PoweredFurnaceScreen

@Mod.EventBusSubscriber(modid = ProcessedMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object MenuScreens {
    @SubscribeEvent
    fun onRegisterMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.POWERED_FURNACE_MENU.get(), ::PoweredFurnaceScreen)
    }
}