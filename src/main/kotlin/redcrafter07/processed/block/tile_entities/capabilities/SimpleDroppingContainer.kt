package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack

class SimpleDroppingContainer(vararg items: ItemStack?) : SimpleContainer(*items) {
    constructor(size: Int) : this(*NonNullList.withSize(size, ItemStack.EMPTY).toTypedArray())

    fun appendItem(itemStack: ItemStack) {
        val stack = super.addItem(itemStack)
        if (!stack.isEmpty) items.add(stack)
    }
}