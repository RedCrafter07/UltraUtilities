package redcrafter07.processed.block.tile_entities.capabilities

class EmptyEnergyStorage : IEnergyStorageModifiable {
    override fun setEnergyStored(energy: Int) {}
    override fun setMaxEnergyStored(maxEnergy: Int) {}
    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        return 0
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        return 0
    }

    override fun getEnergyStored(): Int {
        return 0
    }

    override fun getMaxEnergyStored(): Int {
        return 0
    }

    override fun canExtract(): Boolean {
        return false
    }

    override fun canReceive(): Boolean {
        return false
    }
}
