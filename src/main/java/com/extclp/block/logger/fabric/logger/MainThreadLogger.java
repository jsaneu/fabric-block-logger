package com.extclp.block.logger.fabric.logger;

import com.extclp.block.logger.fabric.actions.BlockAction;
import com.extclp.block.logger.fabric.actions.MessageAction;
import com.extclp.block.logger.fabric.actions.SessionAction;
import com.extclp.block.logger.fabric.database.Database;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

public class MainThreadLogger implements Logger {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    private final Database database;

    public MainThreadLogger(Database database) {
        this.database = database;
    }

    @Override
    public void logBlock(long time, PlayerEntity player, BlockAction action, World world, BlockPos blockPos, BlockState blockState) {
        try {
            database.logBlock(time, player, action, world, blockPos, blockState);
        }catch (Exception e){
            LOGGER.error("", e);
        }
    }

    @Override
    public void logMessage(long time, PlayerEntity player, MessageAction action, String message) {
        try {
            database.logMessage(time, player, action, message);
        }catch (Exception e){
            LOGGER.error("", e);
        }
    }

    @Override
    public void logTick(long startNanoTime, long endNanoTime, int playerCount) {
        try {
            database.logTick(startNanoTime, endNanoTime, playerCount);
        }catch (Exception e){
            LOGGER.error("", e);
        }
    }

    @Override
    public void logSession(long time, PlayerEntity player, SessionAction action) {
        try {
            database.logSession(time, player, action);
        }catch (Exception e){
            LOGGER.error("", e);
        }
    }

    @Override
    public void close() {}
}
