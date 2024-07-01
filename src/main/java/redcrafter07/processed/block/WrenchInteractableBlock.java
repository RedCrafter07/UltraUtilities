package redcrafter07.processed.block;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public interface WrenchInteractableBlock {
    void onWrenchUse(UseOnContext context, BlockState state);
}