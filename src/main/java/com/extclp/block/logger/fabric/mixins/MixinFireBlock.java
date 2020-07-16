package com.extclp.block.logger.fabric.mixins;

import com.extclp.block.logger.fabric.events.BurnBlockEvent;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FireBlock.class)
public class MixinFireBlock {

    @Inject(method = "trySpreadingFire", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    public void onFireBreakBlock(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge, CallbackInfo ci){
        BurnBlockEvent.EVENT.invoker().onBlockFire(world, pos);
    }
}
