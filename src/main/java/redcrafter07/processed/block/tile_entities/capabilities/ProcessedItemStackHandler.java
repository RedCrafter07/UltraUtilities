package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ProcessedItemStackHandler extends IProcessedItemHandler<CompoundTag> {
    protected NonNullList<ItemStack> items;

    public ProcessedItemStackHandler(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public ProcessedItemStackHandler(int size) {
        this(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    public ProcessedItemStackHandler() {
        this(1);
    }

    @Override
    public int getSlots() {
        return items.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= items.size()) return ItemStack.EMPTY;
        return items.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!isItemValid(slot, stack)) return stack;
        if (invalidSlotIndex(slot)) return ItemStack.EMPTY;
        final var existing = items.get(slot);
        var limit = getStackLimit(slot, stack);
        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;
        final var reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                items.set(slot, (reachedLimit) ? stack.copyWithCount(limit) : stack);
            } else {
                existing.grow((reachedLimit) ? limit : stack.getCount());
            }
            setChanged(slot);
        }

        return (reachedLimit) ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;
        if (invalidSlotIndex(slot)) return ItemStack.EMPTY;
        final var existing = items.get(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;
        final var toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                items.set(slot, ItemStack.EMPTY);
                setChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                items.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                setChanged(slot);
            }

            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (invalidSlotIndex(slot)) return 0;
        if (items.get(slot).isEmpty()) return Item.ABSOLUTE_MAX_STACK_SIZE;
        return Math.min(Item.ABSOLUTE_MAX_STACK_SIZE, items.get(slot).getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (invalidSlotIndex(slot)) return;
        items.set(slot, stack);
        setChanged(slot);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider) {
        final var nbtTagList = new ListTag();
        for (int i = 0; i < items.size(); ++i) {
            if (items.get(i).isEmpty()) continue;
            final var itemTag = new CompoundTag();
            itemTag.putInt("Slot", i);
            nbtTagList.add(items.get(i).save(provider, itemTag));
        }
        final var nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", items.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        setSize((nbt.contains("Size", Tag.TAG_INT)) ? nbt.getInt("Size") : items.size());
        final var tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); ++i) {
            final var itemTags = tagList.getCompound(i);
            var slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < items.size())
                items.set(slot, ItemStack.parseOptional(provider, itemTags));

        }
    }

    protected void setSize(int newSize) {
        items = NonNullList.withSize(newSize, ItemStack.EMPTY);
        setChanged(-1);
    }

    protected boolean invalidSlotIndex(int slot) {
        return (slot < 0 || slot >= items.size());
    }

    protected int getStackLimit(int slot, ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }
}