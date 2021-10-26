package com.redcrafter07.processed.item;

import com.redcrafter07.processed.Processed;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Processed.MOD_ID);


    public static final RegistryObject<Item> CRAFTING_PROCESSOR = ITEMS.register("crafting_processor", () -> new Item(new Item.Properties().group(ModItemGroup.UU_GROUP)));
    public static final RegistryObject<Item> PROCESSOR_CORE = ITEMS.register("processor_core", () -> new Item(new Item.Properties().group(ModItemGroup.UU_GROUP)));

    public static final RegistryObject<Item> PROCESSOR_SWORD = ITEMS.register("processor_sword", () -> new ProcessorSwordItem(ModItemTier.PROCESSOR, 2, 3f, new Item.Properties().group(ModItemGroup.UU_TOOLS).maxStackSize(1)));

    public static final RegistryObject<Item> OVERLOAD_PROCESSOR = ITEMS.register("overload_processor", () -> new Item(new Item.Properties().group(ModItemGroup.UU_GROUP)));
    public static final RegistryObject<Item> POWERED_OVERLOAD_PROCESSOR = ITEMS.register("powered_overload_processor", () -> new Item(new Item.Properties().group(ModItemGroup.UU_GROUP)));
    public static final RegistryObject<Item> CHARGED_OVERLOAD_PROCESSOR = ITEMS.register("charged_overload_processor", () -> new Item(new Item.Properties().group(ModItemGroup.UU_GROUP)));
    public static final RegistryObject<Item> DEACTIVATED_CHARGED_OVERLOAD_PROCESSOR = ITEMS.register("deactivated_charged_overload_processor", () -> new DeactivatedChargedOverloadProcessor(new Item.Properties().group(ModItemGroup.UU_GROUP)));
    public static final RegistryObject<Item> OVERLOAD_INGOT = ITEMS.register("overload_ingot", () -> new Item(new Item.Properties().group(ModItemGroup.UU_GROUP)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}