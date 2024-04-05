package redcrafter07.processed.block

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.item.ModItems
import java.util.function.Supplier

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(ProcessedMod.ID)

    // the returned ObjectHolderDelegate can be used as a property delegate
    // this is automatically registered by the deferred registry at the correct times
    val BLITZ_ORE = registerBlock("blitz_ore") {
        Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).explosionResistance(1200f))
    }
    val BLOCK_PIPE = registerBlock("pipe") { BlockPipe() }

    private fun <T : Block> registerBlock(id: String, block: Supplier<T>): DeferredBlock<T> {
        // this will call registerItem later, that's why it's not implemented yet
        val regBlock = BLOCKS.register(id, block)

        registerBlockItem(id, regBlock)

        return regBlock
    }

    private fun <T : Block> registerBlockItem(id: String, block: DeferredBlock<T>) {
        ModItems.registerItem(id) { ModBlockItem(block.get(), Item.Properties(), id) }
    }
}