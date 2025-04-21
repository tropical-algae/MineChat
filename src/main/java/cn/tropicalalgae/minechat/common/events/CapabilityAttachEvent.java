package cn.tropicalalgae.minechat.common.events;


import cn.tropicalalgae.minechat.MineChat;
import cn.tropicalalgae.minechat.common.capability.TextMemoryProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;

import static cn.tropicalalgae.minechat.MineChat.LOGGER;
import static cn.tropicalalgae.minechat.utils.Util.isEntitySupportText;


@Mod.EventBusSubscriber(modid = MineChat.MOD_ID)
@SuppressWarnings("removal")
public class CapabilityAttachEvent {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!event.getObject().level().isClientSide) {
            Entity entity = event.getObject();
            if (isEntitySupportText(entity)) {
                LOGGER.debug("Init entity memory [TextMessage], target UUID: %s".formatted(entity.getUUID().toString()));
                event.addCapability(
                        new ResourceLocation(MineChat.MOD_ID, "text_memory"),
                        new TextMemoryProvider()
                );
            }
        }
    }
}