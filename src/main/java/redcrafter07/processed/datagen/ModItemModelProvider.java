package redcrafter07.processed.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.item.ModItems;

class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ProcessedMod.ID, existingFileHelper);
    }


    @Override
    public void registerModels() {
        simpleItem(ModItems.BLITZ_ORB);
        simpleItem(ModItems.WRENCH);
    }

    private ItemModelBuilder simpleItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ProcessedMod.rl("item/" + item.getId().getPath()));
    }
}