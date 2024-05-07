package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack

class SimpleOutputItemStore(items: NonNullList<ItemStack>) : SimpleInputItemStore(items) {
    constructor(size: Int) : this(NonNullList.withSize(size, ItemStack.EMPTY))
    constructor() : this(NonNullList.of(ItemStack.EMPTY))

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return stack
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return false
    }
}