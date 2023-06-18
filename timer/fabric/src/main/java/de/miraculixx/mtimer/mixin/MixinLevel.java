package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevel {

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
        at = @At(
            "HEAD"
        ),
        cancellable = true
    )
    private void prePlaceBlock(BlockPos blockPos, BlockState blockState, int i, CallbackInfoReturnable<Boolean> cir) {
        var onPlaceBlock = new CustomPlayerEvents.PlayerBlockEvent(null, blockPos, (Level) (Object) this, false);
        CustomPlayerEvents.INSTANCE.getPreBlockPlace().invoke(onPlaceBlock);
        if (onPlaceBlock.isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}
