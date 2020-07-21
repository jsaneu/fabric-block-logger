package com.extclp.block.logger.fabric;

import com.extclp.block.logger.fabric.actions.BlockAction;
import com.extclp.block.logger.fabric.actions.MessageAction;
import com.extclp.block.logger.fabric.actions.SessionAction;
import com.extclp.block.logger.fabric.database.Database;
import com.extclp.block.logger.fabric.database.H2Database;
import com.extclp.block.logger.fabric.events.*;
import com.extclp.block.logger.fabric.logger.AsyncLogger;
import com.extclp.block.logger.fabric.logger.Logger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.sql.SQLException;

public class BlockLoggerMod implements ModInitializer {

    public static Database dataBase;
    public static Logger logger;

    private long startNanoTime;

    @Override
    public void onInitialize() {
        try {
            dataBase = new H2Database("jdbc:h2:./config/trace/database");
            logger = new AsyncLogger(dataBase);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ServerLifecycleEvents.SERVER_STOPPED.register(BlockLoggerMod::onServerStop);

        //tick
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            startNanoTime = System.nanoTime();
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            logger.logTick(startNanoTime, System.nanoTime(), server.getCurrentPlayerCount());
        });

        //block
        PlayerPlaceBlocKEvent.EVENT.register((player, world, pos) ->
                logger.logBlock(System.currentTimeMillis(), player, BlockAction.PLACE, world, pos, world.getBlockState(pos)));
        PlayerBreakBlockEvent.EVENT.register(((player, world, pos) ->
                logger.logBlock(System.currentTimeMillis(), player, BlockAction.BREAK, world, pos, world.getBlockState(pos))));
        BurnBlockEvent.EVENT.register((world, pos) ->
                logger.logBlock(System.currentTimeMillis(), null, BlockAction.BURN, world, pos, world.getBlockState(pos)));

        //session
        PlayerJoinEvent.EVENT.register(player ->
                logger.logSession(System.currentTimeMillis(), player, SessionAction.JOIN));
        PlayerLeaveEvent.EVENT.register(player ->
                logger.logSession(System.currentTimeMillis(), player, SessionAction.LEAVE));

        //message
        PlayerChatEvent.EVENT.register((player, message) ->
                logger.logMessage(System.currentTimeMillis(), player, MessageAction.CHAT, message));
        PlayerCommandEvent.EVENT.register((player, message) ->
                logger.logMessage(System.currentTimeMillis(), player, MessageAction.COMMAND, message));
    }


    private static void onServerStop(MinecraftServer minecraftServer) {
        try {
            logger.close();
            dataBase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}