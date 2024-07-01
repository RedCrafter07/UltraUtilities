package redcrafter07.processed.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import redcrafter07.processed.gui.inventory.ProcessedMachineMenu;

public class GenericMachineMenuScreen extends AbstractContainerScreen<ProcessedMachineMenu<?>> {

    public Component component;

    public GenericMachineMenuScreen(ProcessedMachineMenu<?> menu, Inventory inventory, Component component) {
        super(
                menu,
                inventory,
                menu.getTitle()
        );
    }


    @Override
    public void init() {
        super.init();
        addRenderableOnly(menu.getProgressBar(leftPos, topPos));
        final var energyWidget = menu.getEnergyContainer(leftPos, topPos);
        if (energyWidget != null) addRenderableWidget(energyWidget);
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderUtils.renderDefault(this, guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}