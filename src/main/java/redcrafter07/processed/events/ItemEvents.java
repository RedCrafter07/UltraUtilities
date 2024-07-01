package redcrafter07.processed.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.item.WrenchItem;
import redcrafter07.processed.network.WrenchModeChangePacket;

@EventBusSubscriber(modid = ProcessedMod.ID)
public class ItemEvents {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        final var connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        final var player = Minecraft.getInstance().player;

        if (player != null && player.isShiftKeyDown()) {
            final var itemStack = player.getMainHandItem();

            if (itemStack.getItem() instanceof WrenchItem) {
                final var mode = WrenchItem.getMode(itemStack);

                final var newMode = (event.getScrollDeltaY() > 0) ? mode.next() : mode.previous();
                WrenchItem.setMode(itemStack, newMode);

                player.getInventory().setChanged();
                event.setCanceled(true);

                connection.send(new WrenchModeChangePacket(newMode));
                player.displayClientMessage(
                    Component.translatable(
                        "item.processed.wrench.mode", Component.translatable(newMode.translation())
                    ), true
                );
            }
        }
    }
}