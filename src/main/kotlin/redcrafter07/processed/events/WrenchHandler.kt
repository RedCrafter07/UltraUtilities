package redcrafter07.processed.events

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.item.WrenchItem
import redcrafter07.processed.network.WrenchModeChangePacket

@EventBusSubscriber(modid = ProcessedMod.ID)
object WrenchHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    fun onMouseScroll(event: InputEvent.MouseScrollingEvent) {
        val connection = Minecraft.getInstance().connection ?: return
        val player = Minecraft.getInstance().player

        if (player != null && player.isShiftKeyDown) {
            val itemStack = player.mainHandItem

            if (itemStack.item is WrenchItem) {
                val mode = WrenchItem.getMode(itemStack)
                val newMode = if (event.scrollDeltaY > 0) mode.next() else mode.previous()
                WrenchItem.setMode(itemStack, newMode)

                player.inventory.setChanged()
                event.isCanceled = true

                connection.send(WrenchModeChangePacket(newMode))
                player.displayClientMessage(
                    Component.translatable(
                        "item.processed.wrench.mode", Component.translatable(newMode.translation())
                    ), true
                )
            }
        }
    }
}