package cn.tropicalalgae.minechat.common.model;

import net.minecraft.nbt.CompoundTag;

public interface IChatMessage {
    String getMessage();
    CompoundTag toNBT();
}
