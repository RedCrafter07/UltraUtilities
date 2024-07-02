package redcrafter07.processed.block.tile_entities.capabilities;

import net.neoforged.neoforge.fluids.FluidStack;

public class EmptyFluidHandler implements IFluidHandlerModifiable {

    @Override
    public void setFluidInTank(int tank, FluidStack stack) {
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTanks() {
        return 0;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }
}