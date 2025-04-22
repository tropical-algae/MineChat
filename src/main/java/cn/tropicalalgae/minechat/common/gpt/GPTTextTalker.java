package cn.tropicalalgae.minechat.common.gpt;


import cn.tropicalalgae.minechat.common.capability.ModCapabilities;
import cn.tropicalalgae.minechat.common.model.IChatMessage;
import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.TextMessage;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static cn.tropicalalgae.minechat.MineChat.LOGGER;
import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.gptRun;
import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.gptRunContext;
import static cn.tropicalalgae.minechat.utils.Util.*;


public class GPTTextTalker implements Runnable {
//    private final ServerPlayer sender;
    private final String senderName;
    private final UUID senderUUID;

    private final Entity receiver;
    private final UUID receiverUUID;
    private final String message;
    private final MinecraftServer server;

    public GPTTextTalker(ServerPlayer sender, Entity receiver, String message, MinecraftServer server){
//        this.sender = sender;
        this.senderName = sender.getGameProfile().getName();
        this.senderUUID = sender.getUUID();
        this.receiver = receiver;
        this.receiverUUID = receiver.getUUID();

        this.message = message;
        this.server = server;
    }


    private static String buildEntityNameRequestBody(String entityType) {
        JsonObject root = new JsonObject();
        JsonArray messages = new JsonArray();

        JsonObject msgContent = new JsonObject();
        msgContent.addProperty("role", "user");
        msgContent.addProperty("content",
                String.format(ENTITY_NAME_PROMPT.formatted(entityType, Config.USER_LANGUAGE.toString()))
        );

        messages.add(msgContent);
        root.addProperty("model", Config.GPT_MODEL.get());
        root.add("messages", messages);
        return new Gson().toJson(root);
    }

    @Override
    public void run() {
        if (isEntitySupportText(this.receiver)) {
            // 尝试获取memory
            this.receiver.getCapability(ModCapabilities.TEXT_MEMORY).ifPresent(memory -> {
                String receiverName = memory.getRoleName();

                if (memory.isInitialized()) {
                    // 运行时，首次对话尝试命名
                    if (receiverName == null) {
                        receiverName = gptRun(buildEntityNameRequestBody(this.receiver.getType().toString()));
                        receiverName = (receiverName == null) ? "Tropical Algae" : receiverName;
                        memory.setRoleName(receiverName);
                        LOGGER.info("Init entity name [%s]".formatted(receiverName));
                    }
                    // 运行时，首次尝试赋予职业prompt
                    if (!memory.hasRolePrompt()) {
                        memory.setRolePrompt(this.receiver);
                        LOGGER.info("Init prompt for entity [%s]".formatted(memory.getRoleName()));
                    }
                }
                // 更新记忆（玩家消息）
                TextMessage msgCont = new TextMessage(
                        this.senderName, this.senderUUID, null, this.message, true
                );
                memory.addNewMessage(msgCont);
                String reply = gptRunContext(memory);

                Component replyComp;
                if (reply != null) {
                    // 更新记忆（模型消息），广播消息
                    TextMessage rplCont = new TextMessage(
                            receiverName, this.receiverUUID, msgCont.getUUID(), reply, false
                    );
                    memory.addNewMessage(rplCont);
                    replyComp = Component.literal("<%s>: %s".formatted(memory.getRoleName(), reply));
                } else {
                    // 播报错误
                    reply = "[ERROR] MineChat inference failed. Please check your config!";
                    replyComp = Component.literal(reply)
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                    LOGGER.error("Error for model inference, latest message: %s".formatted(this.message));
                }

                for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
                    player.sendSystemMessage(replyComp);
                }
            });
        }
    }


}
