package cn.tropicalalgae.minechat.common.events;

import cn.tropicalalgae.minechat.MineChat;
import cn.tropicalalgae.minechat.common.capability.ModCapabilities;
import cn.tropicalalgae.minechat.common.enumeration.MessageType;
import cn.tropicalalgae.minechat.mixin.MerchantMenuAccessor;
import cn.tropicalalgae.minechat.utils.Config;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static cn.tropicalalgae.minechat.utils.Util.isEntitySupported;

@Mod.EventBusSubscriber(modid = MineChat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TradeAdjustEvent {

    @SubscribeEvent
    public static void onOpenTrade(PlayerContainerEvent.Open event) {
        if (!(event.getContainer() instanceof MerchantMenu menu)) return;

        Merchant trader = ((MerchantMenuAccessor) menu).getTrader();

        // 仅被注册的villager可受到调整
        if (trader instanceof Villager villager) {
            if (isEntitySupported(villager, MessageType.CHAT) & Config.TRADE_ADJUST_ENABLED.get()) {
                villager.getCapability(ModCapabilities.ENTITY_ATTRIBUTE).ifPresent(attribute -> {
                    // 所有商品都将被调整
                    for (MerchantOffer offer: trader.getOffers()) {
                        int originDiff = offer.getSpecialPriceDiff();
                        int addedDiff = attribute.getStoreOffer(event.getEntity().getUUID(), offer.getBaseCostA().getCount());
                        offer.setSpecialPriceDiff(originDiff + addedDiff);
                    }
                });
            }
        }
    }
}
