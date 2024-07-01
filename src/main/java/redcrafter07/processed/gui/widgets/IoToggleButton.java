package redcrafter07.processed.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import redcrafter07.processed.block.machine_abstractions.IoState;
import redcrafter07.processed.gui.RenderUtils;

import javax.annotation.Nullable;
import java.util.Set;


public class IoToggleButton extends AbstractWidget {
    private final Component buttonName;
    private final Set<IoState> supportedStates;
    private final OnChange onChange;
    public IoState state;
    public @Nullable ItemStack item;


    public IoToggleButton(int x,
                          int y,
                          Component buttonName,
                          IoState state,
                          Set<IoState> supportedStates,
                          @Nullable ItemStack item,
                          OnChange onChange
    ) {
        super(x + 1, y + 1, 20, 20, Component.translatable("processed.io_button.message", buttonName, state.toComponent()));
        this.buttonName = buttonName;
        this.supportedStates = supportedStates;
        this.onChange = onChange;
        this.state = state;
        this.item = item;
        this.setTooltip(Tooltip.create(Component.translatable("processed.io_button.tooltip", this.state.toComponent())));
    }

    @OnlyIn(Dist.CLIENT)
    @FunctionalInterface
    public interface OnChange {
        void onChange(IoToggleButton button, IoState newState);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderUtils.WIDGETS_TEXTURE, getX() - 1, getY() - 1, this.state.getId() * 22, 0, 22, 22);

        if (item != null && !item.isEmpty())
            graphics.renderFakeItem(item, getX() + 2, getY() + 2);
    }

    @Override
    public boolean isValidClickButton(int button) {
        return (button == 0 || button == 1); // left or right click
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0) nextState();
        else previousState();
        this.onChange.onChange(this, this.state);
        this.setTooltip(Tooltip.create(Component.translatable("processed.io_button.tooltip", this.state.toComponent())));
        this.setMessage(Component.translatable("processed.io_button.message", buttonName, state.toComponent()));
    }

    private void nextState() {
        do this.state = this.state.next();
        while (invalidState(this.state));
    }

    private void previousState() {
        do this.state = this.state.previous();
        while (invalidState(this.state));
    }

    private boolean invalidState(IoState state) {
        return state != IoState.None && !supportedStates.contains(state);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}