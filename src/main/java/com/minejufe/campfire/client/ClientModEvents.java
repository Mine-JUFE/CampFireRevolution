package com.minejufe.campfire.client;

import com.minejufe.campfire.block.ModBlockEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientModEvents {
    // 方块实体渲染器
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {

        event.registerBlockEntityRenderer(
                ModBlockEntities.GREAT_CAMPFIRE_BE.get(),
                context -> new CampfireRenderer<>(context, entity -> entity.inventory));
    }
}
