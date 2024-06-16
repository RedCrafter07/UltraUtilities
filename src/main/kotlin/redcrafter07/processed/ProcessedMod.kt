package redcrafter07.processed

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.gui.ModMenuTypes
import redcrafter07.processed.item.ModItemGroup
import redcrafter07.processed.item.ModItems
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */

@Mod(ProcessedMod.ID)
object ProcessedMod {
    const val ID = "processed"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    fun rl(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ID, path)
    }

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        // Register the KDeferredRegister to the mod-specific event bus
        ModBlocks.BLOCKS.register(MOD_BUS)
        ModItems.ITEMS.register(MOD_BUS)
        ModItemGroup.CREATIVE_MODE_TABS.register(MOD_BUS)
        ModTileEntities.BLOCK_TYPES.register(MOD_BUS)

        ModMenuTypes.MENUS.register(MOD_BUS)

        val obj = runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                "test"
            })
        MOD_BUS.addListener(::onCommonSetup)

        println(obj)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
//        MOD_BUS.register(Capabilities)
        LOGGER.log(Level.INFO, "Server starting...")
    }

    private fun onCommonSetup(event: FMLCommonSetupEvent) {
//        NeoForge.EVENT_BUS.register(WrenchHandler)
//        MOD_BUS.register(MenuScreens)
    }
}