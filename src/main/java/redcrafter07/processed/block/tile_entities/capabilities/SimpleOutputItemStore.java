package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class SimpleOutputItemStore extends SimpleInputItemStore {
    public SimpleOutputItemStore(NonNullList<ItemStack> items) {
        super(items);
    }

    public SimpleOutputItemStore(int size) {
        super(size);
    }

    public SimpleOutputItemStore() {
        super();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false;
    }
}