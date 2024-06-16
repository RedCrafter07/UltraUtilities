package redcrafter07.processed.item

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import java.util.function.Supplier

object ModItemGroup {
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProcessedMod.ID)

    val MAIN_TAB = CREATIVE_MODE_TABS.register("processed", Supplier {
        CreativeModeTab.builder().title(Component.translatable("item_group." + ProcessedMod.ID + ".main"))
            .icon { ItemStack(ModItems.BLITZ_ORB.get()) }.displayItems { _, output ->
                for (i in ModItems.ITEMS.entries) {
                    output.accept(i.get())
                }
            }.build()
    })
}