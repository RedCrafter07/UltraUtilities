package redcrafter07.processed.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.ModBlocks;
import redcrafter07.processed.tags.ProcessedTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, ProcessedMod.ID, existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.BLITZ_ORE.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.BLITZ_ORE.get());

        this.tag(Tags.Blocks.ORES)
                .add(ModBlocks.BLITZ_ORE.get());

        this.tag(ProcessedTags.Blocks.FORGE_ORE_BLITZ)
                .add(ModBlocks.BLITZ_ORE.get());
    }
}