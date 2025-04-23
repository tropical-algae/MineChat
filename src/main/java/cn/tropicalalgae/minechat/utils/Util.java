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

    public static String NULL_MSG_UUID = "NULL_UUID";

    public static String SYS_PROMPT_SUFFIX = """
            %s
            提示：现在的时间是%s，你的名字叫%s，当前你在与%s对话 (该信息仅用于辅助你感知环境，请勿用于回复)
            语种要求：必须使用%s回复
            """.stripIndent();
    public static String ENTITY_NAME_PROMPT = """
            你是一个起名大师，擅长为人们起名字。你现在的任务是为%s起一个名字。
            规则如下：
            - 名字随机，风格也随机（帅气、可爱、幽默、抽象、猎奇）
            - 名字长度在2~12个字符之间，不要使用通用词或无意义的词汇组合
            - 只返回名字本身，不要加说明或解释。
            - 名字使用的语种：%s
            请输出
            """.stripIndent();

    public static String SA_PROMPT = """
            你是一个情感分析专家，擅长排除表面语言的误导（如反话、敷衍、讨好、情绪波动等）,从复杂对话中判断一个人对他人的真实情感（如喜欢、讨厌、关心、冷漠）
            现在我将提供一段对话，请你判断其中“对方”对“我”的真实情感倾向，并给出一个分数，表示“对方是否真的喜欢我”。请注意：
            - 分数范围为 -1 到 1，-1 表示非常不喜欢，1 表示非常喜欢；
            - 不要轻易给出极端值；
            - 如果对方言语暧昧、态度反复，请综合判断其潜在态度；
            - 只输出数字分数，不需要解释。
            """.stripIndent();

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
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
//            for (Entity entity : level.getAllEntities()) {
//                if (entity.getUUID().equals(uuid)) {
//                    return entity;
//                }
//            }
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
        if (!Config.MOD_ENABLE.get()) {
            return false;
        } else {
            if (Config.USE_WHITE_LIST.get()) {
                return Config.WHITE_LIST.get().contains(playerName);
            } else {
                return !Config.BLACK_LIST.get().contains(playerName);
            }
        }
    }

//    private static int calculateCustomOffset(Villager villager, MerchantOffer offer, Player player) {
//        // 举例：对“图书管理员”村民加价 2
//        if (villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
//            return +2; // 涨价
//        }
//
//        // 举例：对新玩家全部交易减 1
//        if (player.getExperienceLevel() < 5) {
//            return -1; // 打折
//        }
//
//        return 0; // 默认无变化
//    }
}
