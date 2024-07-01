package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;

public class SimpleOutputFluidStore extends SimpleInputFluidStore {

    public SimpleOutputFluidStore(NonNullList<FluidStack> tanks, int capacity) {
        super(tanks, capacity);
    }

    public SimpleOutputFluidStore(int tanks, int capacity) {
        super(tanks, capacity);
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        return stack.getAmount();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return false;
    }

}