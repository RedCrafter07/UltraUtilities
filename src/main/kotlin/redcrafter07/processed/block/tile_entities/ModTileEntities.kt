package redcrafter07.processed.block.tile_entities

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import java.util.function.Supplier

object ModTileEntities {
    val BLOCK_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProcessedMod.ID);

    val PIPE_BLOCK_ENTITY = register("item_pipe_block_entity") { BlockEntityType.Builder.of(::ItemPipeBlockEntity, ModBlocks.BLOCK_ITEM_PIPE.get()) };
    val PIPE_PRESSURIZER_BLOCK_ENTITY = register("pipe_pressurizer_block_entity") { BlockEntityType.Builder.of(::PipePressurizerBlockEntity, ModBlocks.BLOCK_PIPE_PRESSURIZER.get()) };
    val MACHINE = register("machine") { BlockEntityType.Builder.of(::MachineBlockEntity, *ModBlocks.BLOCKS_MACHINE.map { block -> block.get() }.toTypedArray()) };
    
    fun <T: BlockEntity> register(name: String, supplier: Supplier<BlockEntityType.Builder<T>>): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> {
        return BLOCK_TYPES.register(name, Supplier { supplier.get().build(null) });
    }
}