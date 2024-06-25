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
    val BLOCKS_POWERED_FURNACE = registerTieredBlock("powered_furnace", ProcessedTiers.TIERS, ::PoweredFurnaceBlock)

    val BLOCK_ITEM_PIPES = registerPipes(PipeData.ITEM_PIPE_BY_TIER)
    val BLOCK_FLUID_PIPES = registerPipes(PipeData.FLUID_PIPE_BY_TIER)
    val BLOCK_ENERGY_PIPES = registerPipes(PipeData.ENERGY_PIPE_BY_TIER)

    private fun registerPipes(pipeDatas: List<PipeData>): List<DeferredBlock<PipeBlock>> {
        return pipeDatas.map { registerBlock(it.identifier) { PipeBlock(it) } }
    }

    private fun <T : TieredProcessedBlock> registerTieredBlock(
        id: String,
        tiers: List<ProcessedTier>,
        block: TieredBlockProvider<T>,
    ): Set<DeferredBlock<T>> {
        return tiers.map { tier ->
            val regBlock = BLOCKS.register("${id}_${tier.named()}", Supplier { block.provide(tier) })
            ModItems.registerItem("${id}_${tier.named()}") { TieredModBlockItem(regBlock.get(), Item.Properties()) }
            regBlock
        }.toSet()
    }

    private fun <T : Block> registerBlock(id: String, block: Supplier<T>): DeferredBlock<T> {
        val regBlock = BLOCKS.register(id, block)

        ModItems.registerItem(id) { ModBlockItem(regBlock.get(), Item.Properties(), id) }

        return regBlock
    }

    fun interface TieredBlockProvider<T> {
        fun provide(tier: ProcessedTier): T
    }
}