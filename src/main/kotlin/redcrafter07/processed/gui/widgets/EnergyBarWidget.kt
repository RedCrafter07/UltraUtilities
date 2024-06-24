package redcrafter07.processed.gui.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import redcrafter07.processed.gui.RenderUtils.fill
import redcrafter07.processed.gui.RenderUtils.GuiColors
import java.util.function.Supplier

class EnergyBarWidget(x: Int, y: Int, width: Int, height: Int, val maxEnergy: Int, val energySupplier: Supplier<Int>) :
    AbstractWidget(x, y, width, height, Component.empty()) {

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (width < 2 || height < 2) return

        fill(graphics, x, y, 1, height - 1, GuiColors.DARK_GRAY)
        fill(graphics, x, y, width - 1, 1, GuiColors.DARK_GRAY)

        fill(graphics, x + width - 1, y, 1, 1, GuiColors.GRAY)
        fill(graphics, x, y + height - 1, 1, 1, GuiColors.GRAY)
        if (width != 2 && height != 2) fill(graphics, x + 1, y + 1, width - 2, height - 2, GuiColors.GRAY)

        fill(graphics, x + 1, y + height - 1, width - 1, 1, GuiColors.WHITE)
        fill(graphics, x + width - 1, y + 1, 1, height - 1, GuiColors.WHITE)

        val energy = energySupplier.get()
        val energyHeight = (energy * (height - 2) / maxEnergy).coerceIn(0, height - 2)

        fill(graphics, x + 1, y - 1 + height - energyHeight, width - 2, energyHeight, GuiColors.ENERGY)

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