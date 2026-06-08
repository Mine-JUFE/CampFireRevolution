package com.minejufe.campfire.item;

import com.minejufe.campfire.CampfireRevolution;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, CampfireRevolution.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CAMPFIRE_TAB = CREATIVE_MODE_TABS
            .register("campfire_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.campfirerevolution"))
                    .icon(() -> ModItems.CAMPFIRE_CORE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // 从 ModItems 和 CampfireRevolution.ITEMS 收集所有物品
                        ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        CampfireRevolution.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    }).build());
}
