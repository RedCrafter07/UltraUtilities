package redcrafter07.processed.gui.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import redcrafter07.processed.gui.RenderUtils.fill
import redcrafter07.processed.gui.RenderUtils.gui_colors
import java.util.function.Supplier

class EnergyBarWidget(x: Int, y: Int, width: Int, height: Int, val maxEnergy: Int, val energySupplier: Supplier<Int>) :
    AbstractWidget(x, y, width, height, Component.empty()) {

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (width < 2 || height < 2) return

        fill(graphics, x, y, 1, height - 1, gui_colors.DARK_GRAY)
        fill(graphics, x, y, width - 1, 1, gui_colors.DARK_GRAY)

        fill(graphics, x + width - 1, y, 1, 1, gui_colors.GRAY)
        fill(graphics, x, y + height - 1, 1, 1, gui_colors.GRAY)
        if (width != 2 && height != 2) fill(graphics, x + 1, y + 1, width - 2, height - 2, gui_colors.GRAY)

        fill(graphics, x + 1, y + height - 1, width - 1, 1, gui_colors.WHITE)
        fill(graphics, x + width - 1, y + 1, 1, height - 1, gui_colors.WHITE)

        val energy = energySupplier.get()
        val energyHeight = (energy * (height - 2) / maxEnergy).coerceIn(0, height - 2)

        fill(graphics, x + 1, y - 1 + height - energyHeight, width - 2, energyHeight, gui_colors.ENERGY)

        if (isHovered)
            graphics.renderTooltip(
                Minecraft.getInstance().font,
                Component.translatable("processed.gui.widgets.energy_bar", energy, maxEnergy),
                mouseX,
                mouseY
            )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}
}