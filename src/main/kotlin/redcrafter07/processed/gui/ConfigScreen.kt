package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.joml.Vector2i
import redcrafter07.processed.ProcessedMod

enum class IOSides {
    Top,
    Bottom,
    Left,
    Right,
    Front,
    Back;

    fun getButtonPos(): Vector2i {
        return when (this) {
            Top -> Vector2i(27, 28)
            Bottom -> Vector2i(27, 54)
            Left -> Vector2i(14, 41)
            Front -> Vector2i(27, 41)
            Right -> Vector2i(40, 41)
            Back -> Vector2i(53, 41)
        }
    }

    fun toComponent(): Component {
        return when (this) {
            Top -> Component.translatable("processed.side.top")
            Bottom -> Component.translatable("processed.side.bottom")
            Left -> Component.translatable("processed.side.left")
            Right -> Component.translatable("processed.side.right")
            Front -> Component.translatable("processed.side.front")
            Back -> Component.translatable("processed.side.back")
        }
    }
}

class ConfigScreen : Screen(Component.translatable("processed.screen.block_config.title")) {
    companion object {
        const val menuWidth = 176
        const val menuHeight = 166
        val menuResource = ResourceLocation(ProcessedMod.ID, "textures/gui/gui_base_inventory.png")
    }

    private var topX = 0;
    private var topY = 0;
    private var isIo = true;

    override fun init() {
        super.init()

        this.topX = (this.width - menuWidth) / 2;
        this.topY = (this.height - menuHeight) / 2;


        this.addRenderableWidget(
            Button.builder(Component.translatable("processed.screen.block_config.upgrades"), this::switchToUpgrades)
                .pos(this.topX + 11, this.topY + 7)
                .size(75, 16)
                .build()
        )
        this.addRenderableWidget(
            Button.builder(Component.translatable("processed.screen.block_config.io"), this::switchToIo)
                .pos(this.topX + 90, this.topY + 7)
                .size(75, 16)
                .build()
        )

        if (isIo) {
            for (side in IOSides.entries) {
                val pos = side.getButtonPos();
                this.addRenderableWidget(
                    IoToggleButton(
                        this.topX + pos.x,
                        this.topY + pos.y,
                        side.toComponent(),
                        GuiIoState.None,
                        this::onIoChange
                    )
                )
            }
        }
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, unknown: Float) {
        super.renderBackground(graphics, mouseX, mouseY, unknown)

        graphics.blit(menuResource, this.topX, this.topY, 0, 0, menuWidth, menuHeight)
    }

    private fun switchToIo(button: Button) {
        this.isIo = true
        this.rebuildWidgets()
    }
    private fun switchToUpgrades(button: Button) {
        this.isIo = false
        this.rebuildWidgets()
    }
    private fun onIoChange(button: IoToggleButton, newState: GuiIoState) {}

    override fun isPauseScreen(): Boolean {
        return false
    }
}