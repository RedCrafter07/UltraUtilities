package redcrafter07.processed.item

enum class WrenchMode {
    Config, Upgrade, Rotate;

    fun next(): WrenchMode {
        return when (this) {
            Config -> Upgrade
            Upgrade -> Rotate
            Rotate -> Config
        }
    }

    fun previous(): WrenchMode {
        return when (this) {
            Config -> Rotate
            Upgrade -> Config
            Rotate -> Upgrade
        }
    }

    fun translation(): String {
        val prefix = "item.processed.wrench.mode"
        val suffix = when (this) {
            Config -> "config"
            Upgrade -> "upgrade"
            Rotate -> "rotate"
        }

        return "$prefix.$suffix"
    }

    fun save(): Byte {
        return when (this) {
            Config -> 0.toByte()
            Upgrade -> 1.toByte()
            Rotate -> 2.toByte()
            else -> 0.toByte()
        }
    }

    companion object {
        fun load(value: Byte): WrenchMode {
            return when (value.toInt()) {
                0 -> Config
                1 -> Upgrade
                2 -> Rotate
                else -> Config
            }
        }
    }
}