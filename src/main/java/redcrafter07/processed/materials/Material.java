package redcrafter07.processed.materials;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nullable;

public class Material {
    private final String identifier;
    private final int color;
    private final String chemicalDescription;
    private final @Nullable TagKey<Block> materialTag;
    private final BlockBehaviour.Properties metalBlockProperties;
    private final BlockBehaviour.Properties oreProperties;

    public Material(String identifier,
                    int color,
                    String chemicalDescription,
                    @Nullable TagKey<Block> materialTag,
                    BlockBehaviour.Properties metalBlockProperties,
                    BlockBehaviour.Properties oreProperties) {
        this.identifier = identifier;
        this.color = color;
        this.chemicalDescription = chemicalDescription;
        this.materialTag = materialTag;
        this.metalBlockProperties = metalBlockProperties;
        this.oreProperties = oreProperties;
    }

    public MutableComponent getComponent() {
        return Component.translatable("processed.material." + identifier);
    }

    public int color() {
        return color;
    }

    public String chemicalDescription() {
        return chemicalDescription;
    }

    public String getDustPath() {
        return identifier + "_dust";
    }

    public TagKey<Item> getDustTag() {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts/" + identifier));
    }

    public String getIngotPath() {
        return identifier + "_ingot";
    }

    public TagKey<Item> getIngotTag() {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/" + identifier));
    }

    public String getNuggetPath() {
        return identifier + "_nugget";
    }

    public TagKey<Item> getNuggetTag() {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets/" + identifier));
    }

    public String getRawPath() {
        return "raw_" + identifier;
    }

    public TagKey<Item> getRawTag() {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/" + identifier));
    }

    public String getMetalBlockPath() {
        return identifier + "_block";
    }

    public BlockBehaviour.Properties getMetalBlockProperties() {
        return metalBlockProperties;
    }

    public String getOreBlockPath() {
        return identifier + "_ore";
    }

    public BlockBehaviour.Properties getOreBlockProperties() {
        return oreProperties;
    }

    public @Nullable TagKey<Block> getMaterialTag() {
        return materialTag;
    }
}
