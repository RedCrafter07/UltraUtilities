package redcrafter07.processed.gui.inventory

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class ProcessedContainerMenu(menuType: MenuType<*>?, containerId: Int, playerInventory: Inventory) :
    AbstractContainerMenu(menuType, containerId) {

    init {
        addPlayerInventory(playerInventory)
        addPlayerHotbar(playerInventory)
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    companion object {
        val HOTBAR_SLOT_COUNT: Int = 9
        val PLAYER_INVENTORY_ROW_COUNT: Int = 3
        val PLAYER_INVENTORY_COLUMN_COUNT: Int = 9
        val PLAYER_INVENTORY_SLOT_COUNT: Int = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
        val VANILLA_SLOT_COUNT: Int = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
        val VANILLA_FIRST_SLOT_INDEX: Int = 0
        val TE_INVENTORY_FIRST_SLOT_INDEX: Int = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT

    }

    open fun customSlotCount(): Int {
        return slots.count() - 36 // #slots (usually the custom slots + vanilla inventory + hotbar slots) - 36 (4*9, 3*9 for the inventory and 1*9 for the hotbar)
    }

    private fun addPlayerInventory(playerInventory: Inventory) {
        for (i in 0..<3) {
            for (l in 0..<9) {
                this.addSlot(Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18))
            }
        }
    }

    private fun addPlayerHotbar(playerInventory: Inventory) {
        for (i in 0..<9) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
    }

    override fun quickMoveStack(playerIn: Player, pIndex: Int): ItemStack {
        val sourceSlot = slots[pIndex]
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY //EMPTY_ITEM

        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(
                    sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                            + customSlotCount(), false
                )
            ) {
                return ItemStack.EMPTY // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + customSlotCount()) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(
                    sourceStack,
                    VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false
                )
            ) {
                return ItemStack.EMPTY
            }
        } else {
            println("Invalid slotIndex:$pIndex")
            return ItemStack.EMPTY
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {
            sourceSlot.set(ItemStack.EMPTY)
        } else {
            sourceSlot.setChanged()
        }
        sourceSlot.onTake(playerIn, sourceStack)
        return copyOfSourceStack
    }
}