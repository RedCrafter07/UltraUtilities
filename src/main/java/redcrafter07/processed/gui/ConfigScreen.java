
package redcrafter07.processed.gui;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import redcrafter07.processed.block.machine_abstractions.BlockSide;
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine;
import redcrafter07.processed.gui.widgets.IoToggleButton;
import redcrafter07.processed.network.IOChangePacket;

import java.util.EnumMap;

public class ConfigScreen extends Screen {

    private final ProcessedMachine machine;
    private final BlockPos blockPos;
    private int topX = 0;
    private int topY = 0;
    private boolean isItem;

    public ConfigScreen(ProcessedMachine machine, BlockPos blockPos) {
        super(machine.getDisplayName());
        this.machine = machine;
        this.blockPos = blockPos;
        isItem = machine.getSupportedItemHandlers().size() > 1;
    }


    @Override
    public void init() {
        super.init();
        final EnumMap<BlockSide, ItemStack> facing = getFacingBlockStates(blockPos);

        topX = (width - RenderUtils.GUI_BASE_TEXTURE_WIDTH) / 2;
        topY = (height - RenderUtils.GUI_BASE_TEXTURE_HEIGHT) / 2;
        for (var side : BlockSide.values()) {
            final var pos = side.getButtonPos();
            addRenderableWidget(
                    new IoToggleButton(
                            topX + pos.x,
                            topY + pos.y,
                            side.toComponent(),
                            machine.getSide(isItem, side),
                            (isItem) ? machine.getSupportedItemHandlers()
                                    : machine.getSupportedFluidHandlers(),
                            facing.get(side),
                            (oldState, newState) -> {
                                machine.setSide(isItem, side, newState);
                                machine.invalidateCapabilities();
                                final var connection = Minecraft.getInstance().getConnection();
                                if (connection != null)
                                    connection.send(new IOChangePacket(blockPos, newState, side, isItem));
                            }
                    )
            );
        }

        if (machine.getSupportedItemHandlers().size() > 1 && machine.getSupportedFluidHandlers().size() > 1) {
            addRenderableWidget(
                    Button.builder(Component.literal("I"), this::ioSwitchToItems)
                            .pos(topX + 110, topY + 32)
                            .size(14, 14)
                            .build()
            ).active = !isItem;


            addRenderableWidget(
                    Button.builder(Component.literal("F"), this::ioSwitchToFluids)
                            .pos(topX + 110, topY + 50)
                            .size(14, 14)
                            .build()
            ).active = isItem;
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, RenderUtils.GUI_BASE_TEXTURE);
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        graphics.blit(RenderUtils.GUI_BASE_TEXTURE, topX, topY, 0, 0, RenderUtils.GUI_BASE_TEXTURE_WIDTH, RenderUtils.GUI_BASE_TEXTURE_HEIGHT);
        graphics.drawString(font, title, topX + 8, topY + 6, 4210752, false);
        graphics.drawString(
                font,
                (isItem) ? Component.translatable("processed.screen.block_config.items") :
                        Component.translatable("processed.screen.block_config.fluids"),
                topX + 8,
                topY + 18,
                4210752,
                false
        );
    }

    private void ioSwitchToItems(Button button) {
        isItem = true;
        rebuildWidgets();
    }

    private void ioSwitchToFluids(Button button) {
        isItem = false;
        rebuildWidgets();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private EnumMap<BlockSide, ItemStack> getFacingBlockStates(BlockPos pos) {
        final var player = Minecraft.getInstance().player;
        if (player == null) return new EnumMap<>(BlockSide.class);
        final var level = player.level();
        final var map = new EnumMap<BlockSide, ItemStack>(BlockSide.class);
        final var machine = level.getBlockState(pos);

        for (var dir : Direction.values()) {
            final var otherBlockPos = pos.relative(dir);
            final var otherBlockState = level.getBlockState(otherBlockPos);
            if (otherBlockState.isEmpty() || otherBlockState.isAir()) continue;
            final var item = otherBlockState.getCloneItemStack(
                    new BlockHitResult(
                            otherBlockPos.getCenter().relative(dir.getOpposite(), 0.5),
                            dir.getOpposite(),
                            otherBlockPos,
                            false
                    ),
                    level,
                    otherBlockPos,
                    player
            );
            if (item.isEmpty()) continue;
            map.put(BlockSide.translateDirection(dir, machine), item);
        }

        return map;
    }
}