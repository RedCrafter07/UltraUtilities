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

    fun save(): UShort {
        return when (this) {
            Config -> 0.toUShort()
            Upgrade -> 1.toUShort()
            Rotate -> 2.toUShort()
            else -> 0.toUShort()
        }
    }

    fun load(value: UShort): WrenchMode {
        return when (value.toInt()) {
            0 -> Config
            1 -> Upgrade
            2 -> Rotate
            else -> Config
        }
    }
}