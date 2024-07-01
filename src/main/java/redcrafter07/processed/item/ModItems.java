package redcrafter07.processed.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;

import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ProcessedMod.ID);

    public static final DeferredItem<ModItem> BLITZ_ORB = registerItem("blitz_orb", () -> new ModItem(new Item.Properties(), "blitz_orb"));
    public static final DeferredItem<WrenchItem> WRENCH = registerItem("wrench", WrenchItem::new);

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}