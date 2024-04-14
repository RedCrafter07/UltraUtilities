package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import redcrafter07.processed.ProcessedMod
import kotlin.time.times

enum class GuiIoStates {
    Input,
    Output,
    InputOutput,
    Additional,
    Auxiliary;

    companion object {
        fun load(value: Short): GuiIoStates {
            return when (value.toInt()) {
                0 -> Input
                1 -> Output
                2 -> InputOutput
                3 -> Additional
                4 -> Auxiliary
                else -> Input
            }
        }

        class Sprite(val offsetX: Int, val offsetY: Int, val width: Int, val height: Int) {}
    }

    fun save(): Short {
        return when (this) {
            Input -> 0.toShort()
            Output -> 1.toShort()
            InputOutput -> 2.toShort()
            Additional -> 3.toShort()
            Auxiliary -> 4.toShort()
        }
    }

    fun getOffsets(): Sprite {
        return Sprite(this.save().toInt() * 14, 0, 14, 14)
    }
}

val widgetsResourceLocation = ResourceLocation(ProcessedMod.ID, "textures/gui/widgets.png")

class GuiIoToggle(x: Int, y: Int, text: Component, var state: GuiIoStates, private var onChange: OnChange) : AbstractWidget(
    x, y, 40, 16,
    text
) {
    companion object {
        @FunctionalInterface
        interface OnChange {
            fun change(button: GuiIoToggle, newStates: GuiIoStates);
        }
    }

    override fun renderWidget(graphics: GuiGraphics, p1: Int, p2: Int, p3: Float) {
        val sprite = this.state.getOffsets()

//        graphics.blitSprite()
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }
}