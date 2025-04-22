package cn.tropicalalgae.minechat.common.gpt;

import cn.tropicalalgae.minechat.common.model.IChatMessage;
import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.utils.Config;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.tropicalalgae.minechat.MineChat.LOGGER;
import static cn.tropicalalgae.minechat.utils.Util.getOpenaiBasedResponseContent;

public class GPTTalkerManager {
    private static final Map<String, ExecutorService> executorMap = new ConcurrentHashMap<>();


    public static void runAsync(String targetUUID, Runnable task) {
        executorMap
                .computeIfAbsent(targetUUID, k -> Executors.newSingleThreadExecutor())
                .submit(task);
    }

    @Nullable
    public static <T extends IChatMessage> String gptRunContext(IEntityMemory<T> memory) {
        String requestBody = memory.getChatRequestBody();
        return gptRun(requestBody);
    }

    @Nullable
    public static String gptRun(String requestBody) {
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
            }
            return null;
        } catch (Exception e) {
            LOGGER.info("OpenAI Model Inference ERROR: %s".formatted(e));
            return null;
        }
    }
}
