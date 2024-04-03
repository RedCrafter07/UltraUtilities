package redcrafter07.processed.item

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks

object ModItemGroup {
    val CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProcessedMod.ID);

    //hang on

//    val EXAMPLE_TAB = CREATIVE_MODE_TABS.register("Processed") {
//        CreativeModeTab.builder()
//            .title(Component.translatable("itemGroup." + ProcessedMod.ID + ".main"))
//            .icon() { ItemStack(ModBlocks.EXAMPLE_BLOCK) }
////            .displayItems() {}
//            .build()
//    };
}