package com.extclp.block.logger.fabric.mixins;

import com.extclp.block.logger.fabric.events.PlayerPlaceBlocKEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem {

    @Shadow public abstract ActionResult place(ItemPlacementContext context);

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult onPlaceBlock(BlockItem blockItem, ItemPlacementContext context){
        ActionResult result = place(context);
        if(result == ActionResult.CONSUME || result == ActionResult.SUCCESS){
            PlayerPlaceBlocKEvent.EVENT.invoker().onPlayerPlace(context.getPlayer(), context.getWorld(), context.getBlockPos());
        }
        return result;
    }
}
