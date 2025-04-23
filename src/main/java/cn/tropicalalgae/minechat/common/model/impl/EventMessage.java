package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.common.enumeration.EventType;
import cn.tropicalalgae.minechat.common.model.IEntityMessage;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

import static cn.tropicalalgae.minechat.utils.Util.getCurrentMinecraftTime;

public class EventMessage implements IEntityMessage {
    private final UUID uuid;
    private final UUID subjectUUID;
    private final EventType eventType;

    public String content;
    public String time;

    public EventMessage(UUID subjectUUID, EventType eventType, String content) {
        this.uuid = UUID.randomUUID();
        this.subjectUUID = subjectUUID;
        this.eventType = eventType;
        this.content = content;
        this.time = getCurrentMinecraftTime();
    }

    public EventMessage(UUID subjectUUID, String eventType, String content) {
        this.uuid = UUID.randomUUID();
        this.subjectUUID = subjectUUID;
        this.eventType = EventType.valueOf(eventType);
        this.content = content;
        this.time = getCurrentMinecraftTime();
    }

    @Override
    public UUID getUUID() { return this.uuid; }

    @Override
    public UUID getSenderUUID() { return null; }

    @Override
    public UUID getRepliedUUID() {return null; }

    @Override
    public String getMessage(Boolean withTime) {
        return withTime ? "[%s] %s".formatted(this.time, this.content) : this.content;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", this.uuid);
        tag.putUUID("subjectUUID", this.subjectUUID);
        tag.putString("eventType", this.eventType.toString());
        tag.putString("content", this.content);
        tag.putString("time", this.time);
        return tag;
    }
}
