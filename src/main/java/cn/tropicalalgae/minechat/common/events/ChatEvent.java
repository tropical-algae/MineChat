package cn.tropicalalgae.minechat.common.events;

import cn.tropicalalgae.minechat.MineChat;
import cn.tropicalalgae.minechat.network.NetworkHandler;
import cn.tropicalalgae.minechat.network.packets.SendGPTRequestPacket;
import cn.tropicalalgae.minechat.utils.Ways;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cn.tropicalalgae.minechat.MineChat.LOGGER;
import static cn.tropicalalgae.minechat.utils.Util.isEntitySupportText;


@Mod.EventBusSubscriber(modid = MineChat.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatEvent {
    @SubscribeEvent
    public static void onPlayerChat(ClientChatEvent event){

        Player player = Minecraft.getInstance().player;

        if (player != null) {
            Entity targetEntity = Ways.getPointedEntity(player, 64.0);

            if(isEntitySupportText(targetEntity)) {
                LOGGER.info("Prepare a conversation, target UUID: %s".formatted(targetEntity.getStringUUID()));
                NetworkHandler.sendToServer(
                        new SendGPTRequestPacket(
                                event.getMessage(),
                                targetEntity.getStringUUID()
                        )
                );
            }
        }

    }
}