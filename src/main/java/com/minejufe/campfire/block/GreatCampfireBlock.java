package com.minejufe.campfire.block;

import com.minejufe.campfire.block.entity.GreatCampfireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nullable;

public class GreatCampfireBlock extends CampfireBlock {

    public GreatCampfireBlock(boolean spawnParticles, int fireDamage, BlockBehaviour.Properties properties) {
        super(spawnParticles, fireDamage, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GreatCampfireBlockEntity(pos, state);
    }

    // 绑定游戏刻
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide())
            return null;

        return createTickerHelper(pBlockEntityType,
                ModBlockEntities.GREAT_CAMPFIRE_BE.get(),
                GreatCampfireBlockEntity::serverTick);
    }

    // override右键事件
    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
            Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pLevel.getBlockEntity(pPos) instanceof GreatCampfireBlockEntity blockEntity) {

            if (!pState.getValue(LIT) || pStack.isEmpty()) {
                return InteractionResult.PASS;
            }

            // 服务端执行
            if (pLevel instanceof ServerLevel serverLevel) {

                SingleRecipeInput input = new SingleRecipeInput(pStack);

                var recipeManager = serverLevel.getServer().getRecipeManager();
                var recipe = recipeManager.getRecipeFor(RecipeType.CAMPFIRE_COOKING, input, serverLevel);

                if (recipe.isPresent()) {
                    for (int i = 0; i < blockEntity.inventory.size(); i++) {

                        if (blockEntity.inventory.getResource(i).isEmpty()) {

                            ItemResource inputResource = ItemResource.of(pStack);

                            blockEntity.inventory.set(i, inputResource, 1);

                            blockEntity.cookingTime[i] = recipe.get().value().cookingTime();
                            blockEntity.cookingProgress[i] = 0;

                            // 扣除非创造玩家手里的 1 个物品
                            if (!pPlayer.gameMode().isCreative()) {
                                pStack.shrink(1);
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            } else { // 客户端执行直接返回成功
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }
}