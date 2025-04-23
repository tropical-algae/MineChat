package cn.tropicalalgae.minechat.common.capability;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.EventMemory;
import cn.tropicalalgae.minechat.common.model.impl.EventMessage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class EventMemoryProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final EventMemory memory = new EventMemory();
    private final LazyOptional<IEntityMemory<EventMessage>> optional = LazyOptional.of(() -> memory);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.EVENT_MEMORY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { }
}
