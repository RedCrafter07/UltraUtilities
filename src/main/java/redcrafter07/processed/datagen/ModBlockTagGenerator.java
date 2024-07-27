package redcrafter07.processed.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.ModBlocks;
import redcrafter07.processed.materials.MaterialBlock;
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
        MaterialBlock[] ore_blocks = ModBlocks.STONE_ORE_BLOCKS.stream().map(DeferredBlock::get).toArray(MaterialBlock[]::new);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.STONE_ORE_BLOCKS.stream().map(DeferredBlock::get).filter(block -> block.getMaterial().getMaterialTag() != null).toArray(MaterialBlock[]::new))
                .add(ModBlocks.METAL_BLOCKS.stream().map(DeferredBlock::get).filter(block -> block.getMaterial().getMaterialTag() != null).toArray(MaterialBlock[]::new))
                .add(ModBlocks.BLITZ_ORE.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.BLITZ_ORE.get());

        this.tag(Tags.Blocks.ORES)
                .add(ore_blocks)
                .add(ModBlocks.BLITZ_ORE.get());

        this.tag(Tags.Blocks.ORE_RATES_SINGULAR)
                .add(ModBlocks.BLITZ_ORE.get())
                .add(ore_blocks);

        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE)
                .add(ModBlocks.BLITZ_ORE.get())
                .add(ore_blocks);

        for (DeferredBlock<MaterialBlock> block : ModBlocks.METAL_BLOCKS) {
            var materialTag = block.get().getMaterial().getMaterialTag();
            if (materialTag != null) this.tag(materialTag).add(block.get());
        }

        for (DeferredBlock<MaterialBlock> block : ModBlocks.STONE_ORE_BLOCKS) {
            MaterialBlock blockInstance = block.get();
            var materialTag = blockInstance.getMaterial().getMaterialTag();
            this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/" + block.getId().getPath())))
                    .add(blockInstance);
            if (materialTag != null) this.tag(materialTag).add(blockInstance);
        }

        this.tag(ProcessedTags.Blocks.ORE_BLITZ)
                .add(ModBlocks.BLITZ_ORE.get());
    }
}