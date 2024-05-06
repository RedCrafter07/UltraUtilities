package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler

open class SimpleInputItemStore(items: NonNullList<ItemStack>) : ItemStackHandler(items), IProcessedItemHandler<CompoundTag> {
    constructor(size: Int) : this(NonNullList.withSize(size, ItemStack.EMPTY))
    constructor() : this(NonNullList.of(ItemStack.EMPTY))
}