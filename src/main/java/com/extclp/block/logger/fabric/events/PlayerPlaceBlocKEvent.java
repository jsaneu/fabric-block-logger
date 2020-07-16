package com.extclp.block.logger.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PlayerPlaceBlocKEvent {

    Event<PlayerPlaceBlocKEvent> EVENT = EventFactory.createArrayBacked(PlayerPlaceBlocKEvent.class,
            listeners -> (player, world, pos) -> {
                for (PlayerPlaceBlocKEvent event : listeners) {
                    event.onPlayerPlace(player, world, pos);
                }
            }
    );

    void onPlayerPlace(PlayerEntity player, World world, BlockPos pos);
}
