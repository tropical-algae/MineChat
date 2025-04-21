package cn.tropicalalgae.minechat.common.model;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public interface IChatMessage {
    UUID getUUID();
    UUID getSenderUUID();
    UUID getRepliedUUID();
    String getMessage(Boolean withTime);
    CompoundTag toNBT();
}
