package redcrafter07.processed.datagen

import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import redcrafter07.processed.datagen.loot.ModBlogLootTables

object ModLootTableProvider {
    fun create(output: PackOutput): LootTableProvider {
        return LootTableProvider(output, setOf(), listOf(
            LootTableProvider.SubProviderEntry(::ModBlogLootTables, LootContextParamSets.BLOCK)
        ));
    }
}