package com.minejufe.campfire.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import com.minejufe.campfire.CampfireRevolution;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CampfireRevolution.MODID);

    public static final DeferredItem<Item> CAMPFIRE_CORE = ITEMS.registerItem("campfire_core",
            Item::new,
            properties -> properties.stacksTo(16));
}
