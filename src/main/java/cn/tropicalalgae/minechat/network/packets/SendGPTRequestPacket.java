package cn.tropicalalgae.minechat.network.packets;

import cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager;
import cn.tropicalalgae.minechat.common.gpt.GPTTextTalker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.network.CustomPayloadEvent;


public class SendGPTRequestPacket {
    private final String message;
    private final String playerName;
    private final String targetUUID;

    public SendGPTRequestPacket(String message, String playerName, String targetUUID) {
        this.message = message;
        this.playerName = playerName;
        this.targetUUID = targetUUID;
    }

    public SendGPTRequestPacket(FriendlyByteBuf buf) {
        this.message = buf.readUtf();
        this.playerName = buf.readUtf();
        this.targetUUID = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.message);
        buf.writeUtf(this.playerName);
        buf.writeUtf(this.targetUUID);
    }

    public void handle(CustomPayloadEvent.Context context) {
        ServerPlayer sender = context.getSender();
        if (sender != null) {
            MinecraftServer server = sender.getServer();
            GPTTalkerManager.runAsync(
                    this.targetUUID,
                    new GPTTextTalker(this.message, this.playerName, this.targetUUID, server)
            );
        }
        context.setPacketHandled(true);
    }
}
