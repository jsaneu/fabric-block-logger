package com.extclp.block.logger.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerCommandEvent {

    Event<PlayerCommandEvent> EVENT = EventFactory.createArrayBacked(PlayerCommandEvent.class, listeners -> (player, message) -> {
        for (PlayerCommandEvent event : listeners) {
            event.onPlayerCommand(player, message);
        }
    });

    void onPlayerCommand(PlayerEntity player, String message);
}
