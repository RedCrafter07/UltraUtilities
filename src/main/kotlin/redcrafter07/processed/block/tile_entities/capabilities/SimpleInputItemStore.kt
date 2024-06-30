package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack

open class SimpleInputItemStore(items: NonNullList<ItemStack>) :
    ProcessedItemStackHandler(items) {
    constructor(size: Int) : this(NonNullList.withSize(size, ItemStack.EMPTY))
    constructor() : this(1)
}