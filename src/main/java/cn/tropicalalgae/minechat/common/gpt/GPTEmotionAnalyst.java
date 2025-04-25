package cn.tropicalalgae.minechat.common.gpt;

import cn.tropicalalgae.minechat.common.enumeration.MessageType;
import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMessage;
import cn.tropicalalgae.minechat.common.model.impl.EntityAttribute;
import cn.tropicalalgae.minechat.utils.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import static cn.tropicalalgae.minechat.common.capability.ChatMemoryProvider.getChatMemory;
import static cn.tropicalalgae.minechat.common.capability.EntityAttributeProvider.getEntityAttribute;
import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.buildRequestBody;
import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.gptRun;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static cn.tropicalalgae.minechat.utils.Util.*;


public class GPTEmotionAnalyst implements Runnable {
    private final Entity subject;
    private final ServerPlayer target;
    private IEntityMemory<ChatMessage> memory = null;
    private EntityAttribute attribute = null;
    private List<ChatMessage> messages = null;

    public GPTEmotionAnalyst(ServerPlayer target, Entity subject) {
        this.subject = subject;
        this.target = target;
    }

    private String buildRequestBodyByHistory(int historyStart, int historyLength) {
        StringBuilder context = new StringBuilder();
        String newLine = System.lineSeparator();

        // 收集对话信息
        for (int i = historyStart; i < historyLength; i++) {
            ChatMessage msg = this.messages.get(i);
            context.append("对方：%s".formatted(msg.getMessage(true))).append(newLine);
            ChatMessage replyMsg = this.memory.getReplyMessageByUUID(msg.getUUID());
            context.append("你：%s".formatted(replyMsg.getMessage(true))).append(newLine);
        }

        // 构建请求
        JsonArray messages = new JsonArray();

        JsonObject sysMsgContent = new JsonObject();
        sysMsgContent.addProperty("role", "system");
        sysMsgContent.addProperty("content", SA_PROMPT);
        messages.add(sysMsgContent);

        JsonObject userMsgContent = new JsonObject();
        userMsgContent.addProperty("role", "user");
        userMsgContent.addProperty("content", context.toString());
        messages.add(userMsgContent);

        return buildRequestBody(messages);
    }

    @NotNull
    private  Boolean canAnalyzeEmotions() {
        if (isEntitySupported(this.subject, MessageType.CHAT)) {
            this.memory = getChatMemory(this.subject);
            this.attribute = getEntityAttribute(this.subject);
            if (this.memory != null && this.attribute != null) {
                this.messages = memory.getMessagesBySenderUUID(this.target.getUUID());
                return this.messages != null;
            }
            return false;
        }
        return false;
    }

    @Override
    public void run() {
        if (canAnalyzeEmotions()) {
            // 周期执行
            int length = this.messages.size();
            if (length % Config.AFFINITY_CONTEXT_LENGTH.get() == 0) {

                // 取1.2 * AFFINITY_CONTEXT_LENGTH的历史会话作为输入，确保判定不会完全遗忘历史
                int msgSize = (int) Math.ceil(Config.AFFINITY_CONTEXT_LENGTH.get() * 1.2);
                int start = Math.max(0, length - msgSize);

                // 推理，存储量化结果
                String requestBody = buildRequestBodyByHistory(start, length);
                String scoreStr = gptRun(requestBody);
                attribute.setEmotionScore(this.target.getUUID(), NumberUtils.toFloat(scoreStr, 0f));
            }
        }
    }
}