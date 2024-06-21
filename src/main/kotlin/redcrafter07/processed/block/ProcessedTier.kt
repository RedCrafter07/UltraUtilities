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

    fun save(name: String, nbt: CompoundTag) {
        val tag = CompoundTag()
        tag.putInt("tier", tier)
        tag.putInt("speed_mul", multiplier_speed)
        tag.putInt("energy_mul", multiplier_energy)
        nbt.put(name, tag)
    }

    fun getMaxPower(): Int {
        return multiplier_energy * 32
    }

    companion object {
        fun load(name: String, nbt: CompoundTag): ProcessedTier {
            val tag = nbt.get(name)
            if (tag !is CompoundTag) return ProcessedTier(1, 1, 1)
            return ProcessedTier(tag.getInt("tier"), tag.getInt("speed_mul"), tag.getInt("energy_mul"))
        }
    }
}

object ProcessedTiers {
    val machine = setOf(
        ProcessedTier(1, 1, 1), // Rudimentary
        ProcessedTier(2, 3, 4), // Basic
        ProcessedTier(3, 9, 16), // Advanced
        ProcessedTier(4, 27, 64), //
        ProcessedTier(5, 81, 256), // Nuclear
        ProcessedTier(6, 243, 1024), // Quantum
        ProcessedTier(7, 729, 4096), // Final
    )
}