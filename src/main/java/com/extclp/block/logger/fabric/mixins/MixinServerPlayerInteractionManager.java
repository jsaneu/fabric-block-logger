package com.extclp.block.logger.fabric.mixins;

import com.extclp.block.logger.fabric.events.PlayerBreakBlockEvent;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow public ServerWorld world;

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "processBlockBreakingAction", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;finishMining(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Ljava/lang/String;)V"))
    public void onCreativePlayerBreakBlock(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci){
        PlayerBreakBlockEvent.EVENT.invoker().onBreakBlock(player, world, pos);
    }

    @Inject(method = "processBlockBreakingAction", at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;finishMining(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Ljava/lang/String;)V"))
    public void onPlayerBreakBlock(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci){
        PlayerBreakBlockEvent.EVENT.invoker().onBreakBlock(player, world, pos);
    }
}
