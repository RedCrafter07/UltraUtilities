package redcrafter07.processed.gui

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.SlotItemHandler
import redcrafter07.processed.block.PoweredFurnaceBlock
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity
import redcrafter07.processed.gui.inventory.ProcessedContainerMenu
import redcrafter07.processed.gui.inventory.SlotOutputItemHandler


class PoweredFurnaceMenu(id: Int, inventory: Inventory, entity: BlockEntity?, val data: ContainerData) :
    ProcessedContainerMenu(ModMenuTypes.POWERED_FURNACE_MENU.get(), id, inventory) {
    val blockEntity: PoweredFurnaceBlockEntity
    val level: Level


    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id,
        inventory,
        inventory.player.level().getBlockEntity(extraData.readBlockPos()),
        SimpleContainerData(2)
    )

    init {
        checkContainerSize(inventory, 2)
        blockEntity = entity as PoweredFurnaceBlockEntity
        level = inventory.player.level()
        addSlot(SlotItemHandler(blockEntity.inputItemHandler, 0, 80, 11))
        addSlot(SlotOutputItemHandler(blockEntity.outputItemHandler, 0, 80, 59))
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

    override fun customSlotCount(): Int {return 2}

    override fun stillValid(player: Player): Boolean {
        return ContainerLevelAccess.create(level, blockEntity.blockPos).evaluate { level, pos ->
            if (level.getBlockState(pos).block !is PoweredFurnaceBlock) false else player.distanceToSqr(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5
            ) <= 64.0
        }.orElse(true)
    }
}