package redcrafter07.processed.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.machine_abstractions.ProcessedTier;
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock;
import redcrafter07.processed.item.ModItems;
import redcrafter07.processed.materials.Material;
import redcrafter07.processed.materials.MaterialBlock;
import redcrafter07.processed.materials.MaterialBlockItem;
import redcrafter07.processed.materials.Materials;
import redcrafter07.processed.multiblock.CasingBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModBlocks {
    public static DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ProcessedMod.ID);

    // the returned ObjectHolderDelegate can be used as a property delegate
    // this is automatically registered by the deferred registry at the correct times
    public static final DeferredBlock<?> BLITZ_ORE = registerBlock("blitz_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).explosionResistance(1200f)));
    public static final DeferredBlock<?> FLUID_TANK = registerBlock("fluid_tank", FluidTankBlock::new);
    public static final Set<DeferredBlock<PoweredFurnaceBlock>> BLOCKS_POWERED_FURNACE = registerTieredBlock("powered_furnace", ProcessedTier.TIERS, PoweredFurnaceBlock::new);
    public static final DeferredBlock<?> BASIC_CASING = registerBlock("basic_casing", () -> new CasingBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<?> BIG_SMELTER = registerBlock("big_smelter", BigSmelterBlock::new);

    public static List<DeferredItem<MaterialBlockItem>> MATERIAL_BLOCK_ITEMS = new ArrayList<>();
    public static List<DeferredBlock<MaterialBlock>> METAL_BLOCKS = registerMaterialBlock(Materials.MATERIALS, Material::getMetalBlockPath, MaterialBlock.MetalBlock::new, MaterialBlockItem.MetalBlock::new);
    public static List<DeferredBlock<MaterialBlock>> STONE_ORE_BLOCKS = registerMaterialBlock(Materials.MATERIALS, Material::getOreBlockPath, MaterialBlock.OreBlock::new, MaterialBlockItem.OreBlock::new);

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

    public static <T extends MaterialBlock> List<DeferredBlock<T>> registerMaterialBlock(List<Material> materials, Function<Material, String> nameSupplier, Function<Material, T> blockConstructor, BiFunction<Block, Material, MaterialBlockItem> itemConstructor) {
        ArrayList<DeferredBlock<T>> list = new ArrayList<>();

        for (Material material : materials) {
            String name = nameSupplier.apply(material);
            final var regBlock = BLOCKS.register(name, () -> blockConstructor.apply(material));
            list.add(regBlock);
            MATERIAL_BLOCK_ITEMS.add(ModItems.registerItem(name, () -> itemConstructor.apply(regBlock.get(), material)));
        }

        return list;
    }

    @FunctionalInterface
    interface TieredBlockProvider<T> {
        T provide(ProcessedTier tier);
    }
}