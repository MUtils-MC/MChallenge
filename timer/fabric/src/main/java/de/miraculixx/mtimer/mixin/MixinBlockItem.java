package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Inject(
        method = "place",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/BlockItem;updateBlockStateFromTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"
        ),
        cancellable = true
    )
    private void preBlockPlace(BlockPlaceContext blockPlaceContext, CallbackInfoReturnable<InteractionResult> cir) {
        CustomPlayerEvents.PlayerBlockEvent playerBlockEvent = new CustomPlayerEvents.PlayerBlockEvent(blockPlaceContext.getPlayer(), blockPlaceContext.getClickedPos(), blockPlaceContext.getLevel(), false);
        CustomPlayerEvents.INSTANCE.getPreBlockPlace().invoke(playerBlockEvent);
        if (playerBlockEvent.isCancelled()) {
            blockPlaceContext.getLevel().setBlock(blockPlaceContext.getClickedPos(), blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos()), 1);
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
