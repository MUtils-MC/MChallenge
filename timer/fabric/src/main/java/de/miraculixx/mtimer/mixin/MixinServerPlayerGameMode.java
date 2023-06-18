package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode {

    @Shadow
    protected ServerLevel level;

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(
        method = "destroyBlock",
        at = @At(
            "HEAD"
        ),
        cancellable = true
    )
    private void preBlockBreak(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.level.getBlockState(blockPos);
        boolean willBreak = this.player.getMainHandItem().getItem().canAttackBlock(blockState, this.level, blockPos, this.player);
        if (willBreak && blockState.getBlock() instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) willBreak = false;
        CustomPlayerEvents.PlayerBlockEvent playerBlockEvent = new CustomPlayerEvents.PlayerBlockEvent(player, blockPos, level, willBreak);
        CustomPlayerEvents.INSTANCE.getPreBlockBreak().invoke(playerBlockEvent);
        if (playerBlockEvent.isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}
