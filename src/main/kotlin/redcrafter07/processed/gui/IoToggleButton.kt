package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import redcrafter07.processed.ProcessedMod

enum class GuiIoState {
    None,
    Input,
    Output,
    InputOutput,
    Additional,
    Auxiliary;

    companion object {
        fun load(value: Short): GuiIoState {
            return when (value.toInt()) {
                0 -> None
                1 -> Input
                2 -> Output
                3 -> InputOutput
                4 -> Additional
                5 -> Auxiliary
                else -> Input
            }
        }
    }

    fun save(): Short {
        return when (this) {
            None -> 0.toShort()
            Input -> 1.toShort()
            Output -> 2.toShort()
            InputOutput -> 3.toShort()
            Additional -> 4.toShort()
            Auxiliary -> 5.toShort()
        }
    }

    fun next(): GuiIoState {
        return when (this) {
            Auxiliary -> None
            None -> Input
            Input -> Output
            Output -> InputOutput
            InputOutput -> Additional
            Additional -> Auxiliary
        }
    }

    fun previous(): GuiIoState {
        return when (this) {
            Input -> None
            Output -> Input
            InputOutput -> Output
            Additional -> InputOutput
            Auxiliary -> Additional
            None -> Auxiliary
        }
    }

    fun toComponent(): Component {
        return when (this) {
            None -> Component.translatable("processed.io_state.none")
            Input -> Component.translatable("processed.io_state.input")
            Output -> Component.translatable("processed.io_state.output")
            InputOutput -> Component.translatable("processed.io_state.input_output")
            Additional -> Component.translatable("processed.io_state.additional")
            Auxiliary -> Component.translatable("processed.io_state.auxiliary")
        }
    }
}

val widgetsResourceLocation = ResourceLocation(ProcessedMod.ID, "textures/gui/widgets.png")

class IoToggleButton(x: Int, y: Int, val buttonName: Component, var state: GuiIoState, private var onChange: OnChange) :
    AbstractWidget(
        x + 1, y + 1, 12, 12,
        Component.translatable("processed.io_button.message", buttonName, state.toComponent())
    ) {
    @OnlyIn(Dist.CLIENT)
    fun interface OnChange {
        fun onChange(button: IoToggleButton, newState: GuiIoState)
    }

    init {
        this.tooltip = Tooltip.create(Component.translatable("processed.io_button.tooltip", this.state.toComponent()))
    }

    override fun renderWidget(graphics: GuiGraphics, p1: Int, p2: Int, p3: Float) {
        graphics.blit(widgetsResourceLocation, this.x - 1, this.y - 1, this.state.save().toInt() * 14, 0, 14, 14)
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