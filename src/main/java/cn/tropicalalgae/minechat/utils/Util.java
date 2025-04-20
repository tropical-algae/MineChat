package cn.tropicalalgae.minechat.utils;


import cn.tropicalalgae.minechat.MineChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import com.google.gson.*;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.net.http.HttpResponse;
import java.util.UUID;

public class Util {

    public static String sysPromptSuffix = "%s\n重要提示：现在的时间是%s (仅作为一个参考)。" +
            "你的名字叫%s，历史消息中的用户可能来自不同的人，但在最新的消息来自%s";
    public static String entityNamePrompt = "快问快答：请你为%s起一个具体的名字，可以是中文或英文。" +
            "要求风格和名字随机一点，不考虑性别。直接输出结果，不要解释:";

    public static String getOpenaiBasedResponseContent(HttpResponse<String> response) {
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        String content = json
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
        return content.replaceAll("^[\\r\\n]+|[\\r\\n]+$", "");
    }

    public static String getCurrentMinecraftTime() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return "Unknown";

        ServerLevel level = server.getLevel(Level.OVERWORLD); // 主世界
        if (level == null) return "Unknown";

        long totalTicks = level.getDayTime();
        long day = totalTicks / 24000;
        long time = totalTicks % 24000;
        long hour = (time / 1000 + 6) % 24;
        long minute = (time % 1000) * 60 / 1000;

        return String.format("Day %d %02d:%02d", day, hour, minute);
    }

    @Nullable
    public static Entity findEntityByUUID(MinecraftServer server, String strUUID) {
        UUID uuid = UUID.fromString(strUUID);
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity.getUUID().equals(uuid)) {
                    return entity;
                }
            }
        }
        return null; // nothing
    }

    public static boolean isEntitySupportText(Entity entity) {
        /* 判断是否是适用文本交流的实体 */
        if (entity == null) {
            return false;
        }
        return MineChat.ENTITIES_SUPPORTED_TEXT.stream()
                .anyMatch(clazz -> clazz.isAssignableFrom(entity.getClass()));
    }

    public static String buildEntityNameRequestBody(String entityType) {
        JsonObject root = new JsonObject();
        JsonArray messages = new JsonArray();

        JsonObject msgContent = new JsonObject();
        msgContent.addProperty("role", "user");
        msgContent.addProperty("content", String.format(entityNamePrompt.formatted(entityType)));

        messages.add(msgContent);
        root.addProperty("model", Config.GPT_MODEL.get());
        root.add("messages", messages);
        return new Gson().toJson(root);
    }

    @Nullable
    public static String getVillagePrompt(VillagerProfession profession) {
        if (profession.equals(VillagerProfession.FARMER)) {
            return Config.FAMOUS_PROMPT.get();
        } else if (profession.equals(VillagerProfession.ARMORER)) {
            return Config.FAMOUS_PROMPT.get();
        } else {
            return null;
        }
    }

    public static Boolean canPlayerTalkToEntity(String playerName) {
        if (!Config.MOD_ENABLED.get()) {
            return false;
        } else {
            if (Config.USE_WHITE_LIST.get()) {
                return Config.WHITE_LIST.get().contains(playerName);
            } else {
                return !Config.BLACK_LIST.get().contains(playerName);
            }
        }
    }
}
