package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import redcrafter07.processed.gui.inventory.ProcessedMachineMenu

class GenericMachineMenuScreen(
    menu: ProcessedMachineMenu,
    inventory: Inventory,
    val component: Component,
) :
    AbstractContainerScreen<ProcessedMachineMenu>(
        menu,
        inventory,
        menu.getTitle()
    ) {
    override fun init() {
        super.init()
        addRenderableOnly(menu.getProgressBar(leftPos, topPos))
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderUtils.renderDefault(this, guiGraphics)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}