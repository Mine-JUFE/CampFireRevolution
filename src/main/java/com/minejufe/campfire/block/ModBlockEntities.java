package com.minejufe.campfire.block;

import com.minejufe.campfire.CampfireRevolution;
import com.minejufe.campfire.block.entity.GreatCampfireBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
        // 方块实体延迟注册器
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
                        .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CampfireRevolution.MODID);

        // 绑定方块到方块实体上
        public static final Supplier<BlockEntityType<GreatCampfireBlockEntity>> GREAT_CAMPFIRE_BE = BLOCK_ENTITIES
                        .register(
                                        "great_campfire_be",
                                        () -> new BlockEntityType<>(GreatCampfireBlockEntity::new,
                                                        ModBlocks.GREAT_CAMPFIRE.get(),
                                                        ModBlocks.GOODNESS_CAMPFIRE.get()));
}