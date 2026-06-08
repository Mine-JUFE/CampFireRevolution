package com.minejufe.campfire.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import com.minejufe.campfire.CampfireRevolution;
import com.minejufe.campfire.block.ModBlocks;

public class ModItems {
        // 物品延迟注册器
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CampfireRevolution.MODID);

        // 营火核心 — 用于合成各种营火
        public static final DeferredItem<Item> CAMPFIRE_CORE = ITEMS.registerItem("campfire_core",
                        Item::new,
                        properties -> properties.stacksTo(16));

        public static final DeferredItem<BlockItem> BASIC_CAMPFIRE_ITEM = ITEMS.registerSimpleBlockItem(
                        "great_campfire",
                        ModBlocks.GREAT_CAMPFIRE);

        public static final DeferredItem<BlockItem> GOODNESS_CAMPFIRE_ITEM = ITEMS
                        .registerSimpleBlockItem("goodness_campfire", ModBlocks.GOODNESS_CAMPFIRE);
}
