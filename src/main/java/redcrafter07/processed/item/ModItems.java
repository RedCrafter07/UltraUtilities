package redcrafter07.processed.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;
import redcrafter07.processed.materials.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ProcessedMod.ID);

    public static final DeferredItem<ModItem> BLITZ_ORB = registerItem("blitz_orb", () -> new ModItem(new Item.Properties(), "blitz_orb"));
    public static final DeferredItem<WrenchItem> WRENCH = registerItem("wrench", WrenchItem::new);

    public static final List<DeferredItem<MaterialItem>> DUST_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::getDustPath, MaterialItem.Dust::new);
    public static final List<DeferredItem<MaterialItem>> INGOT_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::getIngotPath, MaterialItem.Ingot::new);
    public static final List<DeferredItem<MaterialItem>> NUGGET_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::getNuggetPath, MaterialItem.Nugget::new);
    public static final List<DeferredItem<MaterialItem>> RAW_ITEMS = registerMaterialItem(Materials.MATERIALS, Material::getRawPath, MaterialItem.Raw::new);

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    public static <T extends MaterialItem> List<DeferredItem<T>> registerMaterialItem(List<Material> materials, Function<Material, String> nameSupplier, Function<Material, T> itemConstructor) {
        ArrayList<DeferredItem<T>> list = new ArrayList<>();

        for (Material material : materials) {
            String name = nameSupplier.apply(material);
            list.add(ITEMS.register(name, () -> itemConstructor.apply(material)));
        }

        return list;
    }
}