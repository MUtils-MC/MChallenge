package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomServerEvents;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class MixinServerLevel {

    @Inject(
        method = "tick",
        at = @At(
            "HEAD"
        ),
        cancellable = true
    )
    private void onTick(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        CustomServerEvents.WorldEvent worldEvent = new CustomServerEvents.WorldEvent((ServerLevel) (Object) this, false);
        CustomServerEvents.INSTANCE.getPreWorldTick().invoke(worldEvent);
        if (worldEvent.isCancelled()) ci.cancel();
    }
}
