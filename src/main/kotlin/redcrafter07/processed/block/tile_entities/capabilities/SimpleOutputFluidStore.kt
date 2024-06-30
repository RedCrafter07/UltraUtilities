package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class SimpleOutputFluidStore(fluids: NonNullList<FluidStack>, capacity: Int) :
    SimpleInputFluidStore(fluids, capacity) {
    constructor(size: Int, capacity: Int) : this(NonNullList.withSize(size, FluidStack.EMPTY), capacity)

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        return stack.amount
    }

    override fun isFluidValid(slot: Int, stack: FluidStack): Boolean {
        return false
    }
}