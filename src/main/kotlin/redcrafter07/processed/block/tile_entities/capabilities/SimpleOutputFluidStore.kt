package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class SimpleOutputFluidStore(slots: Int, capacity: Int) : SimpleInputFluidStore(slots, capacity) {
    constructor() : this(1, 1000)
    constructor(fluids: NonNullList<FluidStack>, capacity: Int) : this(fluids.size, capacity) {
        for (i in 0..<fluids.size) this.setStackInTank(i, fluids[i].copy())
    }

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        return stack.amount
    }

    override fun isFluidValid(slot: Int, stack: FluidStack): Boolean {
        return false
    }
}