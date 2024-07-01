package redcrafter07.processed.block.tile_entities.capabilities;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface IFluidHandlerModifiable extends IFluidHandler {
    void setStackInTank(int tank, FluidStack stack);
}