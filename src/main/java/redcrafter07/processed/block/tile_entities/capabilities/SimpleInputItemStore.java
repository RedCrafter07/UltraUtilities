package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class SimpleInputItemStore extends
        ProcessedItemStackHandler {
    public SimpleInputItemStore(NonNullList<ItemStack> items) {
        super(items);
    }

    public SimpleInputItemStore(int size) {
        super(size);
    }

    public SimpleInputItemStore() {
        super();
    }
}