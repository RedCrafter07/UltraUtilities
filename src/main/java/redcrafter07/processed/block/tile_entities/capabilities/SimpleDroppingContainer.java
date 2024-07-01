package redcrafter07.processed.block.tile_entities.capabilities;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SimpleDroppingContainer extends SimpleContainer {
    public SimpleDroppingContainer(ItemStack... items) {
        super(items);
    }

    public void appendItem(ItemStack itemStack) {
        var stack = super.addItem(itemStack);
        if (!stack.isEmpty()) getItems().add(stack);
    }
}