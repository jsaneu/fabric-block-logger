package com.extclp.block.logger.fabric.mixins;

import com.extclp.block.logger.fabric.events.PlayerJoinEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At(value = "HEAD"))
    public void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        PlayerJoinEvent.EVENT.invoker().onPlayerJoin(player);
    }
}
