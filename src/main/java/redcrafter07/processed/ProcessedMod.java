package redcrafter07.processed;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redcrafter07.processed.block.ModBlocks;
import redcrafter07.processed.block.tile_entities.ModTileEntities;
import redcrafter07.processed.gui.ModMenuTypes;
import redcrafter07.processed.item.ModDataComponents;
import redcrafter07.processed.item.ModItemGroup;
import redcrafter07.processed.item.ModItems;

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in neoforge.mods.toml.
 * <p>
 * An example for blocks is in the `blocks` package of this mod.
 */

@Mod(ProcessedMod.ID)
public class ProcessedMod {
    public static final String ID = "processed";

    // the logger for our mod
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    public ProcessedMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItemGroup.CREATIVE_MODE_TABS.register(modEventBus);
        ModTileEntities.BLOCK_TYPES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
    }
}