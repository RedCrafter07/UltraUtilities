package redcrafter07.processed.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import redcrafter07.processed.block.PoweredFurnaceBlock;
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity;
import redcrafter07.processed.gui.inventory.ProcessedMachineMenu;
import redcrafter07.processed.gui.inventory.SlotOutputItemHandler;
import redcrafter07.processed.gui.widgets.ProgressBarWidget;
import redcrafter07.processed.gui.widgets.ProgressBars;

import javax.annotation.Nullable;
import java.util.Objects;


public class PoweredFurnaceMenu extends ProcessedMachineMenu<PoweredFurnaceBlockEntity> {
    public Level level;
    public ContainerData data;

    public PoweredFurnaceMenu(int id, Inventory inventory, @Nullable BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.POWERED_FURNACE_MENU.get(), id, inventory, (PoweredFurnaceBlockEntity) Objects.requireNonNull(entity));
        this.data = data;
        checkContainerSize(inventory, 2);
        level = inventory.player.level();
        addSlot(new SlotItemHandler(blockEntity.getInputItemHandler(), 0, 80, 21));
        addSlot(new SlotOutputItemHandler(blockEntity.getOutputItemHandler(), 0, 80, 59));
        addDataSlots(data);
    }

    public PoweredFurnaceMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(
                id,
                inventory,
                inventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(2)
        );
    }

    private double getProgress() {
        final var progress = data.get(0);
        final var maxProgress = data.get(1);

        return (maxProgress != 0 && progress != 0) ? (double) progress / (double) maxProgress : 0.0;
    }

    @Override
    public ProgressBarWidget getProgressBar(int offX, int offY) {
        return ProgressBars.POWERED_FURNACE.create(offX + 85, offY + 40, this::getProgress);
    }

    @Override
    public Component getTitle() {
        return blockEntity.getDisplayName();
    }

    @Override
    public int customSlotCount() {
        return 2;
    }

    @Override
    public boolean stillValid(Player player) {
        return ContainerLevelAccess.create(level, blockEntity.getBlockPos()).evaluate((level, pos) -> {
            if (level.getBlockState(pos).getBlock() instanceof PoweredFurnaceBlock) return player.distanceToSqr(
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5
            ) <= 64.0;
            return false;
        }).orElse(true);
    }
}