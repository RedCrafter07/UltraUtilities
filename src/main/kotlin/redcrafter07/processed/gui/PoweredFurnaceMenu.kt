package redcrafter07.processed.gui

import net.minecraft.world.level.block.FurnaceBlock
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.SlotItemHandler
import redcrafter07.processed.block.PoweredFurnace
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity
import redcrafter07.processed.gui.inventoryutils.SlotOutputItemHandler


class PoweredFurnaceMenu : AbstractContainerMenu {
    val blockEntity: PoweredFurnaceBlockEntity
    val level: Level
    val data: ContainerData


    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id,
        inventory,
        inventory.player.level().getBlockEntity(extraData.readBlockPos()),
        SimpleContainerData(2)
    )

    constructor(id: Int, inventory: Inventory, entity: BlockEntity?, data: ContainerData) : super(ModMenuTypes.POWERED_FURNACE_MENU.get(), id) {
        checkContainerSize(inventory, 2)
        blockEntity = entity as PoweredFurnaceBlockEntity
        level = inventory.player.level()
        this.data = data

        addPlayerInventory(inventory)
        addPlayerHotbar(inventory)

        level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.blockPos, Direction.NORTH)?.let { capability ->
            addSlot(SlotItemHandler(capability, 0, 80, 11))
            addSlot(SlotOutputItemHandler(capability, 1, 80, 59))
        }


        addDataSlots(data)
    }

    fun isCrafting(): Boolean {
        return data.get(0) > 0
    }

    fun getScaledProgress(): Int {
        val progress = data.get(0)
        val maxProgress = data.get(1)
        val progressArrowSize = 26

        return if (maxProgress != 0 && progress != 0) progress * progressArrowSize / maxProgress else 0
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    companion object {
    private val HOTBAR_SLOT_COUNT: Int = 9
        private val PLAYER_INVENTORY_ROW_COUNT: Int = 3
        private val PLAYER_INVENTORY_COLUMN_COUNT: Int = 9
        private val PLAYER_INVENTORY_SLOT_COUNT: Int = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
        private val VANILLA_SLOT_COUNT: Int = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
        private val VANILLA_FIRST_SLOT_INDEX: Int = 0
        private val TE_INVENTORY_FIRST_SLOT_INDEX: Int = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT

        // THIS YOU HAVE TO DEFINE!
        private val TE_INVENTORY_SLOT_COUNT: Int = 2 // must be the number of slots you have!
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
                            + TE_INVENTORY_SLOT_COUNT, false
                )
            ) {
                return ItemStack.EMPTY // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
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

    override fun stillValid(player: Player): Boolean {
        return ContainerLevelAccess.create(level, blockEntity.blockPos).evaluate { level, pos ->
            if (level.getBlockState(pos).block !is PoweredFurnace) false else player.distanceToSqr(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5
            ) <= 64.0
        }.orElse(true)
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
}