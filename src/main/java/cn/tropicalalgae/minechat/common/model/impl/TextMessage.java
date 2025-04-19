package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.common.model.IChatMessage;
import net.minecraft.nbt.CompoundTag;

import static cn.tropicalalgae.minechat.utils.Util.getCurrentMinecraftTime;


public class TextMessage implements IChatMessage {
    public String role;
    public String content;
    public Boolean fromPlayer;
    public String time;

    public TextMessage(String role, String content, Boolean fromPlayer) {
        this.role = role;
        this.content = content;
        this.fromPlayer = fromPlayer;
        this.time = getCurrentMinecraftTime();
    }

    public TextMessage(CompoundTag tag) {
        this.role = tag.getString("role");
        this.content = tag.getString("content");
        this.fromPlayer = tag.getBoolean("fromPlayer");
        this.time = tag.getString("time");
    }

    @Override
    public String getMessage() {
        return this.content;
//        return "[发送时间]: %s\n[消息内容]: %s".formatted(this.time, this.content);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("role", role);
        tag.putString("content", content);
        tag.putBoolean("fromPlayer", fromPlayer);
        tag.putString("time", time);
        return tag;
    }
}