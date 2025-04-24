package cn.tropicalalgae.minechat.common.capability;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMessage;
import cn.tropicalalgae.minechat.common.model.impl.EntityAttribute;
import cn.tropicalalgae.minechat.common.model.impl.EventMemory;
import cn.tropicalalgae.minechat.common.model.impl.EventMessage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityAttributeProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final EntityAttribute attribute = new EntityAttribute();
    private final LazyOptional<EntityAttribute> optional = LazyOptional.of(() -> attribute);

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.ENTITY_ATTRIBUTE ? optional.cast() : LazyOptional.empty();
    }

    public static EntityAttribute getEntityAttribute(Entity entity) {
        return entity.getCapability(ModCapabilities.ENTITY_ATTRIBUTE)
                .resolve()
                .orElse(null);
    }

    @Override
    public CompoundTag serializeNBT() {
        return attribute.toNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.attribute.fromNBT(nbt);
    }
}
