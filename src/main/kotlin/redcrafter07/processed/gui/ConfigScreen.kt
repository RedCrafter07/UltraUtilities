package redcrafter07.processed.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.tile_entities.BlockSide
import redcrafter07.processed.block.tile_entities.ProcessedMachine
import redcrafter07.processed.gui.widgets.IoToggleButton
import redcrafter07.processed.network.IOChangePacket
import java.util.*
import redcrafter07.processed.gui.RenderUtils.GUI_BASE_TEXTURE as TEXTURE
import redcrafter07.processed.gui.RenderUtils.GUI_BASE_TEXTURE_HEIGHT as IMAGE_HEIGHT
import redcrafter07.processed.gui.RenderUtils.GUI_BASE_TEXTURE_WIDTH as IMAGE_WIDTH

class ConfigScreen(private val machine: ProcessedMachine, private val blockPos: BlockPos) :
    Screen(machine.displayName) {

    private var topX = 0
    private var topY = 0
    private var isItem = machine.supportedItemHandlers.size > 1

    override fun init() {
        super.init()
        val facing = getFacingBlockStates(blockPos)

        topX = (width - IMAGE_WIDTH) / 2
        topY = (height - IMAGE_HEIGHT) / 2
        for (side in BlockSide.entries) {
            val pos = side.getButtonPos()
            addRenderableWidget(
                IoToggleButton(
                    topX + pos.x,
                    topY + pos.y,
                    side.toComponent(),
                    machine.getSide(isItem, side),
                    if (isItem) machine.supportedItemHandlers else machine.supportedFluidHandlers,
                    facing[side]
                ) { _, newState ->
                    machine.setSide(isItem, side, newState)
                    machine.invalidateCapabilities()
                    Minecraft.getInstance().connection?.send(IOChangePacket(blockPos, newState, side, isItem))
                }
            )
        }

        if (machine.supportedItemHandlers.size > 1 && machine.supportedFluidHandlers.size > 1) {
            addRenderableWidget(
                Button.builder(Component.literal("I"), this::ioSwitchToItems)
                    .pos(topX + 110, topY + 32)
                    .size(14, 14)
                    .build()
            ).active = !isItem


            addRenderableWidget(
                Button.builder(Component.literal("F"), this::ioSwitchToFluids)
                    .pos(topX + 110, topY + 50)
                    .size(14, 14)
                    .build()
            ).active = isItem
        }
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, unknown: Float) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.setShaderTexture(0, TEXTURE)
        val x = (width - IMAGE_WIDTH) / 2
        val y = (height - IMAGE_HEIGHT) / 2
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680)
        graphics.blit(TEXTURE, x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
        graphics.drawString(font, title, topX + 8, topY + 6, 4210752, false)
        graphics.drawString(
            font,
            if (isItem) Component.translatable("processed.screen.block_config.items")
            else Component.translatable("processed.screen.block_config.fluids"),
            topX + 8,
            topY + 18,
            4210752,
            false
        )
    }

    private fun ioSwitchToItems(button: Button) {
        isItem = true
        rebuildWidgets()
    }

    private fun ioSwitchToFluids(button: Button) {
        isItem = false
        rebuildWidgets()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    private fun getFacingBlockStates(pos: BlockPos): EnumMap<BlockSide, ItemStack> {
        val player = Minecraft.getInstance().player ?: return EnumMap(BlockSide::class.java)
        val level = player.level() ?: return EnumMap(BlockSide::class.java)
        val map = EnumMap<BlockSide, ItemStack>(BlockSide::class.java)
        val machine = level.getBlockState(pos)

        for (dir in Direction.entries) {
            val otherBlockPos = pos.relative(dir)
            val otherBlockState = level.getBlockState(otherBlockPos)
            if (otherBlockState.isEmpty || otherBlockState.isAir) continue
            val item = otherBlockState.getCloneItemStack(
                BlockHitResult(
                    otherBlockPos.center.relative(dir.opposite, 0.5),
                    dir.opposite,
                    otherBlockPos,
                    false
                ),
                level,
                otherBlockPos,
                player
            )
            if (item.isEmpty) continue

            map[BlockSide.translateDirection(dir, machine)] = item
        }

        return map
    }
}