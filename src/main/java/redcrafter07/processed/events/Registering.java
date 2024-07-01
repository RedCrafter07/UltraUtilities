package redcrafter07.processed.events;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.machine_abstractions.BlockSide;
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity;
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity;
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity;
import redcrafter07.processed.block.tile_entities.ModTileEntities;
import redcrafter07.processed.gui.GenericMachineMenuScreen;
import redcrafter07.processed.gui.ModMenuTypes;
import redcrafter07.processed.network.IOChangePacket;
import redcrafter07.processed.network.WrenchModeChangePacket;

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
public class Registering {
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        final var blocks = ModTileEntities.BLOCK_TYPES.getEntries().stream().flatMap(entry -> entry.get().getValidBlocks().stream()).toArray(Block[]::new);

        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    if (side != null && blockEntity instanceof ItemCapableBlockEntity itemCapableBlockEntity)
                        return itemCapableBlockEntity.itemCapabilityForSide(BlockSide.translateDirection(side, state), state);
                    return null;
                },
                blocks
        );

        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    if (side != null && blockEntity instanceof FluidCapableBlockEntity itemCapableBlockEntity)
                        return itemCapableBlockEntity.fluidCapabilityForSide(BlockSide.translateDirection(side, state), state);
                    return null;
                },
                blocks
        );
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    if (side != null && blockEntity instanceof EnergyCapableBlockEntity itemCapableBlockEntity)
                        return itemCapableBlockEntity.energyCapabilityForSide(BlockSide.translateDirection(side, state), state);
                    return null;
                },
                blocks
        );
    }

    @SubscribeEvent
    public static void registerNetworkHandlers(RegisterPayloadHandlersEvent event) {
        final var register = event.registrar(ProcessedMod.ID);
        ProcessedMod.LOGGER.info("Registering packets!");

        register.playToServer(
                WrenchModeChangePacket.TYPE,
                WrenchModeChangePacket.CODEC,
                WrenchModeChangePacket::handleServer
        );

        register.playToServer(
                IOChangePacket.TYPE,
                IOChangePacket.CODEC,
                IOChangePacket::handleServer
        );
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(
                ModMenuTypes.POWERED_FURNACE_MENU.get(),
                GenericMachineMenuScreen::new
        );
    }
}