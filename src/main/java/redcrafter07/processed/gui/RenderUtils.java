package redcrafter07.processed.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import redcrafter07.processed.ProcessedMod;

import javax.annotation.Nullable;

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

    public static TextureAtlasSprite getFluidTexture(FluidStack fluidStack, boolean flowing) {
        final var properties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        final @Nullable ResourceLocation spriteLocation = flowing ? properties.getFlowingTexture() : properties.getStillTexture();
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }

    public static int getFluidColor(FluidStack fluidStack) {
        final var properties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        return properties.getTintColor();
    }

    public static void renderCube(VertexConsumer buffer, PoseStack poseStack, float xMax, float xMin, float yMin, float height, float zMin, float zMax, TextureAtlasSprite textureAtlasSprite, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
        float uMin = textureAtlasSprite.getU0();
        float uMax = textureAtlasSprite.getU1();
        float vMin = textureAtlasSprite.getV0();
        float vMax = textureAtlasSprite.getV1();

        float vHeight = vMax - vMin;

        // top
        addVertexWithUV(buffer, poseStack, xMax, height, zMax, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMax, height, zMin, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, height, zMin, uMin, vMax, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, height, zMax, uMax, vMax, red, green, blue, alpha, packedLight, packedOverlay);

        // north
        addVertexWithUV(buffer, poseStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, yMin, zMin, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, height, zMin, uMin, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMax, height, zMin, uMax, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);

        // south
        addVertexWithUV(buffer, poseStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMax, height, zMax, uMin, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, height, zMax, uMax, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, yMin, zMax, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay);

        // east
        addVertexWithUV(buffer, poseStack, xMax, yMin, zMin, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMax, height, zMin, uMin, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMax, height, zMax, uMax, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMax, yMin, zMax, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay);

        // west
        addVertexWithUV(buffer, poseStack, xMin, yMin, zMax, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, height, zMax, uMin, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, height, zMin, uMax, vMin + (vHeight * height), red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, yMin, zMin, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay);

        // down
        addVertexWithUV(buffer, poseStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, yMin, zMax, uMin, vMax, red, green, blue, alpha, packedLight, packedOverlay);
        addVertexWithUV(buffer, poseStack, xMin, yMin, zMin, uMax, vMax, red, green, blue, alpha, packedLight, packedOverlay);
    }

    public static void addVertexWithUV(VertexConsumer buffer, PoseStack matrixStack, float x, float y, float z, float u, float v, float red, float green, float blue, float alpha, int packedLight, int packedOverlay) {
        buffer
                .addVertex(matrixStack.last().pose(), x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setLight(packedLight)
                .setOverlay(packedOverlay)
                .setNormal(1, 0, 0);
    }

    public static final int WHITE = color(0xff, 0xff, 0xff);
    public static final int GRAY = color(0x8b, 0x8b, 0x8b);
    public static final int DARK_GRAY = color(0x37, 0x37, 0x37);
    public static final int ENERGY = color(0x0e, 0xa5, 0xe9);
}