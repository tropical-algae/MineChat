package cn.tropicalalgae.minechat.common.gpt;

import cn.tropicalalgae.minechat.common.capability.ModCapabilities;
import cn.tropicalalgae.minechat.common.enumeration.MessageType;
import cn.tropicalalgae.minechat.common.model.impl.ChatMessage;
import cn.tropicalalgae.minechat.utils.Config;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import static cn.tropicalalgae.minechat.common.gpt.GPTTalkerManager.gptRun;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

import static cn.tropicalalgae.minechat.utils.Util.*;


public class GPTEmotionAnalyst implements Runnable {
    private final Entity subject;
    private final ServerPlayer target;

    public GPTEmotionAnalyst(ServerPlayer target, Entity subject) {
        this.subject = subject;
        this.target = target;
    }

    private static String sentAnalysisRequestBody(String history) {
        // 构建请求param
        JsonObject root = new JsonObject();
        JsonArray messages = new JsonArray();

        // 设置系统提示词
        String sysPrompt = SA_PROMPT;

        JsonObject sysMsgContent = new JsonObject();
        sysMsgContent.addProperty("role", "system");
        sysMsgContent.addProperty("content", sysPrompt);
        messages.add(sysMsgContent);

        JsonObject userMsgContent = new JsonObject();
        userMsgContent.addProperty("role", "user");
        userMsgContent.addProperty("content", history);
        messages.add(userMsgContent);

        root.addProperty("model", Config.GPT_MODEL.get());
        root.add("messages", messages);
        return new Gson().toJson(root);
    }

    @Override
    public void run() {
        if (isEntitySupported(this.subject, MessageType.EVENT)) {
            // 尝试获取两人历史对话
            this.subject.getCapability(ModCapabilities.CHAT_MEMORY).ifPresent(memory -> {
                List<ChatMessage> messages = memory.getMessagesBySenderUUID(this.target.getUUID());
                if (messages == null) {
                    return ;
                }
                // 周期执行
                int length = messages.size();
                if (length % Config.AFFINITY_CONTEXT_LENGTH.get() == 0) {

                    // 取1.2 * AFFINITY_CONTEXT_LENGTH的历史会话作为输入，确保判定不会完全遗忘历史
                    int msgSize = (int) Math.ceil(Config.AFFINITY_CONTEXT_LENGTH.get() * 1.2);
                    int start = Math.max(0, length - msgSize);
                    StringBuilder context = new StringBuilder();
                    String newLine = System.lineSeparator();

                    // 收集对话信息
                    for (int i = start; i < length; i++) {
                        ChatMessage msg = messages.get(i);
                        context.append("对方：%s".formatted(msg.getMessage(true))).append(newLine);
                        ChatMessage replyMsg = memory.getReplyMessageByUUID(msg.getUUID());
                        context.append("你：%s".formatted(replyMsg.getMessage(true))).append(newLine);
                    }
                    String requestBody = sentAnalysisRequestBody(context.toString());
                    String scoreStr = gptRun(requestBody);

                    float score = NumberUtils.toFloat(scoreStr, 0f);
                }
            });
        }
    }
}