package redcrafter07.processed.block

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import redcrafter07.processed.ProcessedMod
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.item.ModItems
import java.util.function.Supplier

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(ProcessedMod.ID)

    // the returned ObjectHolderDelegate can be used as a property delegate
    // this is automatically registered by the deferred registry at the correct times
    val EXAMPLE_BLOCK =
        registerBlock("example_block") { Block(BlockBehaviour.Properties.of().lightLevel { 15 }.instabreak()) };

    private fun <T : Block> registerBlock(name: String, block: Supplier<T>): DeferredBlock<T> {
        // this will call registerItem later, that's why it's not implemented yet
        val regBlock = BLOCKS.register(name, block);

        registerBlockItem(name, regBlock);

        return regBlock;
    }

    private fun <T : Block> registerBlockItem(name: String, block: DeferredBlock<T>) {
        ModItems.registerItem(name) { BlockItem(block.get(), Item.Properties()) };
    }
}