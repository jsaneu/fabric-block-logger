package com.extclp.block.logger.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerLeaveEvent {

    Event<PlayerLeaveEvent> EVENT = EventFactory.createArrayBacked(PlayerLeaveEvent.class,
            listeners -> (player) -> {
                for (PlayerLeaveEvent event : listeners) {
                    event.onPlayerLeave(player);
                }
            }
    );

    void onPlayerLeave(PlayerEntity player);
}
