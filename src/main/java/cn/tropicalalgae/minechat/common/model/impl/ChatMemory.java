package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.common.model.IEntityMessage;
import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.utils.Config;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

import javax.annotation.Nullable;
import java.util.*;

import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.buildRequestBody;
import static cn.tropicalalgae.minechat.utils.Util.*;


public class ChatMemory implements IEntityMemory<ChatMessage> {
    private final Entity entity;
    /* 持久化参数 */
    private final List<ChatMessage> history = new ArrayList<>();

    /* 动态获取de参数 */
    private final Map<UUID, ChatMessage> messageMapID = new HashMap<>();
    private final Map<UUID, List<ChatMessage>> messageMapSender = new HashMap<>();
    private final Map<UUID, UUID> messageReplyMap = new HashMap<>(); // 消息之间的回复关系 消息：消息的回复

    /* 状态判断参数 */
    private Boolean isInitialized = false;


    public ChatMemory(Entity entity) {
        this.entity = entity;
        this.isInitialized = true;
    }

    public Boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public String getChatRequestBody() {
        // 构建请求param
        JsonArray messages = new JsonArray();

        // 设置系统提示词
        ChatMessage latestMsg = this.history.get(this.history.size() - 1);
        String sysPrompt = SYS_PROMPT_SUFFIX.formatted(
                getEntityPrompt(entity),
                latestMsg.time,
                getEntityCustomName(this.entity),
                latestMsg.senderName,
                Config.USER_LANGUAGE.get().toString()
        );

        JsonObject sysMsgContent = new JsonObject();
        sysMsgContent.addProperty("role", "system");
        sysMsgContent.addProperty("content", sysPrompt);
        messages.add(sysMsgContent);

        // 拼接历史记录
        int length = history.size();
        int start = Math.max(0, length - Config.CONTEXT_LENGTH.get());
        for (int i = start; i < length; i++) {
            ChatMessage msg = history.get(i);
            JsonObject msgContent = new JsonObject();
            msgContent.addProperty("role", msg.fromPlayer ? "user" : "assistant");
            msgContent.addProperty("content", msg.getMessage(false));
            if (msg.fromPlayer) {
                msgContent.addProperty("name", msg.senderName);
            }
            messages.add(msgContent);
        }
        return buildRequestBody(messages); // new Gson().toJson(root);
    }

    @Override
    public void addNewMessage(IEntityMessage newMessage) {
        // TODO 添加高性能的历史截断方法
        if (newMessage instanceof ChatMessage chatMessage) {
            if (chatMessage.getRepliedUUID() != null) {
                this.messageReplyMap.put(chatMessage.getRepliedUUID(), newMessage.getUUID());
            }
            this.history.add(chatMessage);
            this.messageMapID.put(chatMessage.getUUID(), chatMessage);
            this.messageMapSender
                    .computeIfAbsent(chatMessage.getSenderUUID(), k -> new ArrayList<>())
                    .add(chatMessage);
        }
    }

    @Override
    public List<ChatMessage> getHistory() {
        return this.history;
    }

    @Override
    public ChatMessage getMessageByUUID(UUID messageUUID) {
        return this.messageMapID.getOrDefault(messageUUID, null);
    }

    @Override
    public ChatMessage getReplyMessageByUUID(UUID messageUUID) {
        UUID replyMessageUUID = this.messageReplyMap.getOrDefault(messageUUID, null);
        if (replyMessageUUID != null) {
            return getMessageByUUID(replyMessageUUID);
        }
        return null;
    }

    @Override
    public List<ChatMessage> getMessagesBySenderUUID(UUID senderUUID) {
        return this.messageMapSender.getOrDefault(senderUUID, null);
    }
}
