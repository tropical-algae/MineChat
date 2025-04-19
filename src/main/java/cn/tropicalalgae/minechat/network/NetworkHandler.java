package cn.tropicalalgae.minechat.network;

import cn.tropicalalgae.minechat.MineChat;
import cn.tropicalalgae.minechat.network.packets.SendGPTRequestPacket;
import net.minecraftforge.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
public class NetworkHandler {
    private static final int PROTOCOL_VERSION = 1;

    @SuppressWarnings("removal")
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(
            new ResourceLocation(MineChat.MOD_ID, "main"))
            .clientAcceptedVersions((status, versions) -> true)
            .serverAcceptedVersions((status, versions) -> true)
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel();

    public static void register() {
        int index = 0;

        INSTANCE.messageBuilder(SendGPTRequestPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SendGPTRequestPacket::new)
                .encoder(SendGPTRequestPacket::toBytes)
                .consumerMainThread(SendGPTRequestPacket::handle)
                .add();
    }
    public static void sendToServer(Object msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }
}