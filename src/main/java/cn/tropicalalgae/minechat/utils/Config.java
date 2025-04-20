package cn.tropicalalgae.minechat.utils;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<String> GPT_API;
    public static final ForgeConfigSpec.ConfigValue<String> GPT_KEY;
    public static final ForgeConfigSpec.ConfigValue<String> GPT_MODEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONTEXT_LENGTH;
    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_PROMPT;
    public static final ForgeConfigSpec.ConfigValue<String> FAMOUS_PROMPT;
    public static final ForgeConfigSpec.ConfigValue<String> ARMORER_PROMPT;

    public static ForgeConfigSpec.BooleanValue MOD_ENABLED;
    public static ForgeConfigSpec.BooleanValue USE_WHITE_LIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WHITE_LIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> BLACK_LIST;


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        /* 基础配置 */
        builder.comment("Config for Model").push("model_config");

        GPT_API = builder
                .comment("Model URL that follows the OpenAI API protocol")
                .define("gpt_api", "https://api.openai.com/v1/chat/completions");

        GPT_KEY = builder
                .comment("Model API key")
                .define("gpt_key", "sk-xxx");

        GPT_MODEL = builder
                .comment("Model name")
                .define("gpt_model", "gpt-4o-mini-ca");

        CONTEXT_LENGTH = builder
                .comment("Max context length for each chat")
                .define("context_length", 10);

        builder.pop();

        /* prompt管理 */
        builder.comment("Config for prompt").push("prompt_config");

        DEFAULT_PROMPT = builder
                .comment("Default prompt for villagers of all professions")
                .define("default_prompt", "你是Minecraft中的一位村民，你友好且善良");

        FAMOUS_PROMPT = builder
                .comment("Default prompt for villagers of famous")
                .define("famous_prompt", "");

        ARMORER_PROMPT = builder
                .comment("Default prompt for villagers of armorer")
                .define("armorer_prompt", "");

        builder.pop();

        /* 权限管理 */
        builder.comment("Config for player authorization").push("authorization");

        MOD_ENABLED = builder
                .comment("Allow entities to speak or not. If false, entities will not speak with you by llm")
                .define("mod_enabled", true);

        USE_WHITE_LIST = builder
                .comment("Use white list or not. If True, only the players who on the white_list can chat with entities (villager).")
                .define("use_white_list", false);

        WHITE_LIST = builder
                .comment("Player white list")
                .defineListAllowEmpty(
                        "white_list",
                        List.of("Steve", "Alex"),
                        obj -> obj instanceof String
                );

        BLACK_LIST = builder
                .comment("Player black list. Specify the list of player names to block from interacting with entities (villagers).")
                .defineListAllowEmpty(
                        "black_list",
                        List.of(),
                        obj -> obj instanceof String
                );

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}