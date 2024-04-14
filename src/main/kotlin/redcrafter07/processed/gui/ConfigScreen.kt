package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import redcrafter07.processed.ProcessedMod

class ConfigScreen : Screen(Component.translatable("processed.screen.block_config.title")) {
    companion object {
        const val menuWidth = 176
        const val menuHeight = 166
        val menuResource = ResourceLocation(ProcessedMod.ID, "textures/gui/gui_base_inventory.png")
    }
    var topX = 0;
    var topY = 0;

    override fun init() {
        super.init()

        this.topX = (this.width - menuWidth) / 2;
        this.topY = (this.height - menuHeight) / 2;


        this.addRenderableWidget(
            Button.builder(Component.translatable("processed.screen.block_config.upgrades"), this::void).pos(this.topX + 11, this.topY + 7)
                .size(75, 16)
                .build()
        )
        this.addRenderableWidget(
            Button.builder(Component.translatable("processed.screen.block_config.io"), this::void).pos(this.topX + 90, this.topY + 7)
                .size(75, 16)
                .build()
        )
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, unknown: Float) {
        super.renderBackground(graphics, mouseX, mouseY, unknown)

        graphics.blit(menuResource, this.topX, this.topY, 0, 0, menuWidth, menuHeight)
    }

    private fun void(button: Button) {}

    override fun isPauseScreen(): Boolean {
        return false
    }
}