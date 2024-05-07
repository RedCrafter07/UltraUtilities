package redcrafter07.processed.datagen.loot

import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import net.neoforged.neoforge.registries.DeferredHolder
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.item.ModItems

class ModBlockLootTables : BlockLootSubProvider(setOf(), FeatureFlags.REGISTRY.allFlags()) {
    override fun generate() {
        createOreLootTable(ModBlocks.BLITZ_ORE.get(), ModItems.BLITZ_ORB.get(), 1f, 3f)
        this.dropSelf(ModBlocks.BLOCK_ITEM_PIPE.get())
        this.dropSelf(ModBlocks.BLOCK_PIPE_PRESSURIZER.get())

        ModBlocks.BLOCKS_POWERED_FURNACE.forEach { this.dropSelf(it.get()) }
    }

    private fun createOreLootTable(block: Block, item: Item, min: Float, max: Float) {
        this.add(
            block, createSilkTouchDispatchTable(
                block, applyExplosionDecay(
                    block,
                    LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))
                ) as LootPoolEntryContainer.Builder<*>
            )
        )
    }

    override fun getKnownBlocks(): Iterable<Block> {
        return Iterable {
            ModBlocks.BLOCKS.entries.stream().map(DeferredHolder<*, out Block>::get).iterator()
        }
    }
}