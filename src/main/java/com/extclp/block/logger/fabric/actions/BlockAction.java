package com.extclp.block.logger.fabric.actions;

public enum BlockAction{
    PLACE("place", (byte)0),
    BREAK("break", (byte)1),
    BURN("burn", (byte)2),
    ;

    public final String name;

    public final byte id;

    BlockAction(String name, byte id) {
        this.name = name;
        this.id = id;
    }
}