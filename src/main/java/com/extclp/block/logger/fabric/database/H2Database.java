package com.extclp.block.logger.fabric.database;

import com.extclp.block.logger.fabric.actions.BlockAction;
import com.extclp.block.logger.fabric.actions.MessageAction;
import com.extclp.block.logger.fabric.actions.SessionAction;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class H2Database implements Database {

    private final Map<String, Byte> worldIDCache = Maps.newHashMap();
    private final Map<UUID, Integer> playerIDCache = Maps.newHashMap();
    private final Map<String, Short> blockIDCache = Maps.newHashMap();

    private final Connection connection;

    public H2Database(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);

        try (Statement statement = connection.createStatement()){
            statement.execute("create table if not exists cache_world_id(" +
                    "world char primary key," +
                    "id tinyint auto_increment" +
                    ");");

        }

        try (PreparedStatement statement = connection.prepareStatement("select * from cache_world_id")) {
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    worldIDCache.put(resultSet.getString(1), resultSet.getByte(2));
                }
            }
        }

        try (Statement statement = connection.createStatement()){
            statement.execute("create table if not exists cache_player_id(" +
                    "player uuid primary key," +
                    "id tinyint auto_increment" +
                    ");");
        }

        try (Statement statement = connection.createStatement()){
            statement.execute("create table if not exists cache_block_id(" +
                    "block char primary key," +
                    "id tinyint auto_increment" +
                    ");");
        }

        try (PreparedStatement statement = connection.prepareStatement("select * from cache_block_id")) {
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    blockIDCache.put(resultSet.getString(1), resultSet.getShort(2));
                }
            }
        }

        try (Statement statement = connection.createStatement()){
            statement.execute("create table if not exists record_block(" +
                    "id integer primary key auto_increment, " +
                    "time date," +
                    "player integer, " +
                    "action tinyint," +
                    "world tinyint, " +
                    "x integer, " +
                    "y integer, " +
                    "z integer, " +
                    "type smallint, " +
                    "data binary" +
                    ");");
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("create table if not exists record_session(" +
                    "id integer primary key auto_increment, " +
                    "time date," +
                    "player integer, " +
                    "action tinyint" +
                    ");");
        }

        try (Statement statement = connection.createStatement()){
            statement.execute("create table if not exists record_message(" +
                    "id integer primary key auto_increment, " +
                    "time date," +
                    "player integer, " +
                    "action tinyint," +
                    "chat char" +
                    ");");
        }

        try (Statement statement = connection.createStatement()){
            statement.execute("create table if not exists record_tick(" +
                    "id integer primary key auto_increment," +
                    "start_nano_time bigint," +
                    "end_nano_time bigint," +
                    "player_count int" +
                    ");");
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    @Override
    public void logBlock(long time, PlayerEntity player, BlockAction action, World world, BlockPos blockPos, BlockState blockState) throws SQLException{
        long startTime = System.nanoTime();
        try(PreparedStatement statement = connection.prepareStatement(
                "insert into record_block(time, player,action,world,x,y,z,type,data) values(?,?,?,?,?,?,?,?,?)")){
            statement.setDate(1, new Date(time));
            if(player != null){
                statement.setInt(2, getPlayerID(player));
            }else {
                statement.setInt(2, -1);
            }
            statement.setByte(3, action.id);
            statement.setByte(4, getWorldID(world));
            statement.setDouble(5, blockPos.getX());
            statement.setDouble(6, blockPos.getY());
            statement.setDouble(7, blockPos.getZ());
            statement.setShort(8, getBlockID(blockState.getBlock()));
            statement.setBytes(9, getBlockPropertiesByte(blockState));
            statement.execute();
        }
        long endTime = System.nanoTime();
        System.out.println("block: " + ((double)(endTime - startTime)) / 1000000);
    }

    private int getPlayerID(PlayerEntity player) throws SQLException {
        UUID playerUuid = player.getUuid();
        Integer playerID = playerIDCache.get(playerUuid);
        if(playerID != null){
            return playerID;
        } else {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select id from cache_player_id where player = ?;")){
                statement.setObject(1, playerUuid);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    playerID = resultSet.getInt(1);
                    playerIDCache.put(playerUuid, playerID);
                    return playerID;
                }
                try (PreparedStatement insetStatement = connection.prepareStatement(
                        "insert into cache_player_id(player) values(?);", Statement.RETURN_GENERATED_KEYS)){
                    insetStatement.setObject(1, playerUuid);
                    playerID = insetStatement.executeUpdate();
                    playerIDCache.put(playerUuid, playerID);
                    return playerID;
                }
            }
        }
    }

    public byte getWorldID(World world) throws SQLException{
        String worldName = world.getRegistryKey().getValue().toString();
        Byte worldID = worldIDCache.get(worldName);
        if(worldID != null){
            return worldID;
        } else {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into cache_world_id(world) values(?);", Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, worldName);
                worldID = ((byte) statement.executeUpdate());
                worldIDCache.put(worldName,  worldID);
                return worldID;
            }
        }
    }

    private short getBlockID(Block block) throws SQLException {
        String blockName = Registry.BLOCK.getId(block).toString();
        Short blockID = blockIDCache.get(blockName);
        if(blockID != null){
            return blockID;
        } else {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into cache_block_id(block) values(?);", Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, blockName);
                blockID = ((short) statement.executeUpdate());
                blockIDCache.put(blockName,  blockID);
                return blockID;
            }
        }
    }

    public byte[] getBlockPropertiesByte(BlockState blockState) {
        try {
            CompoundTag compoundTag = NbtHelper.fromBlockState(blockState).getCompound("Properties");
            System.out.println(compoundTag);
            if(!compoundTag.isEmpty()){
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                NbtIo.writeCompressed(compoundTag, arrayOutputStream);
                System.out.println(compoundTag);
                return arrayOutputStream.toByteArray();
            } else {
                return null;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logSession(long time, PlayerEntity player, SessionAction action) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(
                "insert into record_session(time,player,action) values(?,?,?)")){
            statement.setDate(1, new Date(time));
            statement.setInt(2, getPlayerID(player));
            statement.setByte(3, action.id);
            statement.execute();
        }
    }

    @Override
    public void logMessage(long time, PlayerEntity player, MessageAction action, String chat) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(
                "insert into record_message(time,player,action,message) values(?,?,?,?)")){
            statement.setDate(1, new Date(time));
            statement.setInt(2, getPlayerID(player));
            statement.setByte(3, action.id);
            statement.setString(3, chat);
            statement.execute();
        }
    }

    @Override
    public void logTick(long startNanoTime, long endNanoTime, int playerCount) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(
                "insert into record_tick(start_nano_time,end_nano_time,player_count) values(?,?)")){
            statement.setLong(1, startNanoTime);
            statement.setLong(1, endNanoTime);
            statement.setInt(2, playerCount);
            statement.execute();
        }
    }
}
