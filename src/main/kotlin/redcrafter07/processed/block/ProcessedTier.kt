package redcrafter07.processed.block

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

class ProcessedTier(val tier: Int, val multiplier_speed: Int, val multiplier_energy: Int) {
    fun named(): String {
        return "tier_$tier"; }

    fun translated(): Component {
        return Component.translatable("processed.${named()}")
    }

    fun colored(): Component {
        return Component.translatable("processed.${named()}.colored")
    }

    fun getMaxPower(): Int {
        return multiplier_energy * 32
    }
}

object ProcessedTiers {
    val machine = listOf(
        ProcessedTier(1, 1, 1), // Rudimentary
        ProcessedTier(2, 3, 4), // Basic
        ProcessedTier(3, 9, 16), // Advanced
        ProcessedTier(4, 27, 64), //
        ProcessedTier(5, 81, 256), // Nuclear
        ProcessedTier(6, 243, 1024), // Quantum
        ProcessedTier(7, 729, 4096), // Final
    )
}