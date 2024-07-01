package redcrafter07.processed.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;


public class ModItem extends Item {
    private final String itemID;

    public ModItem(Properties properties, String itemID) {
        super(properties);
        this.itemID = itemID;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("item.processed." + itemID + ".tooltip"));
        final var additionalTooltip = getAdditionalTooltip(stack, context, flag);
        if (additionalTooltip != null) tooltip.add(additionalTooltip);

        tooltip.add(Component.empty());
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public @Nullable MutableComponent getAdditionalTooltip(ItemStack stack, TooltipContext context, TooltipFlag flag) {
        return null;
    }
}