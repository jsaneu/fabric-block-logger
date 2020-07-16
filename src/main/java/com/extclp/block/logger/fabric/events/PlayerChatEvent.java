package com.extclp.block.logger.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerChatEvent {

    Event<PlayerChatEvent> EVENT = EventFactory.createArrayBacked(PlayerChatEvent.class, listeners -> (player, message) -> {
        for (PlayerChatEvent event : listeners) {
            event.onPlayerChat(player, message);
        }
    });

    void onPlayerChat(PlayerEntity player, String message);
}
