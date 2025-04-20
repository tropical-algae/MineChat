package cn.tropicalalgae.minechat.network.packets;

import cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager;
import cn.tropicalalgae.minechat.common.gpt.GPTTextTalker;
import cn.tropicalalgae.minechat.utils.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import static cn.tropicalalgae.minechat.utils.Util.canPlayerTalkToEntity;


public class SendGPTRequestPacket {
    private final String message;
//    private final String playerName;
    private final String targetUUID;

    public SendGPTRequestPacket(String message, String targetUUID) {
        this.message = message;
//        this.playerName = playerName;
        this.targetUUID = targetUUID;
    }

    public SendGPTRequestPacket(FriendlyByteBuf buf) {
        this.message = buf.readUtf();
//        this.playerName = buf.readUtf();
        this.targetUUID = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.message);
//        buf.writeUtf(this.playerName);
        buf.writeUtf(this.targetUUID);
    }

    public void handle(CustomPayloadEvent.Context context) {
        ServerPlayer sender = context.getSender();
        if (sender != null) {
            MinecraftServer server = sender.getServer();
            String playerName = sender.getGameProfile().getName();

            // 权限判断
            if (canPlayerTalkToEntity(playerName)){
                GPTTalkerManager.runAsync(
                        this.targetUUID,
                        new GPTTextTalker(this.message, playerName, this.targetUUID, server)
                );
            } else {
                Component message = Component.literal("You are unable to chat due to certain reasons.")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
                sender.sendSystemMessage(message);
            }
        }
        context.setPacketHandled(true);
    }
}
