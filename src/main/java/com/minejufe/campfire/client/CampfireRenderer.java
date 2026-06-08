package com.minejufe.campfire.client;

import java.util.List;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class CampfireRenderer<T extends BlockEntity>
        implements BlockEntityRenderer<T, CampfireRenderer.CampfireRenderState> {

    public static class CampfireRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState[] items = new ItemStackRenderState[8];

        public int packedLight;

        public CampfireRenderState() {
            for (int i = 0; i < 8; i++) {
                items[i] = new ItemStackRenderState();
            }
        }
    }

    private final ItemModelResolver itemModelResolver;
    private final Function<T, ItemStacksResourceHandler> inventoryProvider;

    public CampfireRenderer(BlockEntityRendererProvider.Context context,
            Function<T, ItemStacksResourceHandler> inventoryProvider) {
        this.itemModelResolver = context.itemModelResolver();
        this.inventoryProvider = inventoryProvider;
    }

    @Override
    public CampfireRenderState createRenderState() {
        return new CampfireRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, CampfireRenderState state, float partialTicks,
            Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {

        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        if (blockEntity.getLevel() != null) {
            int blockLight = blockEntity.getLevel().getBrightness(LightLayer.BLOCK, blockEntity.getBlockPos().above());
            int skyLight = blockEntity.getLevel().getBrightness(LightLayer.SKY, blockEntity.getBlockPos().above());
            state.packedLight = (skyLight << 20) | (blockLight << 4);
        }

        ItemStacksResourceHandler inventory = this.inventoryProvider.apply(blockEntity);

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getAmountAsInt(i) > 0) {
                ItemStack stack = inventory.getResource(i).toStack();

                // 使用营火的坐标和物品槽位生成一个固定的随机种子,用于物品模型的随机变化
                int seed = (int) blockEntity.getBlockPos().asLong() + i;

                this.itemModelResolver.updateForTopItem(
                        state.items[i],
                        stack,
                        ItemDisplayContext.FIXED,
                        blockEntity.getLevel(),
                        null,
                        seed);

            } else {
                state.items[i].clear();
            }
        }
    }

    @Override
    public void submit(CampfireRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera) {

        for (int i = 0; i < state.items.length; i++) {
            if (state.items[i].isEmpty())
                continue;

            poseStack.pushPose();

            poseStack.translate(0.5F, 0.52F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(i * 45.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(-0.3125F, -0.3125F, 0.0F);
            poseStack.scale(0.37F, 0.37F, 0.37F);

            state.items[i].submit(poseStack, submitNodeCollector, state.packedLight, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }
    }
}