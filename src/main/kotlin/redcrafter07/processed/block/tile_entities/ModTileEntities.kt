package redcrafter07.processed.block.tile_entities

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
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
    val BLOCK_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProcessedMod.ID)

    val PIPE_BLOCK_ENTITY = register(
        "item_pipe_block_entity", ::ItemPipeBlockEntity,
        ModBlocks.BLOCK_ITEM_PIPE.get()
    )
    val PIPE_PRESSURIZER_BLOCK_ENTITY =
        register("pipe_pressurizer_block_entity", ::PipePressurizerBlockEntity, ModBlocks.BLOCK_PIPE_PRESSURIZER.get())
    val MACHINE = register_blocks("powered_furnace", ::PoweredFurnaceBlockEntity, ModBlocks.BLOCKS_POWERED_FURNACE)

    fun <TEntity : BlockEntity> register_blocks(
        name: String,
        blockEntity: BlockEntitySupplier<TEntity>,
        blocks: Set<DeferredBlock<*>>
    ): DeferredHolder<BlockEntityType<*>, BlockEntityType<TEntity>> {
        return BLOCK_TYPES.register(
            name,
            Supplier {
                BlockEntityType.Builder.of(blockEntity, *blocks.map(DeferredBlock<*>::get).toTypedArray())
                    .build(null)
            })
    }

    fun <T : BlockEntity> register(
        name: String,
        blockEntity: BlockEntitySupplier<T>,
        block: Block,
    ): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> {
        return BLOCK_TYPES.register(name, Supplier { BlockEntityType.Builder.of(blockEntity, block).build(null) })
    }
}