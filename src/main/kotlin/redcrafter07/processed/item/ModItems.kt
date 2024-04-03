package redcrafter07.processed.item

import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredRegister
import redcrafter07.processed.ProcessedMod
import java.util.function.Supplier


//looked at the wrong file lmao

// but you gotta say this is way easier than normal java

//your implementation is correct fyi
object ModItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.createItems(ProcessedMod.ID);

    fun <T: Item> registerItem(name: String, item: Supplier<T>) {
        ITEMS.register(name, item);
    }
}