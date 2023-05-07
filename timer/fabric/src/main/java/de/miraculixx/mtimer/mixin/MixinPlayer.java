package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class MixinPlayer {

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    private void onDrop(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir) {
        CustomPlayerEvents.PlayerItemEvent<Player> playerItemEvent = new CustomPlayerEvents.PlayerItemEvent<>((Player) (Object) this, itemStack);
        if (CustomPlayerEvents.INSTANCE.getPreDrop().invoke(playerItemEvent).isCancelled().get()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
            method = "hurt",
            at = @At(
                    "TAIL"
            ),
            cancellable = true
    )
    private void onHurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        CustomPlayerEvents.PlayerDamageEvent<Player> playerDamageEvent = new CustomPlayerEvents.PlayerDamageEvent<>((Player) (Object) this, damageSource, damage);
        if (CustomPlayerEvents.INSTANCE.getOnFinalDamage().invoke(playerDamageEvent).isCancelled().get()) {
            cir.setReturnValue(false);
        }
    }
}
