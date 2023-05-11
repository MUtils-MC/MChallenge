package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public class MixinMobEffect {

    @Inject(
        method = "applyEffectTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"
        ),
        cancellable = true
    )
    private void prePlayerSaturationEffect(LivingEntity livingEntity, int i, CallbackInfo ci) {
        var playerHungerChangeEvent = new CustomPlayerEvents.PlayerHungerChangeEvent((Player) livingEntity, i, false);
        CustomPlayerEvents.INSTANCE.getPreHungerChange().invoke(playerHungerChangeEvent);
        if (playerHungerChangeEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
