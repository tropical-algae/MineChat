package cn.tropicalalgae.minechat.utils;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<String> GPT_API;
    public static final ForgeConfigSpec.ConfigValue<String> GPT_KEY;
    public static final ForgeConfigSpec.ConfigValue<String> GPT_MODEL;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONTEXT_LENGTH;
    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_PROMPT;
    public static final ForgeConfigSpec.ConfigValue<String> FAMOUS_PROMPT;
    public static final ForgeConfigSpec.ConfigValue<String> ARMORER_PROMPT;


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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

        builder.comment("Config for prompt").push("prompt_config");

        DEFAULT_PROMPT = builder
                .comment("Default prompt for villagers of all professions")
                .define("default_prompt", "你是Minecraft中的一位村民，你友好且善良");

        FAMOUS_PROMPT = builder
                .comment("Default prompt for villagers of all professions")
                .define("famous_prompt", "");

        ARMORER_PROMPT = builder
                .comment("Default prompt for villagers of all professions")
                .define("armorer_prompt", "");

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}