package redcrafter07.processed.datagen.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredBlock;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.ModBlocks;
import redcrafter07.processed.item.ModItems;
import redcrafter07.processed.materials.MaterialBlock;

import java.util.Set;

public final class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        createOreLootTable(ModBlocks.BLITZ_ORE.get(), ModItems.BLITZ_ORB.get(), 1, 3);

        for (DeferredBlock<MaterialBlock> oreBlock : ModBlocks.STONE_ORE_BLOCKS) {
            MaterialBlock instance = oreBlock.get();
            createOreLootTable(
                    instance,
                    BuiltInRegistries.ITEM.get(ProcessedMod.rl(instance.getMaterial().getRawPath())),
                    1, 2
            );
        }

        dropSelf(ModBlocks.FLUID_TANK);
        dropSelf(ModBlocks.BLOCKS_POWERED_FURNACE);
        dropSelf(ModBlocks.BASIC_CASING);
        dropSelf(ModBlocks.BIG_SMELTER);

        dropSelf(ModBlocks.METAL_BLOCKS);
    }

    private void dropSelf(DeferredBlock<?> block) {
        dropSelf(block.get());
    }

    private <T extends Block> void dropSelf(Iterable<DeferredBlock<T>> blocks) {
        for (var block : blocks)
            dropSelf(block);
    }

    private void createOreLootTable(Block block, Item item, float min, float max) {
        final var enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);

        this.add(block,
                createSilkTouchDispatchTable(block,
                        applyExplosionDecay(block, LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                .apply(ApplyBonusCount.addUniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))
                        )
                )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value).toList();
    }
}