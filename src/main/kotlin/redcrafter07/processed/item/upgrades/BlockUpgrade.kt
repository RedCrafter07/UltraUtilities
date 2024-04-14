package redcrafter07.processed.item.upgrades

enum class BlockUpgrade {
    Speed, Efficiency, Capacity, Range, Energy, AreaOfEffect;

    companion object {
        private var validUpgrades: Array<BlockUpgrade> = entries.toTypedArray()

        fun fromString(string: String): BlockUpgrade {
            return valueOf(string)
        }

        fun fromInt(int: Int): BlockUpgrade {
            return entries[int]
        }

        fun validUpgrades(upgrades: Array<BlockUpgrade>): Array<BlockUpgrade> {
            this.validUpgrades = entries.filter { it !in upgrades }.toTypedArray()
            return entries.filter { it !in upgrades }.toTypedArray()
        }

        fun getItemNameForUpgrade(upgrade: BlockUpgrade): String {
            val uppercaseIndexes =
                upgrade.toString().toCharArray().mapIndexed { index, c -> if (c.isUpperCase()) index else null }
                    .filterNotNull()
            val name = upgrade.toString().toCharArray().toMutableList()

            uppercaseIndexes.forEach { name.add(it, '_') }

            return name.joinToString("").lowercase()
        }
    }

    fun upgradeIsValid(): Boolean {
        return this in validUpgrades
    }

    fun toInt(): Int {
        return entries.indexOf(this)
    }

    fun getItemNameForUpgrade(): String {
        val uppercaseIndexes =
            this.toString().toCharArray().mapIndexed { index, c -> if (c.isUpperCase()) index else null }
                .filterNotNull()
        val name = this.toString().toCharArray().toMutableList()

        uppercaseIndexes.forEach { name.add(it, '_') }

        return name.joinToString("").lowercase()
    }
}