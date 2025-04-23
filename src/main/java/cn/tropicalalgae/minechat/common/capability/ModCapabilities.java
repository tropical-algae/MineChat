package cn.tropicalalgae.minechat.common.capability;

import cn.tropicalalgae.minechat.common.model.IEntityMemory;
import cn.tropicalalgae.minechat.common.model.impl.ChatMessage;
import cn.tropicalalgae.minechat.common.model.impl.EntityAttribute;
import cn.tropicalalgae.minechat.common.model.impl.EventMessage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {
    public static final Capability<IEntityMemory<ChatMessage>> CHAT_MEMORY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IEntityMemory<EventMessage>> EVENT_MEMORY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<EntityAttribute> ENTITY_ATTRIBUTE = CapabilityManager.get(new CapabilityToken<>() {});

}
