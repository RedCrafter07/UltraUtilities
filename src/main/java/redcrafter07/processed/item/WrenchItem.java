package redcrafter07.processed.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import redcrafter07.processed.block.WrenchInteractableBlock;

import javax.annotation.Nullable;
import java.util.List;

import static redcrafter07.processed.item.ModDataComponents.WRENCH_MODE;

public class WrenchItem extends ModItem {
    public WrenchItem() {
        super(new Properties().stacksTo(1), "wrench");
    }

    public static WrenchMode getMode(ItemStack stack) {
        return stack.getOrDefault(WRENCH_MODE, WrenchMode.Config);
    }

    public static ItemStack setMode(ItemStack stack, WrenchMode mode) {
        stack.set(WRENCH_MODE, mode);
        return stack;
    }

    private static final List<DirectionProperty> facingProperties = List.of(
            DirectionalBlock.FACING,
            HorizontalDirectionalBlock.FACING,
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.FACING
    );

    @Override
    public ItemStack getDefaultInstance() {
        return setMode(super.getDefaultInstance(), WrenchMode.Config);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(context.getHand());
        if (stack.getItem() instanceof WrenchItem item) {
            WrenchMode wrenchMode = getMode(stack);

            return switch (wrenchMode) {
                case WrenchMode.Config -> configure(stack, context);
                case WrenchMode.Rotate -> rotate(stack, context);
            };
        }
        return InteractionResult.PASS;
    }

    private InteractionResult rotate(ItemStack stack, UseOnContext context) {
        var blockState = context.getLevel().getBlockState(context.getClickedPos());
        var player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        for (var property : facingProperties) {
            if (blockState.hasProperty(property)) {
                var facing = blockState.getValue(property);
                var newFacing = (player.isShiftKeyDown()) ? facing.getCounterClockWise() : facing.getClockWise();
                var newState = blockState.setValue(property, newFacing);
                context.getLevel().setBlock(context.getClickedPos(), newState, 3);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private InteractionResult configure(ItemStack stack, UseOnContext context) {
        var blockState = context.getLevel().getBlockState(context.getClickedPos());

        if (blockState.getBlock() instanceof WrenchInteractableBlock block) {
            block.onWrenchUse(context, blockState);
            return InteractionResult.SUCCESS;
        }

        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof WrenchInteractableBlock blockEntity) {
            blockEntity.onWrenchUse(context, blockState);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @Nullable MutableComponent

    getAdditionalTooltip(ItemStack stack, TooltipContext context, TooltipFlag flag) {
        return Component.translatable(
                "item.processed.wrench.mode", Component.translatable(getMode(stack).translation())
        );
    }
}