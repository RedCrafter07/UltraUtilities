package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.util.FastColor
import redcrafter07.processed.ProcessedMod

object RenderUtils {
    val WIDGETS_TEXTURE = ProcessedMod.rl("textures/gui/widgets.png")
    val GUI_BASE_TEXTURE = ProcessedMod.rl("textures/gui/gui_base.png")
    const val GUI_BASE_TEXTURE_WIDTH = 176
    const val GUI_BASE_TEXTURE_HEIGHT = 166

    fun renderSlot(graphics: GuiGraphics, x: Int, y: Int) {
        graphics.blit(WIDGETS_TEXTURE, x, y, 0, 22, 18, 18)
    }

    fun renderDefault(screen: AbstractContainerScreen<*>, graphics: GuiGraphics) {
        val xOff = screen.guiLeft
        val yOff = screen.guiTop
        graphics.blit(
            GUI_BASE_TEXTURE,
            xOff,
            yOff,
            0,
            0,
            GUI_BASE_TEXTURE_WIDTH,
            GUI_BASE_TEXTURE_HEIGHT
        )
        screen.menu.slots.forEach { renderSlot(graphics, xOff + it.x - 1, yOff + it.y - 1) }
    }

    fun color(red: Int, green: Int, blue: Int, alpha: Int): Int {
        return FastColor.ARGB32.color(alpha, red, green, blue)
    }

    fun color(red: Int, green: Int, blue: Int): Int {
        return color(red, green, blue, 255)
    }

    fun fill(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, color: Int) {
        guiGraphics.fill(x, y, x + width, y + height, color)
    }

    object GuiColors {
        val WHITE = color(0xff, 0xff, 0xff)
        val GRAY = color(0x8b, 0x8b, 0x8b)
        val DARK_GRAY = color(0x37, 0x37, 0x37)
        val ENERGY = color(0x0e, 0xa5, 0xe9)
    }
}