package com.minejufe.campfire.block.entity;

import javax.annotation.Nullable;

import com.minejufe.campfire.block.ModBlockEntities;
import com.minejufe.campfire.block.entity.SyncedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class GreatCampfireBlockEntity extends SyncedBlockEntity {
    // 定义容量大小
    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(8);
    // 记录每个格子的加工进度和需要的总时间
    public final int[] cookingProgress = new int[8];
    public final int[] cookingTime = new int[8];

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

        // 遍历每个栏位
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

                        serverLevel.sendBlockUpdated(pos, state, state, 3);
                    }
                }
            }
        }

        // 如果发生了状态改变，保存数据
        if (didCook) {
            blockEntity.setChanged();
        }
    }

    // 存放在营火中的物品数据
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        // 转换成itemstack以备调用
        NonNullList<ItemStack> list = NonNullList
                .withSize(this.inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.inventory.size(); i++) {
            if (this.inventory.getAmountAsInt(i) > 0) {
                list.set(i, this.inventory.getResource(i).toStack());
            }
        }

        // 保存加工进度
        for (int i = 0; i < this.cookingProgress.length; i++) {
            output.putInt("CookingProgress_" + i, this.cookingProgress[i]);
            output.putInt("CookingTime_" + i, this.cookingTime[i]);
        }
        // 序列化营火中的物品
        ContainerHelper.saveAllItems(output.child("Items"), list, true);
    }

    // 读取在营火里的物品数据
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        NonNullList<ItemStack> list = NonNullList
                .withSize(this.inventory.size(), ItemStack.EMPTY);

        input.child("Items").ifPresent(it -> ContainerHelper.loadAllItems(it, list));

        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isEmpty()) {
                this.inventory.set(i, ItemResource.of(list.get(i)),
                        list.get(i).getCount());
            } else {
                this.inventory.set(i, ItemResource.EMPTY, 0);
            }
        }

        for (int i = 0; i < this.cookingProgress.length; i++) {
            this.cookingProgress[i] = input.getIntOr("CookingProgress_" + i, 0);
            this.cookingTime[i] = input.getIntOr("CookingTime_" + i, 0);
        }
    }

    // 破坏后掉落物品
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {

        super.preRemoveSideEffects(pos, state);
        if (this.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {

            for (int i = 0; i < this.inventory.size(); i++) {
                if (this.inventory.getAmountAsInt(i) > 0) {

                    ItemStack stack = this.inventory.getResource(i).toStack();
                    Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(), stack);
                    // 清空物品栏
                    this.inventory.set(i, net.neoforged.neoforge.transfer.item.ItemResource.EMPTY, 0);
                }
            }
        }
    }
}