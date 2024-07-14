package redcrafter07.processed.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.block.machine_abstractions.BlockSide;
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity;
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity;
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity;
import redcrafter07.processed.block.tile_entities.FluidTankBlockEntity;
import redcrafter07.processed.block.tile_entities.ModTileEntities;
import redcrafter07.processed.gui.GenericMachineMenuScreen;
import redcrafter07.processed.gui.ModMenuTypes;
import redcrafter07.processed.network.IOChangePacket;
import redcrafter07.processed.network.MultiblockDestroyPacket;
import redcrafter07.processed.network.WrenchModeChangePacket;

import java.util.Arrays;

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
public class Registering {
    private static boolean blockEntityIs(Block block, Class<?> clazz) {
        if (block instanceof EntityBlock entityBlock) {
            try {
                final BlockEntity be = entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
                return clazz.isInstance(be);
            } catch (SecurityException e) {
                return false;
            }
        }
        return false;
    }

    private static boolean isItemCapable(Block block) {
        return blockEntityIs(block, ItemCapableBlockEntity.class);
    }

    private static boolean isFluidCapable(Block block) {
        return blockEntityIs(block, FluidCapableBlockEntity.class);
    }

    private static boolean isEnergyCapable(Block block) {
        return blockEntityIs(block, EnergyCapableBlockEntity.class);
    }

    private static Block[] itemCapable(Block[] block) {
        return Arrays.stream(block).filter(Registering::isItemCapable).toArray(Block[]::new);
    }

    private static Block[] fluidCapable(Block[] block) {
        return Arrays.stream(block).filter(Registering::isFluidCapable).toArray(Block[]::new);
    }

    private static Block[] energyCapable(Block[] block) {
        return Arrays.stream(block).filter(Registering::isEnergyCapable).toArray(Block[]::new);
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        final var blocks = ModTileEntities.BLOCK_TYPES.getEntries().stream().flatMap(entry -> entry.get().getValidBlocks().stream()).toArray(Block[]::new);

        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    if (blockEntity instanceof ItemCapableBlockEntity itemCapableBlockEntity) {
                        if (side == null) return itemCapableBlockEntity.itemCapabilityForSide(null, state);
                        return itemCapableBlockEntity.itemCapabilityForSide(BlockSide.translateDirection(side, state), state);
                    }
                    return null;
                },
                itemCapable(blocks)
        );

        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    if (blockEntity instanceof FluidCapableBlockEntity itemCapableBlockEntity) {
                        if (side == null) return itemCapableBlockEntity.fluidCapabilityForSide(null, state);
                        return itemCapableBlockEntity.fluidCapabilityForSide(BlockSide.translateDirection(side, state), state);
                    }
                    return null;
                },
                fluidCapable(blocks)
        );
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    if (blockEntity instanceof EnergyCapableBlockEntity itemCapableBlockEntity) {
                        if (side == null) return itemCapableBlockEntity.energyCapabilityForSide(null, state);
                        return itemCapableBlockEntity.energyCapabilityForSide(BlockSide.translateDirection(side, state), state);
                    }
                    return null;
                },
                energyCapable(blocks)
        );
    }

    @SubscribeEvent
    public static void registerNetworkHandlers(RegisterPayloadHandlersEvent event) {
        final var register = event.registrar(ProcessedMod.ID);

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

        register.playToClient(
                MultiblockDestroyPacket.TYPE,
                MultiblockDestroyPacket.CODEC,
                MultiblockDestroyPacket::handleClient
        );
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(
                ModMenuTypes.POWERED_FURNACE_MENU.get(),
                GenericMachineMenuScreen::new
        );
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModTileEntities.FLUID_TANK.get(), FluidTankBlockEntity.FluidTankEntityRenderer::new);
    }
}