package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevel {

    @Inject(
            method = "destroyBlock",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    private void onBlockBreak(BlockPos blockPos, boolean bl, Entity entity, int i, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player) {
            if (CustomPlayerEvents.INSTANCE.getPreBlockBreak().invoke(new CustomPlayerEvents.PlayerBlockEvent<>((Player) entity, blockPos, (Level) (Object) this)).isCancelled().get()) {
                cir.setReturnValue(false);
                ((Player) entity).containerMenu.sendAllDataToRemote();
            }
        }
    }

}
