package com.minejufe.campfire;

import org.slf4j.Logger;

import com.minejufe.campfire.block.GoodnessCampfireBlock;
import com.minejufe.campfire.item.ModItems;
import com.minejufe.campfire.item.ModCreativeTabs;
import com.minejufe.campfire.block.ModBlockEntities;
import com.minejufe.campfire.block.ModBlocks;
import com.minejufe.campfire.client.ClientModEvents;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CampfireRevolution.MODID)
public class CampfireRevolution {
    public static final String MODID = "campfirerevolution";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, MODID);

    // 上帝的营火 — 方块（registerBlock 会注入 .setId() 再到 Properties）
    public static final DeferredBlock<GoodnessCampfireBlock> GOODNESS_CAMPFIRE =
            BLOCKS.registerBlock("goodness_campfire", GoodnessCampfireBlock::new);
    // 上帝的营火 — 方块物品
    public static final DeferredItem<BlockItem> GOODNESS_CAMPFIRE_ITEM =
            ITEMS.registerSimpleBlockItem("goodness_campfire", GOODNESS_CAMPFIRE);

    // The constructor for the mod class is the first code that is run when your mod
    // is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and
    // pass them in automatically.

    public CampfireRevolution(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEventBus.addListener(this::onBlockEntityTypeAddBlocks);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        // 挂载渲染注册事件
        if (FMLEnvironment.getDist().isClient()) {
            modEventBus.addListener(ClientModEvents::registerBER);
        }
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class
        // (CampfireRevolution) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in
        // this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /**
     * 将 GoodnessCampfireBlock 注册到原版 CAMPFIRE BlockEntityType，
     * 使其能正常使用营火的烹饪逻辑。
     */
    private void onBlockEntityTypeAddBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.CAMPFIRE, GOODNESS_CAMPFIRE.get());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());
        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("CampFire Loading...");
    }
}
