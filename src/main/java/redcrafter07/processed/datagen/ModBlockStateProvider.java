package redcrafter07.processed.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ProcessedMod.ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.BLITZ_ORE);
        blockWithItem(ModBlocks.FLUID_TANK, new ModelFile.ExistingModelFile(ProcessedMod.rl("block/fluid_tank"), this.models().existingFileHelper));

        models().withExistingParent("block/metal_block", ResourceLocation.withDefaultNamespace("block/block"))
                .texture("all", ProcessedMod.rl("block/metal_block"))
                .texture("particle", "#all")
                .element()
                .cube("#all")
                .faces((dir, builder) -> builder.uvs(0, 0, 16, 16).cullface(dir).tintindex(0).end())
                .end();

        models().withExistingParent("block/ore_block", ResourceLocation.withDefaultNamespace("block/block"))
                .texture("layer0", ProcessedMod.rl("block/ore"))
                .texture("layer1", ProcessedMod.rl("block/ore_overlay"))
                .texture("particle", "#layer0")
                .renderType("cutout")
                .element()
                .cube("#layer0")
                .faces((dir, builder) -> builder.uvs(0, 0, 16, 16).cullface(dir).end())
                .end()
                .element()
                .cube("#layer1")
                .faces((dir, builder) -> builder.uvs(0, 0, 16, 16).cullface(dir).tintindex(1).end())
                .end();

        for (DeferredBlock<?> block : ModBlocks.METAL_BLOCKS) {
            final ResourceLocation blockRL = block.getId();
            final ResourceLocation modelRL = ResourceLocation.fromNamespaceAndPath(blockRL.getNamespace(), "block/" + blockRL.getPath());
            var model = models().withExistingParent(modelRL.getPath(), ProcessedMod.rl("block/metal_block"));
            simpleBlock(block.get(), model);
            itemModels().withExistingParent("item/" + block.getId().getPath(), modelRL);
        }

        for (DeferredBlock<?> block : ModBlocks.STONE_ORE_BLOCKS) {
            final ResourceLocation blockRL = block.getId();
            final ResourceLocation modelRL = ResourceLocation.fromNamespaceAndPath(blockRL.getNamespace(), "block/" + blockRL.getPath());
            var model = models().withExistingParent(modelRL.getPath(), ProcessedMod.rl("block/ore_block"));
            simpleBlock(block.get(), model);
            itemModels().withExistingParent("item/" + block.getId().getPath(), modelRL);
        }
    }

    private void blockWithItem(DeferredBlock<?> blockRegistryObject, ModelFile model) {
        simpleBlockWithItem(blockRegistryObject.get(), model);
    }

    private void blockWithItem(DeferredBlock<?> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}