package cn.tropicalalgae.minechat.common.capability;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.TextMemory;
import cn.tropicalalgae.minechat.common.model.impl.TextMessage;
import net.minecraft.nbt.*;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class TextMemoryProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final TextMemory memory = new TextMemory();
    private final LazyOptional<IEntityMemory<TextMessage>> optional = LazyOptional.of(() -> memory);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.TEXT_MEMORY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag messages = new ListTag();
        for (TextMessage msg : memory.getHistory()) {
            messages.add(msg.toNBT());
        }
        String roleName = memory.getRoleName();
        tag.put("messages", messages);
        tag.putString("roleName", (roleName == null) ? "" : roleName);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        memory.getHistory().clear();
        ListTag messages = nbt.getList("messages", Tag.TAG_COMPOUND);
        String roleName = nbt.getString("roleName");
        for (Tag t : messages) {
            if (t instanceof CompoundTag ct) {
                memory.addNewMessage(new TextMessage(ct));
            }
        }
        memory.setRoleName((roleName.equals("")) ? null : roleName);
    }
}