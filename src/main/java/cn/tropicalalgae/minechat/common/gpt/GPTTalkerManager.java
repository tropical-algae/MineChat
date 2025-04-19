package cn.tropicalalgae.minechat.common.gpt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GPTTalkerManager {
    private static final Map<String, ExecutorService> executorMap = new ConcurrentHashMap<>();

    public static void runAsync(String targetUUID, Runnable task) {
        executorMap
                .computeIfAbsent(targetUUID, k -> Executors.newSingleThreadExecutor())
                .submit(task);
    }
}
