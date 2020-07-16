package com.extclp.block.logger.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PlayerBreakBlockEvent {

    Event<PlayerBreakBlockEvent> EVENT = EventFactory.createArrayBacked(PlayerBreakBlockEvent.class,
            listeners -> (player, world, pos) -> {
                for (PlayerBreakBlockEvent event : listeners) {
                    event.onBreakBlock(player, world, pos);
                }
            }
    );

    void onBreakBlock(PlayerEntity player, World world, BlockPos pos);
}
