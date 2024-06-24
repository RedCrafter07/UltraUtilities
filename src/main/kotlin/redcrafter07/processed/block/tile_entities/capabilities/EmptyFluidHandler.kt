package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class EmptyFluidHandler: IFluidHandlerModifiable {
    override fun setStackInTank(tank: Int, stack: FluidStack) {}

    override fun getStackInTank(tank: Int): FluidStack {
        return FluidStack.EMPTY
    }

    override fun getTanks(): Int {
        return 0
    }

    override fun getFluidInTank(p0: Int): FluidStack {
        return FluidStack.EMPTY
    }

    override fun getTankCapacity(p0: Int): Int {
        return 0
    }

    override fun isFluidValid(p0: Int, p1: FluidStack): Boolean {
        return true
    }

    override fun fill(p0: FluidStack, p1: IFluidHandler.FluidAction): Int {
        return 0
    }

    override fun drain(p0: FluidStack, p1: IFluidHandler.FluidAction): FluidStack {
        return FluidStack.EMPTY
    }

    override fun drain(p0: Int, p1: IFluidHandler.FluidAction): FluidStack {
        return FluidStack.EMPTY
    }
}