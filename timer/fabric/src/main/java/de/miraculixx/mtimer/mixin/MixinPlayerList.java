package de.miraculixx.mtimer.mixin;

import de.miraculixx.mtimer.events.CustomPlayerEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {

    @Inject(
        method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
        at = @At(
            "HEAD"
        ),
        cancellable = true
    )
    private void onChatMessage(PlayerChatMessage playerChatMessage, ServerPlayer serverPlayer, ChatType.Bound bound, CallbackInfo ci) {
        var chatMessageEvent = new CustomPlayerEvents.PlayerChatEvent(serverPlayer, playerChatMessage, bound, false);
        CustomPlayerEvents.INSTANCE.getPreSendChatMessage().invoke(chatMessageEvent);
        if (chatMessageEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
