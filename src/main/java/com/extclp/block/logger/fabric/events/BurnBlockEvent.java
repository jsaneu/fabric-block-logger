package com.extclp.block.logger.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BurnBlockEvent {

    Event<BurnBlockEvent> EVENT = EventFactory.createArrayBacked(BurnBlockEvent.class,
            listeners -> (world, pos) -> {
                for (BurnBlockEvent event : listeners) {
                    event.onBlockFire(world, pos);
                }
            }
    );

    void onBlockFire(World world, BlockPos pos);
}
