package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class MixinFoodData {

    @Inject(
        method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"
        ),
        cancellable = true
    )
    private void preEatEvent(Item item, ItemStack itemStack, CallbackInfo ci) {
        FoodProperties foodProperties = item.getFoodProperties();
        if (foodProperties == null) return;
        var hungerChangeEvent = new CustomPlayerEvents.PlayerHungerChangeEvent(null, foodProperties.getNutrition(), false);
        CustomPlayerEvents.INSTANCE.getPreHungerChange().invoke(hungerChangeEvent);
        if (hungerChangeEvent.isCancelled()) {
            itemStack.setCount(itemStack.getCount() + 1);
            ci.cancel();
        }
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Math;max(II)I"
        ),
        cancellable = true
    )
    private void preHungerEvent(Player player, CallbackInfo ci) {
        var hungerChangeEvent = new CustomPlayerEvents.PlayerHungerChangeEvent(player, -1, false);
        CustomPlayerEvents.INSTANCE.getPreHungerChange().invoke(hungerChangeEvent);
        if (hungerChangeEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
