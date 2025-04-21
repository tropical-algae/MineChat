package cn.tropicalalgae.minechat.mixin;

import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import static net.minecraft.client.model.geom.PartNames.TAIL;

@Mixin(MerchantMenu.class)
public interface MerchantMenuAccessor {
    @Accessor("trader")
    Merchant getTrader();
}
//@Mixin(MerchantMenu.class)
//public abstract class MerchantMenuAccessor {
//    @Accessor("trader")
//    public abstract Merchant getTrader();
//}