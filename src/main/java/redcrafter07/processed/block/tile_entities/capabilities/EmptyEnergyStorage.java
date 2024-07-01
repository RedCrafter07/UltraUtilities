package redcrafter07.processed.block.tile_entities.capabilities;

public class EmptyEnergyStorage implements IEnergyStorageModifiable {
    @Override
    public void setEnergyStored(int energy) {}
    @Override
    public void setMaxEnergyStored(int maxEnergy) {}

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
