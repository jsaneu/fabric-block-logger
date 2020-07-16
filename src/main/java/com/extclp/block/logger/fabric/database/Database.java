package com.extclp.block.logger.fabric.database;

import com.extclp.block.logger.fabric.actions.BlockAction;
import com.extclp.block.logger.fabric.actions.MessageAction;
import com.extclp.block.logger.fabric.actions.SessionAction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.sql.SQLException;

public interface Database extends AutoCloseable{

    void logBlock(long time, PlayerEntity player, BlockAction action, World world, BlockPos blockPos, BlockState blockState) throws SQLException;

    void logSession(long time, PlayerEntity player, SessionAction action) throws SQLException;

    void logMessage(long time, PlayerEntity player, MessageAction action, String message) throws SQLException;

    void logTick(long startNanoTime, long endNanoTime, int playerCount) throws SQLException;
}
