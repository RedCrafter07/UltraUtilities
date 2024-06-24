package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

interface IFluidHandlerModifiable : IFluidHandler {
    fun setStackInTank(tank: Int, stack: FluidStack)
    fun getStackInTank(tank: Int): FluidStack
}