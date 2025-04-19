package cn.tropicalalgae.minechat.common.model;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.Deque;

public interface IEntityMemory <T extends IChatMessage> {
    void setRolePrompt(Entity entity);
    void setRoleName(String roleName);
    @Nullable
    String getRoleName();
    Boolean hasRolePrompt();
    Boolean isInitialized();
    String getChatRequestBody();
    void addNewMessage(IChatMessage newMessage);
    Deque<T> getHistory();
}
