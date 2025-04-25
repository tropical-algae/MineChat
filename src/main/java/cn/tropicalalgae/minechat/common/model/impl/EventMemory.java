package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.IEntityMessage;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EventMemory implements IEntityMemory<EventMessage> {
    private Boolean isInitialized = false;
    private final Entity entity;

    public EventMemory(Entity entity) {
        this.entity = entity;
        this.isInitialized = true;
    }

    @Override
    public String getChatRequestBody() { return null; }

    @Override
    public void addNewMessage(IEntityMessage newMessage) {}

    @Override
    public List<EventMessage> getHistory() { return null; }

    @Override
    public EventMessage getMessageByUUID(UUID messageUUID) { return null; }

    @Override
    public EventMessage getReplyMessageByUUID(UUID messageUUID) { return null; }

    @Override
    public List<EventMessage> getMessagesBySenderUUID(UUID senderUUID) { return null; }

}
