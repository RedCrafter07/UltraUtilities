package redcrafter07.processed.item

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import java.util.function.Supplier

object ModItemGroup {
    val CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProcessedMod.ID);

    val EXAMPLE_TAB = registerCreativeModeTab("processed") {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + ProcessedMod.ID + ".main"))
            .icon() { ItemStack(ModBlocks.BLITZ_ORE) }
            .displayItems { params, output ->
                output.accept(ModBlocks.BLITZ_ORE);
                output.accept(ModItems.BLITZ_ORB);
            }
            .build()
    };

    private fun registerCreativeModeTab(name: String, tab: Supplier<CreativeModeTab>) {
        CREATIVE_MODE_TABS.register(name, tab);
    }
}