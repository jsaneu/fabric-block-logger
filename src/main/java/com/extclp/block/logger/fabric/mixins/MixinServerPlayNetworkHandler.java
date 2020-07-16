package com.extclp.block.logger.fabric.mixins;

import com.extclp.block.logger.fabric.events.PlayerChatEvent;
import com.extclp.block.logger.fabric.events.PlayerCommandEvent;
import com.extclp.block.logger.fabric.events.PlayerLeaveEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onGameMessage",  at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;executeCommand(Ljava/lang/String;)V"))
    public void onExecuteCommand(ChatMessageC2SPacket packet, CallbackInfo ci){
        PlayerCommandEvent.EVENT.invoker().onPlayerCommand(player, packet.getChatMessage());
    }

    @Inject(method = "onGameMessage",  at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void onChat(ChatMessageC2SPacket packet, CallbackInfo ci){
        PlayerChatEvent.EVENT.invoker().onPlayerChat(player, packet.getChatMessage());
    }

    @Inject(method = "disconnect", at = @At(value = "HEAD"))
    public void onPlayerDisconnect(Text reason, CallbackInfo ci){
        PlayerLeaveEvent.EVENT.invoker().onPlayerLeave(player);
    }
}
