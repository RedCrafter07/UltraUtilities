package redcrafter07.processed.gui.inventory;

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable

class SlotOutputItemHandler(val itemHandler: IItemHandler, private val realIndex: Int, xPosition: Int, yPosition: Int) :
    Slot(emptyInventory, realIndex, xPosition, yPosition) {

    override fun mayPlace(stack: ItemStack): Boolean { return false }

    override fun getItem(): ItemStack {
        return itemHandler.getStackInSlot(realIndex)
    }

    override fun set(stack: ItemStack) {
        (itemHandler as IItemHandlerModifiable).setStackInSlot(realIndex, stack)
        this.setChanged()
    }

    fun initialize(stack: ItemStack?) {
        (itemHandler as IItemHandlerModifiable).setStackInSlot(realIndex, stack ?: ItemStack.EMPTY)
        this.setChanged()
    }

    override fun onQuickCraft(oldStackIn: ItemStack, newStackIn: ItemStack) {
    }

    override fun getMaxStackSize(): Int {
        return itemHandler.getSlotLimit(realIndex)
    }

    override fun getMaxStackSize(stack: ItemStack): Int {
        val maxAdd = stack.copy()
        val maxInput = stack.maxStackSize
        maxAdd.count = maxInput
        val handler = this.itemHandler
        val currentStack = handler.getStackInSlot(realIndex)
        if (handler is IItemHandlerModifiable) {
            handler.setStackInSlot(realIndex, ItemStack.EMPTY)
            val remainder = handler.insertItem(realIndex, maxAdd, true)
            handler.setStackInSlot(realIndex, currentStack)
            return maxInput - remainder.count
        } else {
            val remainder = handler.insertItem(realIndex, maxAdd, true)
            val current = currentStack.count
            val added = maxInput - remainder.count
            return current + added
        }
    }

    override fun mayPickup(playerIn: Player): Boolean {
        return !itemHandler.extractItem(realIndex, 1, true).isEmpty
    }

    override fun remove(amount: Int): ItemStack {
        return itemHandler.extractItem(realIndex, amount, false)
    }

    companion object {
        private val emptyInventory: Container = SimpleContainer(0)
    }
}