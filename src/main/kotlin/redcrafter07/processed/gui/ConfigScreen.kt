package redcrafter07.processed.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.tile_entities.BlockSide
import redcrafter07.processed.block.tile_entities.ProcessedMachine
import redcrafter07.processed.network.IOChangePacket

class ConfigScreen(private val machine: ProcessedMachine, private val blockPos: BlockPos) :
    Screen(Component.translatable("processed.screen.block_config.title")) {
    companion object {
        const val IMAGE_WIDTH = 176
        const val IMAGE_HEIGHT = 166
        val TEXTURE = ProcessedMod.rl("textures/gui/gui_base_inventory.png")
    }

    private var topX = 0
    private var topY = 0
    private var isItem = true

    override fun init() {
        super.init()

        this.topX = (this.width - IMAGE_WIDTH) / 2
        this.topY = (this.height - IMAGE_HEIGHT) / 2
        for (side in BlockSide.entries) {
            val pos = side.getButtonPos()
            this.addRenderableWidget(
                IoToggleButton(
                    this.topX + pos.x,
                    this.topY + pos.y,
                    side.toComponent(),
                    machine.getSide(isItem, side)
                ) { _, newState ->
                    machine.setSide(isItem, side, newState)
                    machine.invalidateCapabilities()
                    Minecraft.getInstance().connection?.send(IOChangePacket(blockPos, newState, side, isItem))
                }
            )
        }

        this.addRenderableWidget(
            Button.builder(Component.literal("I"), this::ioSwitchToItems)
                .pos(this.topX + 77, this.topY + 32)
                .size(14, 14)
                .build()
        ).active = !isItem
        this.addRenderableWidget(
            Button.builder(Component.literal("F"), this::ioSwitchToFluids)
                .pos(this.topX + 77, this.topY + 50)
                .size(14, 14)
                .build()
        ).active = isItem

    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, unknown: Float) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, TEXTURE)
        val x = (width - IMAGE_WIDTH) / 2
        val y = (height - IMAGE_HEIGHT) / 2
        graphics.blit(TEXTURE, x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
    }

    private fun ioSwitchToItems(button: Button) {
        this.isItem = true
        this.rebuildWidgets()
    }

    private fun ioSwitchToFluids(button: Button) {
        this.isItem = false
        this.rebuildWidgets()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}