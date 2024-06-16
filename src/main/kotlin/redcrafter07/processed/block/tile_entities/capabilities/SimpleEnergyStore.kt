package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import kotlin.math.min

class SimpleEnergyStore(protected var capacity: Int, protected val maxReceive: Int, protected val maxExtract: Int, protected var energy: Int) :
    IProcessedEnergyHandler<CompoundTag> {

    constructor(capacity: Int) : this(capacity, capacity, 0, 0)
    constructor(capacity: Int, maxReceive: Int, maxExtract: Int) : this(capacity, maxReceive, maxExtract, 0)


    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        if (!canReceive()) return 0

        val energyReceived = min((capacity - energy).toDouble(), min(this.maxReceive.toDouble(), maxReceive.toDouble()))
            .toInt()
        if (!simulate) energy += energyReceived
        return energyReceived
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        if (!canExtract()) return 0

        val energyExtracted =
            min(energy.toDouble(), min(this.maxExtract.toDouble(), maxExtract.toDouble()))
                .toInt()
        if (!simulate) energy -= energyExtracted
        return energyExtracted
    }

    override fun getEnergyStored(): Int {
        return energy
    }

    override fun getMaxEnergyStored(): Int {
        return capacity
    }

    override fun canExtract(): Boolean {
        return this.maxExtract > 0
    }

    override fun canReceive(): Boolean {
        return this.maxReceive > 0
    }

    override fun serializeNBT(provider: Provider): CompoundTag {
        val tag = CompoundTag()
        tag.putInt("energy", energy)
        return tag
    }

    override fun deserializeNBT(provider: Provider, tag: CompoundTag) {
        this.energy = tag.getInt("energy")
    }

    override fun setEnergyStored(energy: Int) {
        this.energy = energy
    }

    override fun setMaxEnergyStored(maxEnergy: Int) {
        this.capacity = maxEnergy
    }
}