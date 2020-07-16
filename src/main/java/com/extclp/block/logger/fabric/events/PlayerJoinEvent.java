package com.extclp.block.logger.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerJoinEvent {

    Event<PlayerJoinEvent> EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class,
            listeners -> (player) -> {
                for (PlayerJoinEvent event : listeners) {
                    event.onPlayerJoin(player);
                }
            }
    );

    void onPlayerJoin(PlayerEntity player);
}
