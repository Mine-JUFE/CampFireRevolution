package com.minejufe.campfire.block;

import com.minejufe.campfire.CampfireRevolution;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    // 延迟注册器
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CampfireRevolution.MODID);

    // 注册方块
    public static final DeferredBlock<GreatCampfireBlock> GREAT_CAMPFIRE = BLOCKS.<GreatCampfireBlock>registerBlock(
            "great_campfire",
            p -> new GreatCampfireBlock(true, 1, p),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> state.getValue(CampfireBlock.LIT) ? 15 : 0)
                    .noOcclusion());

}