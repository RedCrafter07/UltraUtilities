package redcrafter07.processed.gui

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.SlotItemHandler
import redcrafter07.processed.block.PoweredFurnaceBlock
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity
import redcrafter07.processed.gui.inventory.ProcessedMachineMenu
import redcrafter07.processed.gui.inventory.SlotOutputItemHandler
import redcrafter07.processed.gui.widgets.ProgressBarWidget
import redcrafter07.processed.gui.widgets.ProgressBars


class PoweredFurnaceMenu(id: Int, inventory: Inventory, entity: BlockEntity?, val data: ContainerData) :
    ProcessedMachineMenu<PoweredFurnaceBlockEntity>(ModMenuTypes.POWERED_FURNACE_MENU.get(), id, inventory, entity as PoweredFurnaceBlockEntity) {
    val level: Level

    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id,
        inventory,
        inventory.player.level().getBlockEntity(extraData.readBlockPos()),
        SimpleContainerData(2)
    )

    init {
        checkContainerSize(inventory, 2)
        level = inventory.player.level()
        addSlot(SlotItemHandler(blockEntity.inputItemHandler, 0, 80, 21))
        addSlot(SlotOutputItemHandler(blockEntity.outputItemHandler, 0, 80, 59))
        addDataSlots(data)
    }

    private fun getProgress(): Double {
        val progress = data.get(0)
        val maxProgress = data.get(1)

        return if (maxProgress != 0 && progress != 0) progress.toDouble() / maxProgress.toDouble() else 0.0
    }

    override fun getProgressBar(offX: Int, offY: Int): ProgressBarWidget {
        return ProgressBars.POWERED_FURNACE.create(offX + 85, offY + 40, this::getProgress)
    }

    override fun getTitle(): Component {
        return blockEntity.displayName
    }

    override fun customSlotCount(): Int {
        return 2
    }

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