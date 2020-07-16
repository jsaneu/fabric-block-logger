package com.extclp.block.logger.fabric.actions;

public enum  MessageAction {
    CHAT("chat", (byte)0),
    COMMAND("name", (byte)1),
    ;

    public final String name;

    public final byte id;

    MessageAction(String name, byte id) {
        this.name = name;
        this.id = id;
    }
}
