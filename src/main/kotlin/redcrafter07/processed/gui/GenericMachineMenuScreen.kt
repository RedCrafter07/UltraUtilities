package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.gui.inventory.ProcessedMachineMenu

class GenericMachineMenuScreen(
    menu: ProcessedMachineMenu<*>,
    inventory: Inventory,
    val component: Component,
) :
    AbstractContainerScreen<ProcessedMachineMenu<*>>(
        menu,
        inventory,
        menu.getTitle()
    ) {
    override fun init() {
        super.init()
        addRenderableOnly(menu.getProgressBar(leftPos, topPos))
        menu.getEnergyContainer(leftPos, topPos)?.let { addRenderableWidget(it) }
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderUtils.renderDefault(this, guiGraphics)
        ProcessedMod.LOGGER.info("Energy: ${menu.blockEntity.energyHandler.energyStored}")
        ProcessedMod.LOGGER.info("Slot #0: ${menu.blockEntity.inputItemHandler.getStackInSlot(0)}")
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}