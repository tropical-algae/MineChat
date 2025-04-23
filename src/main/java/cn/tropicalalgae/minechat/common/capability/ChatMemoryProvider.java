package cn.tropicalalgae.minechat.common.capability;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMessage;
import net.minecraft.nbt.*;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class ChatMemoryProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final ChatMemory memory = new ChatMemory();
    private final LazyOptional<IEntityMemory<ChatMessage>> optional = LazyOptional.of(() -> memory);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.CHAT_MEMORY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag messages = new ListTag();
        for (ChatMessage msg : this.memory.getHistory()) {
            messages.add(msg.toNBT());
        }
        String roleName = this.memory.getRoleName();
        tag.put("messages", messages);
        tag.putString("roleName", (roleName == null) ? "" : roleName);
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
        this.memory.setRoleName((roleName.equals("")) ? null : roleName);
    }
}