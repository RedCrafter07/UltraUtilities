package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class GuiTabButton(x: Int, y: Int, text: Component) : AbstractWidget(
    x, y, 40, 16,
    text
) {
    override fun renderWidget(graphics: GuiGraphics, p1: Int, p2: Int, p3: Float) {

    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }
}