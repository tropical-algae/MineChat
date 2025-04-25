package cn.tropicalalgae.minechat.common.events;


import cn.tropicalalgae.minechat.MineChat;
import cn.tropicalalgae.minechat.common.capability.ChatMemoryProvider;
import cn.tropicalalgae.minechat.common.capability.EntityAttributeProvider;
import cn.tropicalalgae.minechat.common.enumeration.MessageType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;

import static cn.tropicalalgae.minechat.MineChat.LOGGER;
import static cn.tropicalalgae.minechat.utils.Util.isEntitySupported;


@Mod.EventBusSubscriber(modid = MineChat.MOD_ID)
@SuppressWarnings("removal")
public class CapabilityAttachEvent {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!event.getObject().level().isClientSide) {
            Entity entity = event.getObject();
            // 加载Chat Memory
            if (isEntitySupported(entity, MessageType.CHAT)) {
                LOGGER.debug("Init entity memory [ChatMessage], target UUID: %s".formatted(entity.getUUID().toString()));
                event.addCapability(
                        new ResourceLocation(MineChat.MOD_ID, "chat_memory"),
                        new ChatMemoryProvider(entity)
                );
                event.addCapability(
                        new ResourceLocation(MineChat.MOD_ID, "entity_attribute"),
                        new EntityAttributeProvider()
                );
            }
//            // 加载Event Memory
//            if (isEntitySupported(entity, MessageType.EVENT)) {
//                LOGGER.debug("Init entity memory [ChatMessage], target UUID: %s".formatted(entity.getUUID().toString()));
//                event.addCapability(
//                        new ResourceLocation(MineChat.MOD_ID, "event_memory"),
//                        new ChatMemoryProvider(entity)
//                );
//            }
        }
    }
}