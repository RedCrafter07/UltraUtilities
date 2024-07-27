package redcrafter07.processed.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.materials.MaterialBlockItem;
import redcrafter07.processed.materials.MaterialItem;

public class ModItemGroup {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProcessedMod.ID);

    public static final DeferredHolder<?, ?> MAIN_TAB = CREATIVE_MODE_TABS.register("processed.main", () ->
            CreativeModeTab.builder().
                    title(Component.translatable("item_group." + ProcessedMod.ID + ".main"))
                    .icon(() -> new ItemStack(ModItems.BLITZ_ORB.get()))
                    .displayItems((parameters, output) -> {
                        for (var item : ModItems.ITEMS.getEntries()) {
                            final var itemInstance = item.get();
                            if (itemInstance instanceof MaterialItem || itemInstance instanceof MaterialBlockItem) continue;
                            output.accept(itemInstance);
                        }
                    })
                    .build());

    public static final DeferredHolder<?, ?> MATERIALS_TAB = CREATIVE_MODE_TABS.register("processed.materials", () ->
            CreativeModeTab.builder().
                    title(Component.translatable("item_group." + ProcessedMod.ID + ".materials"))
                    .icon(() -> new ItemStack(Items.IRON_INGOT))
                    .displayItems((parameters, output) -> {
                        for (var item : ModItems.ITEMS.getEntries()) {
                            final var itemInstance = item.get();
                            if (itemInstance instanceof MaterialItem || itemInstance instanceof MaterialBlockItem) output.accept(itemInstance);
                        }
                    })
                    .build());
}