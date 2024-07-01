package redcrafter07.processed.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock;

import java.util.List;

public class TieredModBlockItem extends
        BlockItem {
    private final TieredProcessedBlock tieredProcessedBlock;

    public TieredModBlockItem(TieredProcessedBlock tieredProcessedBlock, Properties itemProperties) {
        super(tieredProcessedBlock, itemProperties);
        this.tieredProcessedBlock = tieredProcessedBlock;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tieredProcessedBlock.getDescription(tooltip, flag);

        if (getBlock() instanceof AdditionalBlockInfo block) {
            Component additionalTooltip = block.getAdditionalTooltip(stack, context, flag);
            if (additionalTooltip != null) tooltip.add(additionalTooltip);
        }

        tooltip.add(Component.empty());
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack itemStack) {
        return tieredProcessedBlock.getName();
    }
}
