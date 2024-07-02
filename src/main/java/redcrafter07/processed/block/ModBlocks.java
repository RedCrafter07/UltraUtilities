package redcrafter07.processed.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.machine_abstractions.ProcessedTier;
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock;
import redcrafter07.processed.item.ModItems;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModBlocks {
    public static DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ProcessedMod.ID);

    // the returned ObjectHolderDelegate can be used as a property delegate
    // this is automatically registered by the deferred registry at the correct times
    public static final DeferredBlock<?> BLITZ_ORE = registerBlock("blitz_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).explosionResistance(1200f)));
    public static final DeferredBlock<?> FLUID_TANK = registerBlock("fluid_tank", FluidTankBlock::new);
    public static final Set<DeferredBlock<PoweredFurnaceBlock>> BLOCKS_POWERED_FURNACE = registerTieredBlock("powered_furnace", ProcessedTier.TIERS, PoweredFurnaceBlock::new);

    private static <T extends TieredProcessedBlock> Set<DeferredBlock<T>> registerTieredBlock(
            String id,
            List<ProcessedTier> tiers,
            TieredBlockProvider<T> block
    ) {
        return tiers.stream().map((tier) -> {
            final DeferredBlock<T> regBlock = BLOCKS.register(id + "_" + tier.named(), () -> block.provide(tier));
            ModItems.registerItem(id + "_" + tier.named(), () -> new TieredModBlockItem(regBlock.get(), new Item.Properties()));
            return regBlock;
        }).collect(Collectors.toSet());
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String id, Supplier<T> block) {
        final var regBlock = BLOCKS.register(id, block);

        ModItems.registerItem(id, () -> new ModBlockItem(regBlock.get(), new Item.Properties(), id));

        return regBlock;
    }

    @FunctionalInterface
    interface TieredBlockProvider<T> {
        T provide(ProcessedTier tier);
    }
}