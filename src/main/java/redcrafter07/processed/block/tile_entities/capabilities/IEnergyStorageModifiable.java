package redcrafter07.processed.block.tile_entities.capabilities;

import net.neoforged.neoforge.energy.IEnergyStorage;

public interface IEnergyStorageModifiable extends IEnergyStorage {
    void setEnergyStored(int energy);
    void setMaxEnergyStored(int maxEnergy);
}