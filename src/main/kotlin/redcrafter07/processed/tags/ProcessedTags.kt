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
        val FORGE_INGOT_BLITZ = commonTag("ingots/blitz")

        fun commonTag(name: String): TagKey<Item> {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name))
        }

        fun tag(name: String): TagKey<Item> {
            return ItemTags.create(ProcessedMod.rl(name))
        }
    }

    object Blocks {
        val FORGE_ORE_BLITZ = commonTag("ores/blitz")

        fun commonTag(name: String): TagKey<Block> {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name))
        }

        fun tag(name: String): TagKey<Block> {
            return BlockTags.create(ProcessedMod.rl(name))
        }
    }
}