package redcrafter07.processed.events;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.ModBlocks;
import redcrafter07.processed.item.ModItems;
import redcrafter07.processed.materials.MaterialBlock;
import redcrafter07.processed.materials.MaterialBlockItem;
import redcrafter07.processed.materials.MaterialItem;

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterColorEvent {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(new ItemColorProvider(), ModItems.DUST_ITEMS.toArray(DeferredItem[]::new));
        event.register(new ItemColorProvider(), ModItems.INGOT_ITEMS.toArray(DeferredItem[]::new));
        event.register(new ItemColorProvider(), ModItems.NUGGET_ITEMS.toArray(DeferredItem[]::new));
        event.register(new ItemColorProvider(), ModItems.RAW_ITEMS.toArray(DeferredItem[]::new));
        event.register(new ItemColorProvider(), ModBlocks.MATERIAL_BLOCK_ITEMS.toArray(DeferredItem[]::new));
    }

    private static class ItemColorProvider implements ItemColor {
        @Override
        public int getColor(ItemStack stack, int tintIndex) {
            return switch (stack.getItem()) {
                case ItemColor coloredItem -> coloredItem.getColor(stack, tintIndex);
                case MaterialItem materialItem -> materialItem.getMaterial().color();
                case MaterialBlockItem materialItem -> materialItem.getMaterial().color();
                default -> 0xFFFFFF;
            };
        }
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(new BlockColorProvider(), ModBlocks.METAL_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new));
        event.register(new BlockColorProvider(), ModBlocks.STONE_ORE_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new));
    }

    private static class BlockColorProvider implements BlockColor {
        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
            ProcessedMod.LOGGER.info("BlockColorProvider: {}", state.getBlock());
            return switch (state.getBlock()) {
                case BlockColor coloredBlock -> coloredBlock.getColor(state, level, pos, tintIndex);
                case MaterialBlock materialBlock -> materialBlock.getMaterial().color();
                default -> 0xFFFFFF;
            };
        }
    }
}
