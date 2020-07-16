package com.extclp.block.logger.fabric.actions;

public enum SessionAction {
    JOIN("join", (byte)0),
    LEAVE("leave", (byte)1),
    ;


    public final String name;

    public final byte id;

    SessionAction(String name, byte id){
        this.name = name;
        this.id = id;
    }
}
