package redcrafter07.processed.gui.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine;
import redcrafter07.processed.gui.widgets.EnergyBarWidget;
import redcrafter07.processed.gui.widgets.ProgressBarWidget;

import javax.annotation.Nullable;
import java.awt.Rectangle;

public abstract class ProcessedMachineMenu<T extends ProcessedMachine> extends ProcessedContainerMenu {
    public final static Rectangle ENERGY_DEFAULT_RIGHT = new Rectangle(158, 20, 10, 50); // default position (right) assuming a 176x166 gui
    public final static Rectangle ENERGY_DEFAULT_LEFT = new Rectangle(6, 20, 10, 50); // default position (left) assuming a 176x166 gui

    public T blockEntity;

    public ProcessedMachineMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, T blockEntity) {
        super(menuType, containerId, playerInventory);
        this.blockEntity = blockEntity;
    }

    public abstract ProgressBarWidget getProgressBar(int offX, int offY);

    public abstract Component getTitle();

    protected Rectangle getEnergyBarPosition() {
        return ENERGY_DEFAULT_RIGHT;
    }

    public @Nullable EnergyBarWidget getEnergyContainer(int offX, int offY) {
        if (blockEntity.getEnergyCapability().getMaxEnergyStored() < 1) return null;
        final var position = getEnergyBarPosition();
        return new EnergyBarWidget(
                offX + position.x,
                offY + position.y,
                position.width,
                position.height,
                blockEntity.getEnergyCapability().getMaxEnergyStored(),
                () -> blockEntity.getEnergyCapability().getEnergyStored()
        );
    }
}
