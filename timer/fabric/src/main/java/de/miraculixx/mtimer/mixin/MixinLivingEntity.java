package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import de.miraculixx.mtimer.events.CustomWorldEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow public abstract ItemStack getItemInHand(InteractionHand interactionHand);

    @Shadow public abstract MobType getMobType();

    @Shadow protected boolean dead;

    @Inject(
        method = "startUsingItem",
        at = @At(
            "HEAD"
        ),
        cancellable = true
    )
    private void preUsingItem(InteractionHand interactionHand, CallbackInfo ci) {
        ItemStack itemStack = getItemInHand(interactionHand);
        Player player = (Player) (Object) this;
        var usingItemEvent = new CustomPlayerEvents.PlayerItemEvent<>(player, itemStack, false);
        CustomPlayerEvents.INSTANCE.getPreUseItem().invoke(usingItemEvent);
        if (usingItemEvent.isCancelled()) {
            ci.cancel();
            player.containerMenu.sendAllDataToRemote();
        }
    }

    @Inject(
        method = "die",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"
        )
    )
    private void afterDeath(DamageSource damageSource, CallbackInfo ci) {
        CustomWorldEvents.INSTANCE.getAfterMobDeath().invoke(new CustomWorldEvents.MobEvent((LivingEntity) (Object) this));
    }
}
