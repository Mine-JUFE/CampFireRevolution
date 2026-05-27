package com.minejufe.campfire.block.entity;

import com.minejufe.campfire.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class GreatCampfireBlockEntity extends BlockEntity {
    // 定义容量大小
    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(5);
    // 记录每个格子的加工进度和需要的总时间
    public final int[] cookingProgress = new int[inventory.size()];
    public final int[] cookingTime = new int[inventory.size()];

    public GreatCampfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GREAT_CAMPFIRE_BE.get(), pos, state);
    }

    // 每刻执行
    public static void serverTick(Level level, BlockPos pos, BlockState state,
            GreatCampfireBlockEntity blockEntity) {
        // 如果营火没被点燃，则跳过
        if (!state.getValue(CampfireBlock.LIT))
            return;

        if (!(level instanceof ServerLevel serverLevel))
            return;

        boolean didCook = false;

        // 遍历 4 个格子
        for (int i = 0; i < blockEntity.inventory.size(); i++) {
            ItemStack stack = blockEntity.inventory.getResource(i).toStack();
            if (!stack.isEmpty()) {
                didCook = true;
                blockEntity.cookingProgress[i]++;

                // 如果完成
                if (blockEntity.cookingProgress[i] >= blockEntity.cookingTime[i]) {
                    // 查询合成表，获取加工物品
                    SingleRecipeInput input = new SingleRecipeInput(stack);
                    var recipe = serverLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING,
                            input,
                            serverLevel);
                    if (recipe.isPresent()) {
                        ItemStack result = recipe.get().value().assemble(input);
                        // 照搬原版掉落物品实体代码
                        Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), result);
                        // 清空这个位置的物品
                        blockEntity.inventory.set(i, ItemResource.EMPTY, 0);
                    }
                }
            }
        }

        // 如果发生了状态改变，通知区块保存数据
        if (didCook) {
            blockEntity.setChanged();
        }
    }
}