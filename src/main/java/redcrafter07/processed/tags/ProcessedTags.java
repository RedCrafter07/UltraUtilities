package redcrafter07.processed.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import redcrafter07.processed.ProcessedMod;

public class ProcessedTags {
    public static class Items {
        public static final TagKey<Item> FORGE_INGOT_BLITZ = commonTag("ingots/blitz");

        public static TagKey<Item> commonTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static TagKey<Item> tag(String name) {
            return ItemTags.create(ProcessedMod.rl(name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> FORGE_ORE_BLITZ = commonTag("ores/blitz");

        public static TagKey<Block> commonTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

        public static TagKey<Block> tag(String name) {
            return BlockTags.create(ProcessedMod.rl(name));
        }
    }
}