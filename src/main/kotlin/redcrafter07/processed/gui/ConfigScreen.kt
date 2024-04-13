package redcrafter07.processed.gui

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu

private class ConfigMenu: AbstractContainerMenu() {

}

class ConfigScreen : AbstractContainerScreen<ConfigMenu>(Component.translatable("processed.screen.block_config.title")) {
    override fun init() {
        super.init()
        this.addRenderableWidget(
            Button.builder(Component.translatable("processed.screen.block_config.upgrades"), this::void).pos(11, 7)
                .size(60, 14)
                .build()
        )
        this.addRenderableWidget(
            Button.builder(Component.translatable("processed.screen.block_config.io"), this::void).pos(105, 7)
                .size(60, 14)
                .build()
        )
    }

    private fun void(button: Button) {}

    override fun isPauseScreen(): Boolean {
        return false
    }
}