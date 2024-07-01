package redcrafter07.processed.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import redcrafter07.processed.ProcessedMod;

public class RenderUtils {
    public static final ResourceLocation WIDGETS_TEXTURE = ProcessedMod.rl("textures/gui/widgets.png");
    public static final ResourceLocation GUI_BASE_TEXTURE = ProcessedMod.rl("textures/gui/gui_base.png");
    public static final int GUI_BASE_TEXTURE_WIDTH = 176;
    public static final int GUI_BASE_TEXTURE_HEIGHT = 166;

    public static void renderSlot(GuiGraphics graphics, int x, int y) {
        graphics.blit(WIDGETS_TEXTURE, x, y, 0, 22, 18, 18);
    }

    public static void renderDefault(AbstractContainerScreen<?> screen, GuiGraphics graphics) {
        final var xOff = screen.getGuiLeft();
        final var yOff = screen.getGuiTop();
        graphics.blit(
                GUI_BASE_TEXTURE,
                xOff,
                yOff,
                0,
                0,
                GUI_BASE_TEXTURE_WIDTH,
                GUI_BASE_TEXTURE_HEIGHT
        );
        for (var slot : screen.getMenu().slots) renderSlot(graphics, xOff + slot.x - 1, yOff + slot.y - 1);
    }

    public static int color(int red, int green, int blue, int alpha) {
        return FastColor.ARGB32.color(alpha, red, green, blue);
    }

    public static int color(int red, int green, int blue) {
        return color(red, green, blue, 255);
    }

    public static void fill(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        guiGraphics.fill(x, y, x + width, y + height, color);
    }

    public static final int WHITE = color(0xff, 0xff, 0xff);
    public static final int GRAY = color(0x8b, 0x8b, 0x8b);
    public static final int DARK_GRAY = color(0x37, 0x37, 0x37);
    public static final int ENERGY = color(0x0e, 0xa5, 0xe9);
}