package redcrafter07.processed.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import redcrafter07.processed.gui.RenderUtils;

import java.util.function.Supplier;

import static redcrafter07.processed.gui.RenderUtils.fill;

public class EnergyBarWidget extends AbstractWidget {

    public final int maxEnergy;
    public final Supplier<Integer> energySupplier;

    public EnergyBarWidget(int x, int y, int width, int height, int maxEnergy, Supplier<Integer> energySupplier) {
        super(x, y, width, height, Component.empty());
        this.maxEnergy = maxEnergy;
        this.energySupplier = energySupplier;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (width < 2 || height < 2) return;
        final var x = getX();
        final var y = getY();

        fill(graphics, x, y, 1, height - 1, RenderUtils.DARK_GRAY);
        fill(graphics, x, y, width - 1, 1, RenderUtils.DARK_GRAY);

        fill(graphics, x + width - 1, y, 1, 1, RenderUtils.GRAY);
        fill(graphics, x, y + height - 1, 1, 1, RenderUtils.GRAY);
        if (width != 2 && height != 2) fill(graphics, x + 1, y + 1, width - 2, height - 2, RenderUtils.GRAY);

        fill(graphics, x + 1, y + height - 1, width - 1, 1, RenderUtils.WHITE);
        fill(graphics, x + width - 1, y + 1, 1, height - 1, RenderUtils.WHITE);

        final var energy = energySupplier.get();
        final var energyHeight = Math.min(Math.max((energy * (height - 2) / maxEnergy), 0), height - 2);

        fill(graphics, x + 1, y - 1 + height - energyHeight, width - 2, energyHeight, RenderUtils.ENERGY);

        if (isHovered)
            graphics.renderTooltip(
                    Minecraft.getInstance().font,
                    Component.translatable("processed.gui.widgets.energy_bar", getEnergyComponent(energy), getEnergyComponent(maxEnergy)),
                    mouseX,
                    mouseY
            );
    }

    public static MutableComponent getEnergyComponent(int energy) {
        if (energy >= 2_000_000) return Component.translatable("processed.gui.widget.energy_bar.million", energy / 1_000_000);
        if (energy >= 2_000) return Component.translatable("processed.gui.widget.energy_bar.thousand", energy / 1_000);
        return Component.translatable("processed.gui.widget.energy_bar.normal", energy);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return false;
    }
}