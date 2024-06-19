package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import redcrafter07.processed.ProcessedMod

object RenderUtils {
    val WIDGETS_TEXTURE = ProcessedMod.rl("textures/gui/widgets.png")
    private val GUI_BASE_TEXTURE = ProcessedMod.rl("textures/gui/gui_base.png")
    private const val GUI_BASE_TEXTURE_WIDTH = 176
    private const val GUI_BASE_TEXTURE_HEIGHT = 166

    fun renderSlot(graphics: GuiGraphics, x: Int, y: Int) {
        graphics.blit(WIDGETS_TEXTURE, x, y, 0, 14, 18, 18)
    }

    fun renderSlots(menu: AbstractContainerMenu, graphics: GuiGraphics) {
        menu.slots.forEach { renderSlot(graphics, it.x, it.y) }
    }

    fun renderDefault(screen: AbstractContainerScreen<*>, graphics: GuiGraphics) {
        graphics.blit(GUI_BASE_TEXTURE, screen.guiLeft, screen.guiTop, 0, 0, GUI_BASE_TEXTURE_WIDTH, GUI_BASE_TEXTURE_HEIGHT)
        screen.menu.slots.forEach { renderSlot(graphics, it.x + screen.guiLeft - 1, it.y + screen.guiTop - 1) }
    }
}