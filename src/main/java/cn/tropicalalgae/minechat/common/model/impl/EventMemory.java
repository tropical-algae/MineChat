package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.IEntityMessage;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EventMemory implements IEntityMemory<EventMessage> {
    private Boolean isInitialized = false;

    public EventMemory() {
        this.isInitialized = true;
    }

    @Override
    public void setRolePrompt(Entity entity) { }

    @Override
    public void setRoleName(String roleName) { }

    @Nullable
    @Override
    public String getRoleName() { return null; }
    @Override

    public Boolean hasRolePrompt() { return true; }

    @Override
    public Boolean isInitialized() { return this.isInitialized; }

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
