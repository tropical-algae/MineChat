package cn.tropicalalgae.minechat.common.model;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IEntityMemory <T extends IEntityMessage> {
    Boolean isInitialized();
    String getChatRequestBody();
    void addNewMessage(IEntityMessage newMessage);
    List<T> getHistory();
    T getMessageByUUID(UUID messageUUID);
    T getReplyMessageByUUID(UUID messageUUID);
    List<T> getMessagesBySenderUUID(UUID senderUUID);
}
