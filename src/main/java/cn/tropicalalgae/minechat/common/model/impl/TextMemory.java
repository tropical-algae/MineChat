package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.common.model.IChatMessage;
import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.utils.Config;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;

import static cn.tropicalalgae.minechat.utils.Util.getVillagePrompt;
import static cn.tropicalalgae.minechat.utils.Util.sysPromptSuffix;


public class TextMemory implements IEntityMemory<TextMessage> {
    private final Deque<TextMessage> history = new LinkedList<>();
    private String roleName = null;
    private String rolePrompt;
    private Boolean isInitialized = false;
    private Boolean hasRolePrompt;


    public TextMemory() {
        this.rolePrompt = Config.DEFAULT_PROMPT.get();
        this.isInitialized = true;
        this.hasRolePrompt = false;
    }

    @Override
    public void setRolePrompt(Entity entity) {
        if (entity instanceof Villager villager) {
            VillagerProfession profession = villager.getVillagerData().getProfession();
            String prompt = getVillagePrompt(profession);
            this.rolePrompt = ((prompt == null) || (prompt.equals(""))) ? this.rolePrompt : prompt;
//            Map<String, String> promptMap = MineChatConfig.VILLAGER_PROMPT.get();
//            if (promptMap != null && promptMap.containsKey(profession)) {
//                this.rolePrompt = promptMap.get(profession);
//            }
        }
        this.hasRolePrompt = true;
    }

    @Override
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

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
        TextMessage latestMsg = history.getLast();
        String sysPrompt = sysPromptSuffix.formatted(
                this.rolePrompt, latestMsg.time, this.roleName, latestMsg.role
        );

        JsonObject sysMsgContent = new JsonObject();
        sysMsgContent.addProperty("role", "system");
        sysMsgContent.addProperty("content", sysPrompt);
        messages.add(sysMsgContent);

        // 拼接历史记录
        for (TextMessage msg : this.history) {
            JsonObject msgContent = new JsonObject();
            msgContent.addProperty("role", msg.fromPlayer ? "user" : "assistant");
            msgContent.addProperty("content", msg.getMessage());
            if (msg.fromPlayer) {
                msgContent.addProperty("name", msg.role);
            }
            messages.add(msgContent);
        }

        root.addProperty("model", Config.GPT_MODEL.get());
        root.add("messages", messages);
        return new Gson().toJson(root);
    }

    @Override
    public void addNewMessage(IChatMessage newMessage) {
        if (this.history.size() >= Config.CONTEXT_LENGTH.get()) {
            history.pollFirst();
        }
        if (newMessage instanceof TextMessage) {
            this.history.addLast((TextMessage) newMessage);
        }
    }

    @Override
    public Deque<TextMessage> getHistory() {
        return history;
    }
}
