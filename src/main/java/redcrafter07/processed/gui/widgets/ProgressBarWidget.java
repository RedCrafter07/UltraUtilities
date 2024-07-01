package redcrafter07.processed.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Supplier;

public class ProgressBarWidget implements Renderable {
    private final int x;
    private final int y;
    private final ProgressBarData data;
    private Supplier<Double> progressSupplier;
    public ProgressBarWidget(int x, int y, ProgressBarData data, Supplier<Double> progressSupplier) {
        this.x = x;
        this.y = y;
        this.data = data;
        this.progressSupplier = progressSupplier;
    }

    private void blit(GuiGraphics graphics, int x, int y, int offX, int offY, int width, int height) {
        graphics.blit(data.texture, x, y, offX, offY, width, height, data.width * 2, data.height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        blit(graphics, x, y, 0, 0, data.width, data.height);

        if (data.direction == ProgressBarDirection.Down) {
            final int height = (int) (progressSupplier.get() * data.height);
            blit(graphics, x, y, data.width, 0, data.width, height);
            return;
        }
        if (data.direction == ProgressBarDirection.Left) {
            final int width = (int) (progressSupplier.get() * data.width);
            blit(graphics, x, y, data.width, 0, width, data.height);
        }

        if (data.direction == ProgressBarDirection.Up) {
            final int height = (int) (progressSupplier.get() * data.height);
            blit(graphics, x, y + data.height - height, data.width, data.height - height, data.width, height);
            return;
        }
        if (data.direction == ProgressBarDirection.Right) {
            final int width = (int) (progressSupplier.get() * data.width);
            blit(graphics, x + data.width - width, y, data.width * 2 - width, 0, width, data.height);
        }
    }

    public void setProgressSupplier(Supplier<Double> newSupplier) {
        progressSupplier = newSupplier;
    }

    public enum ProgressBarDirection {
        Down,
        Up,
        Right,
        Left
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    public record ProgressBarData(
            ResourceLocation texture,
        int width,
        int height,
            ProgressBarDirection direction
    ) {
        public ProgressBarWidget create(int x, int y, Supplier<Double> supplier) {
            return new ProgressBarWidget(x, y, this, supplier);
        }
    }
}