package redcrafter07.processed.gui.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public class SlotOutputItemHandler extends Slot {

    private final int realIndex;
    private final IItemHandler itemHandler;
    private static final Container emptyInventory = new SimpleContainer(0);

    public SlotOutputItemHandler(IItemHandler itemHandler, int realIndex, int xPosition, int yPosition) {
        super(emptyInventory, realIndex, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.realIndex = realIndex;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return itemHandler.getStackInSlot(realIndex);
    }

    @Override
    public void set(ItemStack stack) {
        if (itemHandler instanceof IItemHandlerModifiable handler) {
            handler.setStackInSlot(realIndex, stack);
            this.setChanged();
        }
    }

    public void initialize(@Nullable ItemStack stack) {
        if (stack == null) set(ItemStack.EMPTY);
        else set(stack);
    }

    @Override
    public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
    }

    @Override
    public int getMaxStackSize() {
        return itemHandler.getSlotLimit(realIndex);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        final var maxAdd = stack.copy();
        final var maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);
        if (itemHandler instanceof IItemHandlerModifiable handler) {
            final var currentStack = handler.getStackInSlot(realIndex);
            handler.setStackInSlot(realIndex, ItemStack.EMPTY);
            final var remainder = handler.insertItem(realIndex, maxAdd, true);
            handler.setStackInSlot(realIndex, currentStack);
            return maxInput - remainder.getCount();
        } else {
            final var remainder = itemHandler.insertItem(realIndex, maxAdd, true);
            final var current = itemHandler.getStackInSlot(realIndex).getCount();
            final var added = maxInput - remainder.getCount();
            return current + added;
        }
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !itemHandler.extractItem(realIndex, 1, true).isEmpty();
    }

    @Override
    public ItemStack remove(int amount) {
        return itemHandler.extractItem(realIndex, amount, false);
    }
}