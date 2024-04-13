package redcrafter07.processed.events

import net.minecraft.client.Minecraft
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.InputEvent
import redcrafter07.processed.item.WrenchItem
import redcrafter07.processed.item.WrenchMode

@Mod.EventBusSubscriber
object MouseScrollHandler {
    @SubscribeEvent
    fun onMouseScroll(event: InputEvent.MouseScrollingEvent) {
        val player = Minecraft.getInstance().player

        if (player != null && player.isShiftKeyDown) {
            val itemStack = player.mainHandItem

            if (itemStack.item is WrenchItem) {
                val nbt = itemStack.orCreateTag
                val mode = WrenchMode.Config.load(nbt.getShort("mode").toUShort())
                val newMode = if (event.scrollDeltaY > 0) mode.next() else mode.previous()
                nbt.putShort("mode", newMode.save().toShort())

                player.inventory.setChanged()

                event.isCanceled = true
            }
        }
    }
}