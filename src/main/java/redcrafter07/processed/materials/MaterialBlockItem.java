package redcrafter07.processed.materials;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public abstract class MaterialBlockItem extends BlockItem {
    protected final Material material;

    public MaterialBlockItem(Block block, Material material) {
        super(block, new Properties().stacksTo(64));
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public static class MetalBlock extends MaterialBlockItem {
        public MetalBlock(Block block, Material material) {
            super(block, material);
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable("processed.material_metal_block", material.getComponent());
        }
    }

    public static class OreBlock extends MaterialBlockItem {
        public OreBlock(Block block, Material material) {
            super(block, material);
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable("processed.material_ore", material.getComponent());
        }
    }
}