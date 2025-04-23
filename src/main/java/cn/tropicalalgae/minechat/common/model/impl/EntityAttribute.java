package cn.tropicalalgae.minechat.common.model.impl;

import cn.tropicalalgae.minechat.utils.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityAttribute {
    private final Map<UUID, Float> emotionScore = new HashMap<>();


    public CompoundTag toNBT() {
        CompoundTag data = new CompoundTag();

        // 存储emo score
        ListTag emotionScores = new ListTag();
        for (Map.Entry<UUID, Float> entry : this.emotionScore.entrySet()){
            CompoundTag tag = new CompoundTag();
            tag.putUUID("playerUUID", entry.getKey());
            tag.putFloat("score", entry.getValue());
            emotionScores.add(tag);
        }

        data.put("emotionScores", emotionScores);
        return data;
    }

    public void fromNBT(CompoundTag data) {
        ListTag emotionScores = data.getList("emotionScores", Tag.TAG_COMPOUND);
        for (Tag t : emotionScores) {
            if (t instanceof CompoundTag tag) {
                this.setEmotionScore(tag.getUUID("playerUUID"), tag.getFloat("score"));
            }
        }
    }

    public void setEmotionScore(UUID playerUUID, float emotionScore) {
        float score = this.emotionScore.getOrDefault(playerUUID, 0f);
        this.emotionScore.put(playerUUID, (emotionScore < -1 | emotionScore > 1) ? score : emotionScore);
    }

    public int getStoreOffer(UUID playerUUID, int cost) {
        float ratio = this.emotionScore.getOrDefault(playerUUID, 0f);
        return -(int)Math.ceil(cost * Config.MAX_COST_ADJUST_RATIO.get() * ratio);
    }
}
