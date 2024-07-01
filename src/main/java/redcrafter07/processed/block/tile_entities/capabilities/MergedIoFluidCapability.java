package redcrafter07.processed.block.tile_entities.capabilities;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import redcrafter07.processed.block.machine_abstractions.IoState;
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine;

public class MergedIoFluidCapability implements IFluidHandlerModifiable {

    private final ProcessedMachine.CapabilityHandlers handlers;

    public MergedIoFluidCapability(ProcessedMachine.CapabilityHandlers handlers) {
        this.handlers = handlers;
    }

    private IFluidHandler get(IoState state) {
        final var handler = handlers.getFluidHandlerForState(state);
        if (handler == null) return EmptyFluidHandler.INSTANCE;
        return handler;
    }

    @Override
    public int getTanks() {
        return get(IoState.Output).getTanks() + get(IoState.Input).getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int slot) {
        final var handler = handlers.getFluidHandlerForState(IoState.Output);
        if (handler == null) return FluidStack.EMPTY;
        if (slot >= handler.getTanks() || slot < 0) return FluidStack.EMPTY;
        return handler.getFluidInTank(slot);
    }

    @Override
    public void setStackInTank(int slot, FluidStack stack) {
        var realSlot = slot - get(IoState.Output).getTanks();

        if (get(IoState.Input) instanceof IFluidHandlerModifiable handler) {
            if (realSlot < 0 || realSlot >= handler.getTanks()) return;
            handler.setStackInTank(realSlot, stack);
        }
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return get(IoState.Input).fill(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return get(IoState.Output).drain(maxDrain, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return get(IoState.Output).drain(resource, action);
    }

    @Override
    public int getTankCapacity(int slot) {
        final var handlerInput = get(IoState.Input);
        final var handlerOutput = get(IoState.Output);

        if (slot < 0) return 0;
        if (slot < handlerOutput.getTanks()) return handlerOutput.getTankCapacity(slot);
        if (slot < handlerInput.getTanks() + handlerOutput.getTanks()) return handlerInput.getTankCapacity(slot - handlerOutput.getTanks());
        return 0;
    }

    @Override
    public boolean isFluidValid(int slot, FluidStack stack) {
        final var handlerInput = get(IoState.Input);
        final var handlerOutput = get(IoState.Output);

        if (slot < 0) return false;
        if (slot < handlerOutput.getTanks()) return handlerOutput.isFluidValid(slot, stack);
        if (slot < handlerInput.getTanks() + handlerOutput.getTanks()) return handlerInput.isFluidValid(slot - handlerOutput.getTanks(), stack);
        return false;
    }
}