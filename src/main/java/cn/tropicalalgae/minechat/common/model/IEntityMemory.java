package cn.tropicalalgae.minechat.common.model;

import cn.tropicalalgae.minechat.common.model.impl.TextMessage;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IEntityMemory <T extends IChatMessage> {
    void setRolePrompt(Entity entity);
    void setRoleName(String roleName);
    @Nullable
    String getRoleName();
    Boolean hasRolePrompt();
    Boolean isInitialized();
    String getChatRequestBody();
    void addNewMessage(IChatMessage newMessage);
    List<T> getHistory();
    T getMessageByUUID(UUID messageUUID);
    T getReplyMessageByUUID(UUID messageUUID);
    List<T> getMessagesBySenderUUID(UUID senderUUID);
}
