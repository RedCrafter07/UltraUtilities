package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import redcrafter07.processed.block.machine_abstractions.IoState;
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine;

public class MergedIoItemCapability implements IItemHandlerModifiable {

    private final ProcessedMachine.CapabilityHandlers handlers;

    public MergedIoItemCapability(ProcessedMachine.CapabilityHandlers handlers) {
        this.handlers = handlers;
    }

    private IItemHandler get(IoState state) {
        final var handler = handlers.getItemHandlerForState(state);
        if (handler == null) return EmptyItemHandler.INSTANCE;
        return handler;
    }

    @Override
    public int getSlots() {
        return get(IoState.Output).getSlots() + get(IoState.Input).getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        final var handler = handlers.getItemHandlerForState(IoState.Output);
        if (handler == null) return ItemStack.EMPTY;
        if (slot >= handler.getSlots() || slot < 0) return ItemStack.EMPTY;
        return handler.getStackInSlot(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        var realSlot = slot - get(IoState.Output).getSlots();

        if (get(IoState.Input) instanceof IItemHandlerModifiable handler) {
            if (realSlot < 0 || realSlot >= handler.getSlots()) return;
            handler.setStackInSlot(realSlot, stack);
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        var realSlot = slot - get(IoState.Output).getSlots();
        final var handler = get(IoState.Input);

        if (realSlot >= handler.getSlots() || realSlot < 0) return stack;
        return handler.insertItem(realSlot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int maxAmount, boolean simulate) {
        final var handler = get(IoState.Output);
        if (slot >= handler.getSlots() || slot < 0) return ItemStack.EMPTY;

        return handler.extractItem(
                slot,
                maxAmount,
                simulate
        );
    }

    @Override
    public int getSlotLimit(int slot) {
        final var handlerInput = get(IoState.Input);
        final var handlerOutput = get(IoState.Output);

        if (slot < 0) return 0;
        if (slot < handlerOutput.getSlots()) return handlerOutput.getSlotLimit(slot);
        if (slot < handlerInput.getSlots() + handlerOutput.getSlots()) return handlerInput.getSlotLimit(slot - handlerOutput.getSlots());
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        final var handlerInput = get(IoState.Input);
        final var handlerOutput = get(IoState.Output);

        if (slot < 0) return false;
        if (slot < handlerOutput.getSlots()) return handlerOutput.isItemValid(slot, stack);
        if (slot < handlerInput.getSlots() + handlerOutput.getSlots()) return handlerInput.isItemValid(slot - handlerOutput.getSlots(), stack);
        return false;
    }
}