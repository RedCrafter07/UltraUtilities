package redcrafter07.processed.events

import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredBlock
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity

@Mod.EventBusSubscriber(modid = ProcessedMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object Capabilities {
    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlock(
            Capabilities.ItemHandler.BLOCK,
            { _, _, _, blockEntity, _ -> if (blockEntity !is PoweredFurnaceBlockEntity) null; else blockEntity.itemHandler },
            *blocks(ModBlocks.BLOCKS_POWERED_FURNACE)
        )
    }

    fun blocks(blocks: Set<DeferredBlock<*>>): Array<Block> {
        return blocks.map { block -> block.get() }.toTypedArray()
    }
}