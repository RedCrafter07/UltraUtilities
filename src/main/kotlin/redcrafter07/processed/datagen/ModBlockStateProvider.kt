package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredBlock
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.*
import redcrafter07.processed.datagen.blocks.PipeProvider

class ModBlockStateProvider(output: PackOutput, exFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, ProcessedMod.ID, exFileHelper) {
    private val pipeProvider = PipeProvider(this)

    override fun registerStatesAndModels() {
        blockWithItem(ModBlocks.BLITZ_ORE)

        pipeProvider.pipeBlock(ModBlocks.BLOCK_ITEM_PIPE)
    }

    private fun blockWithItem(blockRegistryObject: DeferredBlock<*>, model: ModelFile) {
        simpleBlockWithItem(blockRegistryObject.get(), model)
    }

    private fun blockWithItem(blockRegistryObject: DeferredBlock<*>) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()))
    }
}