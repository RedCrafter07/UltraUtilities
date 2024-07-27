package redcrafter07.processed.materials;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MaterialItem extends Item {
    protected final Material material;

    public MaterialItem(Material material) {
        super(new Item.Properties().stacksTo(64));
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(material.chemicalDescription()).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public abstract TagKey<Item> getTag();

    public static class Dust extends MaterialItem {
        public Dust(Material material) {
            super(material);
        }

        @Override
        public TagKey<Item> getTag() {
            return material.getDustTag();
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable("processed.material_dust", material.getComponent());
        }
    }

    public static class Ingot extends MaterialItem {
        public Ingot(Material material) {
            super(material);
        }

        @Override
        public TagKey<Item> getTag() {
            return material.getIngotTag();
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable("processed.material_ingot", material.getComponent());
        }
    }

    public static class Nugget extends MaterialItem {
        public Nugget(Material material) {
            super(material);
        }

        @Override
        public TagKey<Item> getTag() {
            return material.getNuggetTag();
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable("processed.material_nugget", material.getComponent());
        }
    }

    public static class Raw extends MaterialItem {
        public Raw(Material material) {
            super(material);
        }

        @Override
        public TagKey<Item> getTag() {
            return material.getRawTag();
        }

        @Override
        public Component getName(ItemStack stack) {
            return Component.translatable("processed.material_raw", material.getComponent());
        }
    }
}