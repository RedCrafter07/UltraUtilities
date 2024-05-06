package redcrafter07.processed.events

import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.*

@Mod.EventBusSubscriber(modid = ProcessedMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object Capabilities {
    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        val blocks = ModTileEntities.BLOCK_TYPES.entries.map { entry -> entry.get().validBlocks }.flatten().toTypedArray()

        event.registerBlock(
            Capabilities.ItemHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is ItemCapableBlockEntity) blockEntity.itemCapabilityForSide(translateDirection(side, state), state)
                else null
            },
            *blocks
        )
        event.registerBlock(
            Capabilities.FluidHandler.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is FluidCapableBlockEntity) blockEntity.fluidCapabilityForSide(translateDirection(side, state), state)
                else null
            },
            *blocks
        )
        event.registerBlock(
            Capabilities.EnergyStorage.BLOCK,
            { _, _, state, blockEntity, side ->
                if (blockEntity is EnergyCapableBlockEntity) blockEntity.energyCapabilityForSide(translateDirection(side, state), state)
                else null
            },
            *blocks
        )

//        event.registerBlock(
//            Capabilities.ItemHandler.BLOCK,
//            { _, _, _, blockEntity, _ -> if (blockEntity !is PoweredFurnaceBlockEntity) null; else blockEntity.itemHandler },
//            *blocks(ModBlocks.BLOCKS_POWERED_FURNACE)
//        )
    }

    private val facingProperties =
        listOf(DirectionalBlock.FACING, HorizontalDirectionalBlock.FACING, BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING)

    private fun translateDirection(direction: Direction, state: BlockState): BlockSide {
        for (property in facingProperties) {
            if (state.hasProperty(property)) {
                val facing = state.getValue(property)
                return BlockSide.getFacing(facing, direction)
            }
        }
        return BlockSide.fromDirection(direction)
    }
}