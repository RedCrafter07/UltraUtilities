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

    val PIPE_BLOCK_ENTITY = register("pipe_block_entity") { BlockEntityType.Builder.of(::PipeBlockEntity, ModBlocks.BLOCK_PIPE.get()) };
    
    fun <T: BlockEntity> register(name: String, supplier: Supplier<BlockEntityType.Builder<T>>): DeferredHolder<BlockEntityType<*>, BlockEntityType<T>> {
        return BLOCK_TYPES.register(name, Supplier { supplier.get().build(null) });
    }
}