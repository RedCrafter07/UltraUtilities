package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.IoState


class IoToggleButton(x: Int, y: Int, private val buttonName: Component, var state: IoState, private var onChange: OnChange) :
    AbstractWidget(
        x + 1, y + 1, 12, 12,
        Component.translatable("processed.io_button.message", buttonName, state.toComponent())
    ) {

    companion object {
        private val widgetsResourceLocation = ProcessedMod.rl("textures/gui/widgets.png")
    }

    @OnlyIn(Dist.CLIENT)
    fun interface OnChange {
        fun onChange(button: IoToggleButton, newState: IoState)
    }

    init {
        this.tooltip = Tooltip.create(Component.translatable("processed.io_button.tooltip", this.state.toComponent()))
    }

    override fun renderWidget(graphics: GuiGraphics, p1: Int, p2: Int, p3: Float) {
        graphics.blit(widgetsResourceLocation, this.x - 1, this.y - 1, this.state.getId() * 14, 0, 14, 14)
    }

    override fun isValidClickButton(button: Int): Boolean {
        return (button == 0 || button == 1) // left or right click
    }

    override fun onClick(mouseX: Double, mouseY: Double, button: Int) {

        if (button == 0) this.state = this.state.next()
        else this.state = this.state.previous()
        this.onChange.onChange(this, this.state)
        this.tooltip = Tooltip.create(Component.translatable("processed.io_button.tooltip", this.state.toComponent()))
        this.message = Component.translatable("processed.io_button.message", buttonName, state.toComponent())

    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }
}