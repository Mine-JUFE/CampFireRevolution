package com.minejufe.campfire.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import com.minejufe.campfire.CampfireRevolution;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CampfireRevolution.MODID);

    // 营火核心 — 用于合成各种营火
    public static final DeferredItem<Item> CAMPFIRE_CORE = ITEMS.registerItem("campfire_core",
            Item::new,
            properties -> properties.stacksTo(16));

    // 注意：goodness_campfire 已经改为方块，在 CampfireRevolution.java 中作为 BlockItem 注册
}
