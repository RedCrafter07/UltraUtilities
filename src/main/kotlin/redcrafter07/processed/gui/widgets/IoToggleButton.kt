package redcrafter07.processed.gui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import redcrafter07.processed.block.tile_entities.IoState
import redcrafter07.processed.gui.RenderUtils.WIDGETS_TEXTURE


class IoToggleButton(
    x: Int,
    y: Int,
    private val buttonName: Component,
    var state: IoState,
    private var supportedStates: Set<IoState>,
    val item: ItemStack?,
    private var onChange: OnChange,
) :
    AbstractWidget(
        x + 1, y + 1, 20, 20,
        Component.translatable("processed.io_button.message", buttonName, state.toComponent())
    ) {

    @OnlyIn(Dist.CLIENT)
    fun interface OnChange {
        fun onChange(button: IoToggleButton, newState: IoState)
    }

    init {
        this.tooltip = Tooltip.create(Component.translatable("processed.io_button.tooltip", this.state.toComponent()))
    }

    override fun renderWidget(graphics: GuiGraphics, p1: Int, p2: Int, p3: Float) {
        graphics.blit(WIDGETS_TEXTURE, this.x - 1, this.y - 1, this.state.getId() * 22, 0, 22, 22)

        if (item != null && !item.isEmpty)
            graphics.renderFakeItem(item, x + 2, y + 2)

    }

    override fun isValidClickButton(button: Int): Boolean {
        return (button == 0 || button == 1) // left or right click
    }

    override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
        if (button == 0) nextState()
        else previousState()
        this.onChange.onChange(this, this.state)
        this.tooltip = Tooltip.create(Component.translatable("processed.io_button.tooltip", this.state.toComponent()))
        this.message = Component.translatable("processed.io_button.message", buttonName, state.toComponent())
    }

    private fun nextState() {
        do this.state = this.state.next()
        while (!isValidState(this.state))
    }

    private fun previousState() {
        do this.state = this.state.previous()
        while (!isValidState(this.state))
    }

    private fun isValidState(state: IoState) = state == IoState.None || supportedStates.contains(state)

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }
}