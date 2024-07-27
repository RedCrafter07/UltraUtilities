package redcrafter07.processed.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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

        for (DeferredItem<?> dustItem : ModItems.DUST_ITEMS) {
            withExistingParent(dustItem.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                    .texture("layer0", ProcessedMod.rl("item/dust"));
        }

        for (DeferredItem<?> dustItem : ModItems.INGOT_ITEMS) {
            withExistingParent(dustItem.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                    .texture("layer0", ProcessedMod.rl("item/ingot"));
        }

        for (DeferredItem<?> dustItem : ModItems.NUGGET_ITEMS) {
            withExistingParent(dustItem.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                    .texture("layer0", ProcessedMod.rl("item/nugget"));
        }

        for (DeferredItem<?> dustItem : ModItems.RAW_ITEMS) {
            withExistingParent(dustItem.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                    .texture("layer0", ProcessedMod.rl("item/raw_metal"));
        }
    }

    private void simpleItem(DeferredItem<?> item) {
        withExistingParent(item.getId().getPath(), ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ProcessedMod.rl("item/" + item.getId().getPath()));
    }
}