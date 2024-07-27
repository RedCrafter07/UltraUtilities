package redcrafter07.processed.materials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class MaterialBlock extends Block {
    protected final Material material;

    public MaterialBlock(Material material, BlockBehaviour.Properties properties) {
        super(properties);
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public static class MetalBlock extends MaterialBlock {
        public MetalBlock(Material material) {
            super(material, material.getMetalBlockProperties());
        }
    }

    public static class OreBlock extends MaterialBlock {
        public OreBlock(Material material) {
            super(material, material.getOreBlockProperties());
        }
    }
}