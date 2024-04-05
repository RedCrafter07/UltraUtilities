package redcrafter07.processed.tags

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import redcrafter07.processed.ProcessedMod

object ProcessedTags {
    object Items {
        val FORGE_INGOT_BLITZ = forgeTag("ingots/blitz");

        fun forgeTag(name: String): TagKey<Item> {
            return ItemTags.create(ResourceLocation("forge", name));
        }

        fun tag(name: String): TagKey<Item> {
            return ItemTags.create(ResourceLocation(ProcessedMod.ID, name));
        }
    }

    object Blocks {
        val FORGE_ORE_BLITZ = forgeTag("ores/blitz");

        fun forgeTag(name: String): TagKey<Block> {
            return BlockTags.create(ResourceLocation("forge", name));
        }

        fun tag(name: String): TagKey<Block> {
            return BlockTags.create(ResourceLocation(ProcessedMod.ID, name));
        }
    }
}