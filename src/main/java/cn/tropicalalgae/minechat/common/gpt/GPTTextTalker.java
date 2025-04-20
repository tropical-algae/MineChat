package cn.tropicalalgae.minechat.common.gpt;


import cn.tropicalalgae.minechat.common.capability.ModCapabilities;
import cn.tropicalalgae.minechat.common.model.IChatMessage;
import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.TextMessage;
import cn.tropicalalgae.minechat.utils.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static cn.tropicalalgae.minechat.MineChat.LOGGER;
import static cn.tropicalalgae.minechat.utils.Util.*;


public class GPTTextTalker implements Runnable {
    private final String message;
    private final String playerName;
    private final String targetUUID;
    public MinecraftServer server;
    public GPTTextTalker(String message, String playerName, String targetUUID, MinecraftServer server){
        this.targetUUID = targetUUID;
        this.playerName = playerName;
        this.message = message;
        this.server = server;
    }

    @Override
    public void run() {
        if (this.server != null) {
            // 找到对话者
            Entity targetEntity = findEntityByUUID(this.server, this.targetUUID);
            if (isEntitySupportText(targetEntity)) {
                // 尝试获取memory
                targetEntity.getCapability(ModCapabilities.TEXT_MEMORY).ifPresent(memory -> {
                    String entityTypeName = targetEntity.getType().toString();
                    if (memory.isInitialized()) {
                        // 运行时，首次对话尝试命名
                        if (memory.getRoleName() == null) {
                            String entityName = run(buildEntityNameRequestBody(entityTypeName));
                            entityName = (entityName == null) ? "Tropical Algae" : entityName;
                            memory.setRoleName(entityName);
                            LOGGER.info("Init entity name [%s]".formatted(entityName));
                        }
                        // 运行时，首次尝试赋予职业prompt
                        if (!memory.hasRolePrompt()) {
                            memory.setRolePrompt(targetEntity);
                            LOGGER.info("Init prompt for entity [%s]".formatted(memory.getRoleName()));
                        }
                    }
                    // 模型推理（聊天）
                    memory.addNewMessage(new TextMessage(this.playerName, this.message, true));
                    String entityReply = runContext(memory);

                    Component replyMsg;
                    if (entityReply != null) {
                        // 更新记忆，广播消息
                        memory.addNewMessage(new TextMessage(memory.getRoleName(), entityReply, false));
                        replyMsg = Component.literal("<%s>: %s".formatted(memory.getRoleName(), entityReply));
                    } else {
                        // 播报错误
                        entityReply = "[ERROR] MineChat inference failed. Please check your config!";
                        replyMsg = Component.literal(entityReply)
                                .withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                        LOGGER.error("Error for model inference, latest message: %s".formatted(this.message));
                    }

                    for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
                        player.sendSystemMessage(replyMsg);
                    }
                });
            }
        }
    }

    @Nullable
    public static String run(String requestBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Config.GPT_API.get()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Config.GPT_KEY.get())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();
        try {
            // Get response
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return getOpenaiBasedResponseContent(response);
                //player.sendSystemMessage(Component.literal("<Villager%s>: %s".formatted(chatID, responseContent)));
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static <T extends IChatMessage> String runContext(IEntityMemory<T> memory) {
        String requestBody = memory.getChatRequestBody();
        return run(requestBody);
    }
}
