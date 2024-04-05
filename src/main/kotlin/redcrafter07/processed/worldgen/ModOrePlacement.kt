package redcrafter07.processed.worldgen

import net.minecraft.world.level.levelgen.placement.CountPlacement
import net.minecraft.world.level.levelgen.placement.InSquarePlacement
import net.minecraft.world.level.levelgen.placement.PlacementModifier
import net.minecraft.world.level.levelgen.placement.RarityFilter

object ModOrePlacement {
    fun orePlacement(modifier1: PlacementModifier, modifier2: PlacementModifier): List<PlacementModifier> {
        return listOf(modifier1, InSquarePlacement.spread(), modifier2, InSquarePlacement.spread());
    }

    fun commonOrePlacement(pCount: Int, pHeightRange: PlacementModifier): List<PlacementModifier> {
        return orePlacement(CountPlacement.of(pCount), pHeightRange);
    }

    fun rareOrePlacement(pChance: Int, pHeightRange: PlacementModifier): List<PlacementModifier> {
        return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
    }
}