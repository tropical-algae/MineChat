package cn.tropicalalgae.minechat;

import cn.tropicalalgae.minechat.network.NetworkHandler;
import cn.tropicalalgae.minechat.utils.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

import java.util.List;

/* bu hui xie java, qing pen */

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MineChat.MOD_ID)
public class MineChat
{
    public static final String MOD_ID = "minechat";
    public static final Logger LOGGER = LogUtils.getLogger();


    @SuppressWarnings("removal")
    public MineChat()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onInit);
    }

    private void onInit(final FMLCommonSetupEvent event)
    {
        LOGGER.info("Init Mine Chat...");
        NetworkHandler.register(); // 注册通信通道和 packet
        LOGGER.info("Init done!");
    }

}
