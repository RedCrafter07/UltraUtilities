package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class SimpleEnergyStore extends
        IProcessedEnergyHandler<CompoundTag> {
    private int capacity;
    private final int maxReceive;
    private final int maxExtract;
    private int energy;

    public SimpleEnergyStore(
            int capacity,
            int maxReceive,
            int maxExtract,
            int energy
    ) {
        super();
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = energy;
    }

    public SimpleEnergyStore(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public SimpleEnergyStore(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) energy += energyReceived;
        setChanged();
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) energy -= energyExtracted;
        setChanged();
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    @Override
    public @NotNull CompoundTag serializeNBT(Provider provider) {
        var tag = new CompoundTag();
        tag.putInt("energy", energy);
        return tag;
    }

    @Override
    public void deserializeNBT(Provider provider, CompoundTag nbt) {
        energy = nbt.getInt("energy");
    }

    @Override
    public void setEnergyStored(int energy) {
        this.energy = energy;
        setChanged();
    }

    @Override
    public void setMaxEnergyStored(int maxEnergy) {
        capacity = maxEnergy;
        setChanged();
    }
}