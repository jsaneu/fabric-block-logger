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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncLogger implements Logger {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();

    private boolean closed;

    private final Database database;

    public AsyncLogger(Database database){
        this.database = database;
        //"Block-Logger Async Logger Thread"
        new Thread(() -> {
            while (true){
                if(closed){
                    while (!queue.isEmpty()){
                        log(queue.poll());
                    }
                    return;
                }
                try {
                    log(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void log(Object data){
        try {
           if(data instanceof LogBlockData logBlockData){
               database.logBlock(logBlockData.time, logBlockData.player, logBlockData.action, logBlockData.world, logBlockData.blockPos, logBlockData.blockState);
            } else if (data instanceof LogSessionData logSessionData){
                database.logSession(logSessionData.time, logSessionData.player, logSessionData.action);
           } else if (data instanceof LogMessageData logMessageData){
               database.logMessage(logMessageData.time, logMessageData.player, logMessageData.action, logMessageData.message);
           } else if (data instanceof LogTickData logTickData){
               database.logTick(logTickData.startNanoTime, logTickData.endNanoTime, logTickData.playerCount);

           }
        }catch (Exception e){
            LOGGER.error("" , e);
        }
    }

    public void close(){
        closed = true;
        queue.add(this);
    }

    record LogBlockData(long time, PlayerEntity player, BlockAction action, World world, BlockPos blockPos, BlockState blockState){};

    public void logBlock(long time, PlayerEntity player, BlockAction action, World world, BlockPos blockPos, BlockState blockState) {
        queue.add(new LogBlockData(time, player, action, world, blockPos, blockState));
    }

    record LogSessionData(long time, PlayerEntity player, SessionAction action){}

    @Override
    public void logSession(long time, PlayerEntity player, SessionAction action) {
        queue.add(new LogSessionData(time, player, action));
    }

    record LogMessageData(long time, PlayerEntity player, MessageAction action, String message){}

    @Override
    public void logMessage(long time, PlayerEntity player, MessageAction action, String message) {
        queue.add(new LogMessageData(time, player, action, message));
    }


    record LogTickData(long startNanoTime, long endNanoTime, int playerCount){}

    @Override
    public void logTick(long startNanoTime, long endNanoTime, int playerCount) {
        queue.add(new LogTickData(startNanoTime, endNanoTime, playerCount));
    }
}
