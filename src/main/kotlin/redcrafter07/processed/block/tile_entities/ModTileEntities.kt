package redcrafter07.processed.block.tile_entities

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import java.util.function.Supplier

object ModTileEntities {
    val BLOCK_TYPES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProcessedMod.ID)

    val POWERED_FURNACE =
        register("powered_furnace", ::PoweredFurnaceBlockEntity, *ModBlocks.BLOCKS_POWERED_FURNACE.toTypedArray())

    private fun <T : BlockEntity> register(
        name: String,
        blockEntity: BlockEntitySupplier<T>,
        vararg blocks: DeferredBlock<*>,
    ): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> {
        if (blocks.isEmpty()) throw IllegalStateException("Processed: registering blockEntity without blocks for said blockEntity")
        return BLOCK_TYPES.register(
            name,
            Supplier {
                BlockEntityType.Builder.of(blockEntity, *blocks.map(DeferredBlock<*>::get).toTypedArray()).build(null)
            })
    }
}