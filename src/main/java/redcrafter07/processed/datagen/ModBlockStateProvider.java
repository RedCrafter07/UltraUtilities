package redcrafter07.processed.datagen;

import net.minecraft.data.PackOutput;
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
    }

    private void blockWithItem(DeferredBlock<?> blockRegistryObject, ModelFile model) {
        simpleBlockWithItem(blockRegistryObject.get(), model);
    }

    private void blockWithItem(DeferredBlock<?> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}