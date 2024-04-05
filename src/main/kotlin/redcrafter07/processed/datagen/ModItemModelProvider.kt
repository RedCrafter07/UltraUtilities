package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredItem
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.item.ModItems

class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    ItemModelProvider(output, ProcessedMod.ID, existingFileHelper) {
    override fun registerModels() {
        simpleItem(ModItems.BLITZ_ORB);
    }

    private fun simpleItem(item: DeferredItem<*>): ItemModelBuilder {
        return withExistingParent(item.id.path, ResourceLocation("item/generated"))
            .texture("layer0", ResourceLocation(ProcessedMod.ID, "item/" + item.id.path));
    }
}