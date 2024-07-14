package redcrafter07.processed.block.tile_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import redcrafter07.processed.block.ModBlocks;
import redcrafter07.processed.multiblock.BlockmapMultiblockValidator;
import redcrafter07.processed.multiblock.MultiblockBlockEntity;
import redcrafter07.processed.multiblock.MultiblockPart;
import redcrafter07.processed.multiblock.MultiblockValidator;

import java.util.List;

public class BigSmelterBlockEntity extends MultiblockBlockEntity {
    public BigSmelterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModTileEntities.BIG_SMELTER.get(), pos, blockState);
    }

    private static final List<List<MultiblockPart>> BLOCKMAP = List.of(
            List.of(
                    MultiblockPart.Casing, MultiblockPart.Casing, MultiblockPart.Casing,
                    MultiblockPart.Casing, MultiblockPart.Casing, MultiblockPart.Casing,
                    MultiblockPart.Casing, MultiblockPart.Casing, MultiblockPart.Casing
            ),
            List.of(
                    MultiblockPart.Casing, MultiblockPart.Casing, MultiblockPart.Casing,
                    MultiblockPart.Casing, MultiblockPart.Empty, MultiblockPart.Casing,
                    MultiblockPart.Casing, MultiblockPart.Controller, MultiblockPart.Casing
            ),
            List.of(
                    MultiblockPart.Casing, MultiblockPart.Casing, MultiblockPart.Casing,
                    MultiblockPart.Casing, MultiblockPart.Casing, MultiblockPart.Casing,
                    MultiblockPart.Casing, MultiblockPart.Casing, MultiblockPart.Casing
            )
    );

    @Override
    protected MultiblockValidator validator() {
        return new BlockmapMultiblockValidator(MultiblockPart.map(ModBlocks.BASIC_CASING.get(), ModBlocks.BIG_SMELTER.get()),
                BLOCKMAP, new Vector2i(3, 3));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.processed.big_smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }
}
