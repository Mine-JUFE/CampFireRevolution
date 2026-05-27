package com.minejufe.campfire.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import com.minejufe.campfire.CampfireRevolution;

public class ModItems {
    // 物品延迟注册器
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CampfireRevolution.MODID);

    public static final DeferredItem<Item> CAMPFIRE_CORE = ITEMS.registerItem("campfire_core",
            Item::new,
            properties -> properties.stacksTo(16));

    public static final DeferredItem<BlockItem> BASIC_CAMPFIRE_ITEM = ITEMS.registerSimpleBlockItem("great_campfire",
            com.minejufe.campfire.block.ModBlocks.GREAT_CAMPFIRE);
}
