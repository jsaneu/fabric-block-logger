package com.extclp.block.logger.fabric.logger;

import com.extclp.block.logger.fabric.actions.BlockAction;
import com.extclp.block.logger.fabric.actions.MessageAction;
import com.extclp.block.logger.fabric.actions.SessionAction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Logger extends AutoCloseable{

    void logBlock(long time, PlayerEntity player, BlockAction action, World world, BlockPos blockPos, BlockState blockState);

    void logSession(long time, PlayerEntity player, SessionAction action);

    void logMessage(long time, PlayerEntity player, MessageAction action, String message);

    void logTick(long startNanoTime, long endNanoTime, int playerCount);
}
