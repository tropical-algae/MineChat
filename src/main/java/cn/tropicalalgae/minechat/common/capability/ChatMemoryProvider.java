package cn.tropicalalgae.minechat.common.capability;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMessage;
import net.minecraft.nbt.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ChatMemoryProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final ChatMemory memory;
    private final LazyOptional<IEntityMemory<ChatMessage>> optional;

    public ChatMemoryProvider(Entity entity) {
        this.memory = new ChatMemory(entity);
        this.optional = LazyOptional.of(() -> memory);
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.CHAT_MEMORY ? optional.cast() : LazyOptional.empty();
    }

    @Nullable
    public static IEntityMemory<ChatMessage> getChatMemory(Entity entity) {
        return entity.getCapability(ModCapabilities.CHAT_MEMORY)
                .resolve()
                .orElse(null);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag messages = new ListTag();
        for (ChatMessage msg : this.memory.getHistory()) {
            messages.add(msg.toNBT());
        }
        tag.put("messages", messages);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.memory.getHistory().clear();
        ListTag messages = nbt.getList("messages", Tag.TAG_COMPOUND);
        String roleName = nbt.getString("roleName");
        for (Tag t : messages) {
            if (t instanceof CompoundTag ct) {
                memory.addNewMessage(new ChatMessage(ct));
            }
        }
    }
}