package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;

public class SimpleInputFluidStore extends SimpleFluidStore {
    public SimpleInputFluidStore(NonNullList<FluidStack> tanks, int capacity) {
        super(tanks, capacity);
    }

    public SimpleInputFluidStore(int tanks, int capacity) {
        super(tanks, capacity);
    }

    @Override
    public FluidStack drain(FluidStack stack, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }
}
