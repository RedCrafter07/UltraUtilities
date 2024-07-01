package redcrafter07.processed.block.machine_abstractions;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import redcrafter07.processed.gui.widgets.EnergyBarWidget;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TieredProcessedBlock extends ProcessedBlock {
    private final String baseName;
    private final BlockEntityType.BlockEntitySupplier<TieredProcessedMachine> blockEntity;
    final ProcessedTier tier;

    public TieredProcessedBlock(
            Properties properties,
            String baseName,
            ProcessedTier tier,
            BlockEntityType.BlockEntitySupplier<TieredProcessedMachine> blockEntity
    ) {
        super(properties);
        this.baseName = baseName;
        this.tier = tier;
        this.blockEntity = blockEntity;
    }

    @Override
    public MutableComponent getName() {
        return Component.translatable(baseName, tier.translated());
    }

    public void getDescription(
            List<Component> tooltips,
            TooltipFlag tooltipFlag
    ) {
        tooltips.add(Component.translatable(baseName + ".tooltip"));
        tooltips.add(getMachineInfo(tier));
    }

    private static MutableComponent getMachineInfo(ProcessedTier tier) {
        return Component.translatable(
                "processed.tiered_machine_info",
                (Screen.hasShiftDown()
                        ? Component.translatable("processed.gui.widget.energy_bar.normal", tier.getMaxPower())
                        : EnergyBarWidget.getEnergyComponent(tier.getMaxPower())).withStyle(ChatFormatting.GREEN),
                tier.colored()
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        TieredProcessedMachine entity = blockEntity.create(pos, state);
        entity.setTier(tier);
        return entity;
    }
}
