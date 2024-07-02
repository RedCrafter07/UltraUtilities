package redcrafter07.processed.block.tile_entities;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.ModBlocks;

import java.util.Arrays;

public final class ModTileEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProcessedMod.ID);

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE =
            register("powered_furnace", PoweredFurnaceBlockEntity::new, ModBlocks.BLOCKS_POWERED_FURNACE.toArray(DeferredBlock[]::new));
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTankBlockEntity>> FLUID_TANK =
            register("fluid_tank", FluidTankBlockEntity::new, ModBlocks.FLUID_TANK);

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
            String name,
            BlockEntityType.BlockEntitySupplier<T> blockEntity,
            DeferredBlock<?>... blocks
    ) {
        return BLOCK_TYPES.register(
                name,
                () -> BlockEntityType.Builder.of(blockEntity, Arrays.stream(blocks).map(DeferredBlock::get).toArray(Block[]::new)).build(null));
    }
}