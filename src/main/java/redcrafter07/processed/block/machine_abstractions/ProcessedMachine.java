package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import redcrafter07.processed.block.WrenchInteractableBlock;
import redcrafter07.processed.block.tile_entities.capabilities.*;
import redcrafter07.processed.gui.ConfigScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ProcessedMachine
        extends BlockEntity
        implements WrenchInteractableBlock, ItemCapableBlockEntity, EnergyCapableBlockEntity, FluidCapableBlockEntity,
        MenuProvider {


    public ProcessedMachine(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static final IItemHandlerModifiable EMPTY_ITEM_HANDLER = new EmptyItemHandler();
    public static final IFluidHandlerModifiable EMPTY_FLUID_HANDLER = new EmptyFluidHandler();
    public static final IEnergyStorageModifiable EMPTY_ENERGY_HANDLER = new EmptyEnergyStorage();

    private final ArrayList<IoState> sides = new ArrayList<>(
            List.of(IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None,
                    IoState.None)
    );

    private final CapabilityHandlers capabilityHandlers = new CapabilityHandlers(this);

    public IoState getSide(boolean itemOrFluid, BlockSide side) {
        return sides.get(itemOrFluid ? side.getId() : side.getId() + 6);
    }

    public void setSide(boolean itemOrFluid, BlockSide side, IoState value) {
        sides.set(itemOrFluid ? side.getId() : side.getId() + 6, value);
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        final var byteArray = tag.getByteArray("io_states");
        for (int i = 0; i < 12; ++i)
            sides.set(i, byteArray.length <= i ? IoState.None : IoState.BY_ID.apply(byteArray[i]));

        capabilityHandlers.deserializeNBT(registries, tag.getCompound("capability_handlers"));
        invalidateCapabilities();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putByteArray("io_states", sides.stream().map((side) -> (byte) side.getId()).toList());
        tag.put("capability_handlers", capabilityHandlers.serializeNBT(registries));
    }

    public void sync() {
        if (this.level instanceof ServerLevel) {
            final BlockState state = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            setChanged();
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onWrenchUse(UseOnContext context, BlockState state) {
        final var me = context.getLevel().getBlockEntity(context.getClickedPos());
        if (me instanceof ProcessedMachine) {
            if (getSupportedFluidHandlers().size() < 2 && getSupportedItemHandlers().size() < 2) return;
            if (context.getLevel().isClientSide)
                Minecraft.getInstance().setScreen(new ConfigScreen(this, context.getClickedPos()));
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public IEnergyStorage energyCapabilityForSide(BlockSide side, BlockState state) {
        return capabilityHandlers.getEnergyStore();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public IItemHandler itemCapabilityForSide(BlockSide side, BlockState state) {
        return capabilityHandlers.getItemHandlerForState(getSide(true, side));
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public IFluidHandler fluidCapabilityForSide(BlockSide side, BlockState state) {
        return capabilityHandlers.getFluidHandlerForState(getSide(false, side));
    }

    public void clientTick(Level level, BlockPos pos, BlockState state) {
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
    }

    public void commonTick(Level level, BlockPos pos, BlockState state) {
    }


    /// ###########################################
    /// #  E N E R G Y   C A P A B I L I T I E S  #
    /// ###########################################
    protected void useEnergyCapabilityBidirectional(int capacity, int transferRate) {
        capabilityHandlers.setEnergyStore(new SimpleEnergyStore(capacity, transferRate, transferRate, 0));
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void useEnergyCapability(int capacity, int maxInsert) {
        capabilityHandlers.setEnergyStore(new SimpleEnergyStore(capacity, maxInsert, 0, 0));
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void useOutputEnergyCapability(int capacity, int maxExtract) {
        capabilityHandlers.setEnergyStore(new SimpleEnergyStore(capacity, 0, maxExtract, 0));
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void useEnergyCapability(@Nullable IProcessedEnergyHandler<CompoundTag> capability) {
        this.capabilityHandlers.setEnergyStore(capability);
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void stopEnergyCapability() {
        this.capabilityHandlers.setEnergyStore(null);
        this.invalidateCapabilities();
        this.setChanged();
    }

    /**
     * Returns the [IEnergyStorage] for the state.
     * If no Capability for that state was registered, it returns an empty [IEnergyStorage]
     *
     * @see EmptyEnergyStorage
     **/
    public IEnergyStorageModifiable getEnergyCapability() {
        final var capability = this.capabilityHandlers.getEnergyStore();
        if (capability == null) return EMPTY_ENERGY_HANDLER;
        return capability;
    }

    public boolean hasEnergyCapability() {
        return capabilityHandlers.getEnergyStore() != null;
    }

    public @Nullable IEnergyStorageModifiable getEnergyHandlerOrNull() {
        return capabilityHandlers.getEnergyStore();
    }

    /**
     * Tries to use some power
     * @param amount The amount of power you want to use
     * @return Returns if we managed to use that much amount of power (if it returns false, we don't have enough power)
     */
    protected boolean usePower(int amount) {
        final var energyStore = capabilityHandlers.getEnergyStore();
        if (energyStore == null) return false;
        if (energyStore.getEnergyStored() < amount) return false;
        energyStore.setEnergyStored(energyStore.getEnergyStored() - amount);
        return true;
    }

    /// #######################################
    /// #  I T E M   C A P A B I L I T I E S  #
    /// #######################################
    protected void useItemCapability(IoState state) {
        if (state == IoState.None) return;
        capabilityHandlers.setItemHandlerForState(
                state,
                state == IoState.Output ? new SimpleOutputItemStore(1) : new SimpleInputItemStore(1)
        );
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void useItemCapability(IoState state, int size) {
        if (state == IoState.None) return;
        this.capabilityHandlers.setItemHandlerForState(
                state,
                (state == IoState.Output) ? new SimpleOutputItemStore(size) : new SimpleInputItemStore(size)
        );
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void useItemCapability(IoState state, @Nullable IProcessedItemHandler<CompoundTag> capability) {
        if (state == IoState.None) return;
        this.capabilityHandlers.setItemHandlerForState(state, capability);
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void stopItemCapability(IoState state) {
        if (state == IoState.None) return;
        this.capabilityHandlers.setItemHandlerForState(state, null);
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void stopItemCapability() {
        this.capabilityHandlers.setItemHandlerForState(IoState.Input, null);
        this.capabilityHandlers.setItemHandlerForState(IoState.Output, null);
        this.capabilityHandlers.setItemHandlerForState(IoState.Additional, null);
        this.capabilityHandlers.setItemHandlerForState(IoState.Extra, null);
        this.invalidateCapabilities();
        this.setChanged();
    }


    /**
     * Returns the [IItemHandlerModifiable] for the state.
     * If no Capability for that state was registered, it returns an empty [IItemHandlerModifiable]
     *
     * @see EmptyItemHandler
     **/
    public IItemHandlerModifiable getItemCapability(IoState state) {
        final var capability = capabilityHandlers.getItemHandlerForState(state);
        if (capability == null) return EMPTY_ITEM_HANDLER;
        return capability;
    }

    public boolean hasItemCapability(IoState state) {
        return capabilityHandlers.getItemHandlerForState(state) != null;
    }

    public @Nullable IItemHandlerModifiable getItemCapabilityOrNull(IoState state) {
        return capabilityHandlers.getItemHandlerForState(state);
    }

    public IItemHandlerModifiable getInputItemHandler() {
        return getItemCapability(IoState.Input);
    }

    public IItemHandlerModifiable getOutputItemHandler() {
        return getItemCapability(IoState.Output);
    }

    public IItemHandlerModifiable getAdditionalItemHandler() {
        return getItemCapability(IoState.Additional);
    }

    public IItemHandlerModifiable getExtraItemHandler() {
        return getItemCapability(IoState.Extra);
    }

    public Set<IoState> getSupportedItemHandlers() {
        return capabilityHandlers.supportedItemHandlers;
    }

    private void dropItemForState(IoState state) {
        final var level = getLevel();
        if (level == null) return;

        final var capability = this.capabilityHandlers.getItemHandlerForState(state);
        if (capability == null) return;
        if (capability.getSlots() < 1) return;
        final var inventory = new SimpleContainer(capability.getSlots());
        for (int i = 0; i < capability.getSlots(); ++i) inventory.setItem(i, capability.getStackInSlot(i));
        Containers.dropContents(level, this.worldPosition, inventory);

    }

    public void dropItems() {
        dropItemForState(IoState.Input);
        dropItemForState(IoState.Output);
        dropItemForState(IoState.Additional);
        dropItemForState(IoState.Extra);
    }

    /// #########################################
    /// #  F L U I D   C A P A B I L I T I E S  #
    /// #########################################
    protected void useFluidCapability(IoState state, int capacity) {
        useFluidCapability(state, capacity, 1);
    }

    protected void useFluidCapability(IoState state, int capacity, int slots) {
        if (state == IoState.None) return;
        this.capabilityHandlers.setFluidHandlerForState(
                state,
                ((state == IoState.Output) ? new SimpleOutputFluidStore(
                        slots,
                        capacity
                ) : new SimpleInputFluidStore(
                        slots,
                        capacity
                ))
        );
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void useFluidCapability(IoState state, @Nullable IProcessedFluidHandler<CompoundTag> capability) {
        if (state == IoState.None) return;
        this.capabilityHandlers.setFluidHandlerForState(state, capability);
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void stopFluidCapability(IoState state) {
        if (state == IoState.None) return;
        this.capabilityHandlers.setFluidHandlerForState(state, null);
        this.invalidateCapabilities();
        this.setChanged();
    }

    protected void stopFluidCapability() {
        this.capabilityHandlers.setFluidHandlerForState(IoState.Input, null);
        this.capabilityHandlers.setFluidHandlerForState(IoState.Output, null);
        this.capabilityHandlers.setFluidHandlerForState(IoState.Additional, null);
        this.capabilityHandlers.setFluidHandlerForState(IoState.Extra, null);
        this.invalidateCapabilities();
        this.setChanged();
    }

    /**
     * Returns the [IFluidHandlerModifiable] for the state.
     * If no Capability for that state was registered, it returns an empty [IFluidHandlerModifiable]
     *
     * @see EmptyFluidHandler
     **/
    protected IFluidHandlerModifiable getFluidCapability(IoState state) {
        final var capability = capabilityHandlers.getFluidHandlerForState(state);
        if (capability == null) return EMPTY_FLUID_HANDLER;
        return capability;
    }

    protected boolean hasFluidCapability(IoState state) {
        return capabilityHandlers.getFluidHandlerForState(state) != null;
    }

    public @Nullable IFluidHandlerModifiable getFluidCapabilityOrNull(IoState state) {
        return capabilityHandlers.getFluidHandlerForState(state);
    }

    public IFluidHandlerModifiable getInputFluidHandler() {
        return getFluidCapability(IoState.Input);
    }

    public IFluidHandlerModifiable getOutputFluidHandler() {
        return getFluidCapability(IoState.Output);
    }

    public IFluidHandlerModifiable getAdditionalFluidHandler() {
        return getFluidCapability(IoState.Additional);
    }

    public IFluidHandlerModifiable getExtraFluidHandler() {
        return getFluidCapability(IoState.Extra);
    }

    public Set<IoState> getSupportedFluidHandlers() {
        return capabilityHandlers.supportedFluidHandlers;
    }

    public static class CapabilityHandlers implements INBTSerializable<CompoundTag> {
        private final ProcessedMachine attachedMachine;

        public CapabilityHandlers(ProcessedMachine attachedMachine) {
            this.attachedMachine = attachedMachine;
        }

        public Set<IoState> supportedItemHandlers = new HashSet<>(List.of(IoState.None));
        Set<IoState> supportedFluidHandlers = new HashSet<>(List.of(IoState.None));

        private @Nullable IProcessedEnergyHandler<CompoundTag> energyStore = null;

        public @Nullable IProcessedEnergyHandler<CompoundTag> getEnergyStore() {
            return energyStore;
        }

        public void setEnergyStore(@Nullable IProcessedEnergyHandler<CompoundTag> newEnergyStore) {
            if (newEnergyStore != null) newEnergyStore.setOnChange(attachedMachine::sync);
            energyStore = newEnergyStore;
        }

        private @Nullable IProcessedItemHandler<CompoundTag> inputItemHandler = null;
        private @Nullable IProcessedItemHandler<CompoundTag> outputItemHandler = null;
        private @Nullable IProcessedItemHandler<CompoundTag> additionalItemHandler = null;
        private @Nullable IProcessedItemHandler<CompoundTag> extraItemHandler = null;
        private final IItemHandlerModifiable mergedIoItemCapabilityHandler = new MergedIoItemCapability(this);

        private @Nullable IProcessedFluidHandler<CompoundTag> inputFluidHandler = null;
        private @Nullable IProcessedFluidHandler<CompoundTag> outputFluidHandler = null;
        private @Nullable IProcessedFluidHandler<CompoundTag> additionalFluidHandler = null;
        private @Nullable IProcessedFluidHandler<CompoundTag> extraFluidHandler = null;
        private final IFluidHandlerModifiable mergedIoFluidCapabilityHandler = new MergedIoFluidCapability(this);


        public @Nullable IItemHandlerModifiable getItemHandlerForState(IoState state) {
            return switch (state) {
                case IoState.None -> null;
                case IoState.Input -> inputItemHandler;
                case IoState.Output -> outputItemHandler;
                case IoState.InputOutput ->
                        (inputItemHandler == null && outputItemHandler == null) ? null : mergedIoItemCapabilityHandler;
                case IoState.Additional -> additionalItemHandler;
                case IoState.Extra -> extraItemHandler;
            };
        }

        public void setItemHandlerForState(IoState state, @Nullable IProcessedItemHandler<CompoundTag> handler) {
            if (handler != null) handler.setOnChange(attachedMachine::sync);

            if (handler != null) {
                supportedItemHandlers.add(state);
                if (
                        (state == IoState.Input && supportedItemHandlers.contains(IoState.Output))
                                || (state == IoState.Output && supportedItemHandlers.contains(IoState.Input))
                )
                    supportedItemHandlers.add(IoState.InputOutput);
            } else if (state != IoState.None) {
                supportedItemHandlers.remove(state);
                if (state == IoState.Input || state == IoState.Output) supportedItemHandlers.remove(IoState.InputOutput);
            }

            switch (state) {
                case IoState.None, IoState.InputOutput -> {
                }
                case IoState.Input -> inputItemHandler = handler;
                case IoState.Output -> outputItemHandler = handler;
                case IoState.Additional -> additionalItemHandler = handler;
                case IoState.Extra -> extraItemHandler = handler;
            }
        }


        public @Nullable IFluidHandlerModifiable getFluidHandlerForState(IoState state) {
            return switch (state) {
                case IoState.None -> null;
                case IoState.Input -> inputFluidHandler;
                case IoState.Output -> outputFluidHandler;
                case IoState.InputOutput ->
                        (inputFluidHandler == null && outputFluidHandler == null) ? null : mergedIoFluidCapabilityHandler;
                case IoState.Additional -> additionalFluidHandler;
                case IoState.Extra -> extraFluidHandler;
            };
        }

        public void setFluidHandlerForState(IoState state, @Nullable IProcessedFluidHandler<CompoundTag> handler) {
            if (handler != null) handler.setOnChange(attachedMachine::sync);

            if (handler != null) {
                supportedFluidHandlers.add(state);
                if (
                        (state == IoState.Input && supportedFluidHandlers.contains(IoState.Output))
                                || (state == IoState.Output && supportedFluidHandlers.contains(IoState.Input))
                )
                    supportedFluidHandlers.add(IoState.InputOutput);
            } else if (state != IoState.None) {
                supportedFluidHandlers.remove(state);
                if (state == IoState.Input || state == IoState.Output)
                    supportedFluidHandlers.remove(IoState.InputOutput);
            }

            switch (state) {
                case IoState.None, IoState.InputOutput -> {
                }
                case IoState.Input -> inputFluidHandler = handler;
                case IoState.Output -> outputFluidHandler = handler;
                case IoState.Additional -> additionalFluidHandler = handler;
                case IoState.Extra -> extraFluidHandler = handler;
            }
        }

        @Override
        public CompoundTag serializeNBT(Provider provider) {
            final var tag = new CompoundTag();

            // energy
            var energyStoreNbt = energyStore != null ? energyStore.serializeNBT(provider) : null;

            if (energyStoreNbt != null) tag.put("energyStore", energyStoreNbt);


            // items
            var inputItemNbt = inputItemHandler != null ? inputItemHandler.serializeNBT(provider) : null;
            var outputItemNbt = outputItemHandler != null ? outputItemHandler.serializeNBT(provider) : null;
            var additionalItemNbt = additionalItemHandler != null ? additionalItemHandler.serializeNBT(provider) : null;
            var auxiliaryItemNbt = extraItemHandler != null ? extraItemHandler.serializeNBT(provider) : null;

            if (inputItemNbt != null) tag.put("inputItemNbt", inputItemNbt);
            if (outputItemNbt != null) tag.put("outputItemNbt", outputItemNbt);
            if (additionalItemNbt != null) tag.put("additionalItemNbt", additionalItemNbt);
            if (auxiliaryItemNbt != null) tag.put("auxiliaryItemNbt", auxiliaryItemNbt);


            // fluids
            var inputFluidNbt = inputFluidHandler != null ? inputFluidHandler.serializeNBT(provider) : null;
            var outputFluidNbt = outputFluidHandler != null ? outputFluidHandler.serializeNBT(provider) : null;
            var additionalFluidNbt = additionalFluidHandler != null ? additionalFluidHandler.serializeNBT(provider) : null;
            var auxiliaryFluidNbt = extraFluidHandler != null ? extraFluidHandler.serializeNBT(provider) : null;

            if (inputFluidNbt != null) tag.put("inputFluidNbt", inputFluidNbt);
            if (outputFluidNbt != null) tag.put("outputFluidNbt", outputFluidNbt);
            if (additionalFluidNbt != null) tag.put("additionalFluidNbt", additionalFluidNbt);
            if (auxiliaryFluidNbt != null) tag.put("auxiliaryFluidNbt", auxiliaryFluidNbt);

            return tag;
        }

        @Override
        public void deserializeNBT(Provider provider, CompoundTag tag) {
            // energy
            if (tag.contains("energyStore", 10) && energyStore != null)
                energyStore.deserializeNBT(provider, tag.getCompound("energyStore"));

            // items
            if (tag.contains("inputItemNbt", 10) && inputItemHandler != null)
                inputItemHandler.deserializeNBT(provider, tag.getCompound("inputItemNbt"));
            if (tag.contains("outputItemNbt", 10) && outputItemHandler != null)
                outputItemHandler.deserializeNBT(provider, tag.getCompound("outputItemNbt"));
            if (tag.contains("additionalItemNbt", 10) && additionalItemHandler != null)
                additionalItemHandler.deserializeNBT(provider, tag.getCompound("additionalItemNbt"));
            if (tag.contains("auxiliaryItemNbt", 10) && extraItemHandler != null)
                extraItemHandler.deserializeNBT(provider, tag.getCompound("auxiliaryItemNbt"));

            // fluids
            if (tag.contains("inputFluidNbt", 10) && inputFluidHandler != null)
                inputFluidHandler.deserializeNBT(provider, tag.getCompound("inputFluidNbt"));
            if (tag.contains("outputFluidNbt", 10) && outputFluidHandler != null)
                outputFluidHandler.deserializeNBT(provider, tag.getCompound("outputFluidNbt"));
            if (tag.contains("additionalFluidNbt", 10) && additionalFluidHandler != null)
                additionalFluidHandler.deserializeNBT(provider, tag.getCompound("additionalFluidNbt"));
            if (tag.contains("auxiliaryFluidNbt", 10) && extraFluidHandler != null)
                extraFluidHandler.deserializeNBT(provider, tag.getCompound("auxiliaryFluidNbt"));
        }
    }
}

