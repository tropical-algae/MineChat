package cn.tropicalalgae.minechat.common.events;

import cn.tropicalalgae.minechat.MineChat;
import cn.tropicalalgae.minechat.mixin.MerchantMenuAccessor;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = MineChat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TradeAdjustEvent {

    @SubscribeEvent
    public static void onOpenTrade(PlayerContainerEvent.Open event) {
        if (!(event.getContainer() instanceof MerchantMenu menu)) return;

        Merchant trader = ((MerchantMenuAccessor) (Object) menu).getTrader();

        List<MerchantOffer> offers = menu.getOffers();
//        Merchant merchant = menu.getTrader();
//        if (!(merchant instanceof Villager villager)) return;
//
//        UUID playerId = event.getEntity().getUUID();
//        List<MerchantOffer> offers = villager.getOffers();
//
//        for (MerchantOffer offer : offers) {
//            // 原版价格修正 + 你的修正
//            int originalOffset = offer.getSpecialPriceDiff(); // 原版系统设置的价格差值（声望、英雄村庄等）
//            int yourOffset = calculateCustomOffset(villager, offer, event.getEntity());
//
//            offer.setSpecialPriceDiff(originalOffset + yourOffset); // ✅ 叠加你的偏移
//        }
    }
}
