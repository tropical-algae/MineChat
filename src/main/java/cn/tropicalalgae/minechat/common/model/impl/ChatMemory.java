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

import static cn.tropicalalgae.minechat.utils.Util.getEntityPrompt;
import static cn.tropicalalgae.minechat.utils.Util.SYS_PROMPT_SUFFIX;


public class ChatMemory implements IEntityMemory<ChatMessage> {
    /* 持久化参数 */
    private final List<ChatMessage> history = new ArrayList<>();
    private String roleName = null;

    /* 动态获取de参数 */
    private String rolePrompt;
    private final Map<UUID, ChatMessage> messageMapID = new HashMap<>();
    private final Map<UUID, List<ChatMessage>> messageMapSender = new HashMap<>();
    private final Map<UUID, UUID> messageReplyMap = new HashMap<>(); // 消息之间的回复关系 消息：消息的回复

    /* 状态判断参数 */
    private Boolean isInitialized = false;
    private Boolean hasRolePrompt;


    public ChatMemory() {
        this.rolePrompt = Config.DEFAULT_PROMPT.get();
        this.isInitialized = true;
        this.hasRolePrompt = false;
    }

    @Override
    public void setRolePrompt(Entity entity) {
        this.rolePrompt = getEntityPrompt(entity);
        this.hasRolePrompt = true;
    }

    @Override
    public void setRoleName(String roleName) { this.roleName = roleName; }

    @Override
    @Nullable
    public String getRoleName() {
        return this.roleName;
    }

    public Boolean hasRolePrompt() { return this.hasRolePrompt; }

    public Boolean isInitialized() {
        return this.isInitialized;
    }

    @Override
    public String getChatRequestBody() {

        // 构建请求param
        JsonObject root = new JsonObject();
        JsonArray messages = new JsonArray();

        // 设置系统提示词
        ChatMessage latestMsg = this.history.get(this.history.size() - 1);
        String sysPrompt = SYS_PROMPT_SUFFIX.formatted(
                this.rolePrompt,
                latestMsg.time,
                this.roleName,
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
        root.addProperty("model", Config.GPT_MODEL.get());
        root.add("messages", messages);
        return new Gson().toJson(root);
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
        return this.messageMapID.get(messageUUID);
    }

    @Override
    public ChatMessage getReplyMessageByUUID(UUID messageUUID) {
        UUID replyMessageUUID = this.messageReplyMap.get(messageUUID);
        if (replyMessageUUID != null) {
            return getMessageByUUID(replyMessageUUID);
        }
        return null;
    }

    @Override
    public List<ChatMessage> getMessagesBySenderUUID(UUID senderUUID) {
        return this.messageMapSender.get(senderUUID);
    }
}
