package redcrafter07.processed.gui;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import redcrafter07.processed.ProcessedMod;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ProcessedMod.ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PoweredFurnaceMenu>> POWERED_FURNACE_MENU = register("powered_furnace_menu", PoweredFurnaceMenu::new);

    public static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(
            String name,
            MenuSupplier<T> supplier
    ) {
        return MENUS.register(name, () -> new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS));
    }

    public static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(
            String name,
            IContainerFactory<T> factory
    ) {
        return MENUS.register(name, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }
}