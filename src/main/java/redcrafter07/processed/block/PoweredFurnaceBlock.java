package redcrafter07.processed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import redcrafter07.processed.block.machine_abstractions.ProcessedTier;
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock;
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity;

public class PoweredFurnaceBlock extends
        TieredProcessedBlock {

    public PoweredFurnaceBlock(ProcessedTier tier) {
        super(Properties.of().sound(SoundType.STONE), "block.processed.powered_furnace", tier, PoweredFurnaceBlockEntity::new);
    }

    @Override
    public InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos blockPos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (level.getBlockEntity(blockPos) instanceof PoweredFurnaceBlockEntity be) {
            player.openMenu(be, (data) -> data.writeBlockPos(blockPos));
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}

