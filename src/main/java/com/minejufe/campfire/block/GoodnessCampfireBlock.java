package com.minejufe.campfire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import org.jspecify.annotations.Nullable;

import com.minejufe.campfire.block.entity.GreatCampfireBlockEntity;

/**
 * 上帝的营火 — 永恒燃烧、满亮度、赋予神圣效果。
 * <p>
 * 请在注册时通过
 * {@code BLOCKS.registerBlock("goodness_campfire", GoodnessCampfireBlock::new)}
 * 方式注册，以保证 NeoForge 能正确注入方块 id 到 Properties。
 */
public class GoodnessCampfireBlock extends CampfireBlock {

    /** 方块构造器不会被直接调用，由 DeferredRegister 通过 Properties 工厂注入。 */
    public GoodnessCampfireBlock(BlockBehaviour.Properties properties) {
        super(true, 2,
                properties.mapColor(MapColor.GOLD)
                        .strength(50.0F, 3600.0F) // 基岩级抗性
                        .sound(SoundType.NETHERITE_BLOCK) // 厚重的声音
                        .lightLevel(state -> 15) // 永远满亮度
                        .noOcclusion()
                        .requiresCorrectToolForDrops());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GreatCampfireBlockEntity(pos, state);
    }

    // ====== 永不熄灭 ======

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        // 神圣之火不会被雨浇灭
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility,
            boolean simulate) {
        // 阻止铲子熄灭营火
        if (itemAbility == ItemAbilities.SHOVEL_DOUSE) {
            return null;
        }
        return null; // 默认不做任何工具改造
    }

    // ====== 神圣效果 ======

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
            InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2, false, false, true));
            living.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 1, false, false, true));
            living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, true));
        }
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
    }

    // ====== 金色粒子 ======

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.8;
        double z = pos.getZ() + 0.5;

        // 三个金色光点
        for (int i = 0; i < 3; i++) {
            double dx = (random.nextDouble() - 0.5) * 0.6;
            double dz = (random.nextDouble() - 0.5) * 0.6;
            level.addParticle(ParticleTypes.END_ROD,
                    x + dx, y + random.nextDouble() * 0.4, z + dz,
                    0.0, 0.04, 0.0);
        }

        // 也保留原版火焰粒子
        super.animateTick(state, level, pos, random);
    }
}
