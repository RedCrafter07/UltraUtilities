package redcrafter07.processed.block;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

interface AdditionalBlockInfo {
    @Nullable
    default MutableComponent getAdditionalTooltip(ItemStack stack, TooltipContext context, TooltipFlag flag) {
        return null;
    }
}

public class ModBlockItem extends BlockItem {
    private final String itemID;

    public ModBlockItem(Block block, Properties itemProperties, String itemID) {
        super(block, itemProperties);
        this.itemID = itemID;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("block.processed." + itemID + ".tooltip"));

        if (getBlock() instanceof AdditionalBlockInfo block) {
            Component additionalTooltip = block.getAdditionalTooltip(stack, context, flag);
            if (additionalTooltip != null) tooltip.add(additionalTooltip);
        }

        tooltip.add(Component.empty());
        super.appendHoverText(stack, context, tooltip, flag);
    }
}

