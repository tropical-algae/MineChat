package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.common.model.IEntityMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static cn.tropicalalgae.minechat.utils.Util.*;


public class ChatMessage implements IEntityMessage {
    private final UUID uuid;
    private final UUID repliedUUID;  // 被该信息回复的消息的UUID
    private final UUID senderUUID;
    public String senderName;
    public String content;
    public Boolean fromPlayer;
    public String time;

    public ChatMessage(@NotNull ServerPlayer sender, String content, UUID repliedUUID) {
        this.uuid = UUID.randomUUID();
        this.senderUUID = sender.getUUID();
        this.senderName = sender.getGameProfile().getName();
        this.repliedUUID = repliedUUID;
        this.content = content;
        this.fromPlayer = true;
        this.time = getCurrentMinecraftTime();
    }

    public ChatMessage(@NotNull Entity sender, UUID repliedUUID, String content) {
        this.uuid = UUID.randomUUID();
        this.senderUUID = sender.getUUID();
        this.senderName = getEntityCustomName(sender);
        this.repliedUUID = repliedUUID;
        this.content = content;
        this.fromPlayer = false;
        this.time = getCurrentMinecraftTime();
    }

    public ChatMessage(CompoundTag tag) {
        String fakeRepliedUUID = tag.getString("repliedUUID");

        this.uuid = tag.contains("uuid", Tag.TAG_INT_ARRAY) ? tag.getUUID("uuid") : UUID.randomUUID();
        this.senderUUID = tag.getUUID("senderUUID");
        this.senderName = tag.getString("senderName");
        this.repliedUUID = fakeRepliedUUID.equals(NULL_MSG_UUID) ? null : UUID.fromString(fakeRepliedUUID);
        this.content = tag.getString("content");
        this.fromPlayer = tag.getBoolean("fromPlayer");
        this.time = tag.getString("time");
    }

    @Override
    public UUID getUUID() { return this.uuid; }

    @Override
    public UUID getSenderUUID() { return this.senderUUID; }

    @Override
    public UUID getRepliedUUID() {return this.repliedUUID; }

    @Override
    public String getMessage(Boolean withTime) {
        return withTime ? "%s [消息发送时间 %s]".formatted(this.content, this.time) : this.content;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", this.uuid);
        tag.putUUID("senderUUID", this.senderUUID);
        tag.putString("senderName", this.senderName);
        tag.putString("repliedUUID", this.repliedUUID == null ? NULL_MSG_UUID : this.repliedUUID.toString());
        tag.putString("content", this.content);
        tag.putBoolean("fromPlayer", this.fromPlayer);
        tag.putString("time", this.time);
        return tag;
    }
}