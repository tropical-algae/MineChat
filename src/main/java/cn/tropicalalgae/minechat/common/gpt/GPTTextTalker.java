package cn.tropicalalgae.minechat.common.gpt;


import cn.tropicalalgae.minechat.common.enumeration.MessageType;
import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMessage;
import cn.tropicalalgae.minechat.utils.Config;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import static cn.tropicalalgae.minechat.MineChat.LOGGER;
import static cn.tropicalalgae.minechat.common.capability.ChatMemoryProvider.getChatMemory;
import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.gptRun;
import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.gptRunContext;
import static cn.tropicalalgae.minechat.utils.Util.*;
import static cn.tropicalalgae.minechat.utils.Util.getEntityCustomName;


public class GPTTextTalker implements Runnable {
    private final ServerPlayer sender;
    private final Entity receiver;
    private final String message;
    private final MinecraftServer server;
    private IEntityMemory<ChatMessage> memory = null;


    public GPTTextTalker(ServerPlayer sender, Entity receiver, String message, MinecraftServer server){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.server = server;
        initReceiverName();
    }

    private static String buildEntityNameRequestBody() {
        JsonObject root = new JsonObject();
        JsonArray messages = new JsonArray();

        JsonObject msgContent = new JsonObject();
        msgContent.addProperty("role", "user");
        msgContent.addProperty("content",
                String.format(ENTITY_NAME_PROMPT.formatted(Config.USER_LANGUAGE.get().toString()))
        );

        messages.add(msgContent);
        root.addProperty("model", Config.GPT_MODEL.get());
        root.add("messages", messages);
        return new Gson().toJson(root);
    }

    private void messageBroadcast(Component replyComp) {
        for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(replyComp);
        }
    }

    @NotNull
    private Boolean canReceiverTalk() {
        if (isEntitySupported(this.receiver, MessageType.CHAT)) {
            this.memory = getChatMemory(this.receiver);
            return this.memory != null;
        }
        return false;
    }

    private void initReceiverName() {
        if (this.receiver.getCustomName() == null) {
            String receiverName = gptRun(buildEntityNameRequestBody());
            receiverName = (receiverName == null) ? "Tropical Algae" : receiverName;
            this.receiver.setCustomName(Component.literal(receiverName));
            LOGGER.info("Init entity name [%s]".formatted(receiverName));
        }
    }

    @Override
    public void run() {
        if (canReceiverTalk()){

            // 更新记忆（玩家消息）
            ChatMessage msgCont = new ChatMessage(this.sender, this.message, null);
            this.memory.addNewMessage(msgCont);
            String reply = gptRunContext(this.memory);

            // 更新记忆（模型消息）
            Component replyComp;
            if (reply != null) {
                ChatMessage rplCont = new ChatMessage(this.receiver, msgCont.getUUID(), reply);
                memory.addNewMessage(rplCont);
                replyComp = Component.literal("<%s>: %s".formatted(getEntityCustomName(this.receiver), reply));
            } else {
                // 更新失败（模型推理失败）
                reply = "[ERROR] MineChat inference failed. Please check your config!";
                replyComp = Component.literal(reply).withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                LOGGER.error("Error for model inference, latest message: %s".formatted(this.message));
            }
            // 广播消息
            messageBroadcast(replyComp);
        }
    }
}
