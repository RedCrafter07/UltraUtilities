package redcrafter07.processed.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.item.ModItems;
import redcrafter07.processed.materials.MaterialItem;
import redcrafter07.processed.tags.ProcessedTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(
            PackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> providerCompletableFuture,
            CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(
                packOutput,
                providerCompletableFuture,
                tagLookupCompletableFuture,
                ProcessedMod.ID,
                existingFileHelper
        );
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(Tags.Items.INGOTS)
                .add(ModItems.BLITZ_ORB.get())
                .add(ModItems.INGOT_ITEMS.stream().map(DeferredItem::get).toArray(Item[]::new));

        this.tag(Tags.Items.DUSTS)
                .add(ModItems.DUST_ITEMS.stream().map(DeferredItem::get).toArray(Item[]::new));

        this.tag(Tags.Items.NUGGETS)
                .add(ModItems.NUGGET_ITEMS.stream().map(DeferredItem::get).toArray(Item[]::new));

        this.tag(ProcessedTags.Items.FORGE_INGOT_BLITZ)
                .add(ModItems.BLITZ_ORB.get());


        for (DeferredItem<MaterialItem> item : ModItems.DUST_ITEMS)
            this.tag(item.get().getTag()).add(item.get());

        for (DeferredItem<MaterialItem> item : ModItems.INGOT_ITEMS)
            this.tag(item.get().getTag()).add(item.get());

        for (DeferredItem<MaterialItem> item : ModItems.NUGGET_ITEMS)
            this.tag(item.get().getTag()).add(item.get());
    }
}