package com.ms.wx.message;

import com.ms.wx.exception.UnsupportedMessageTypeException;

public enum MessageType {
    TEXT("text"), IMAGE("image"), LINK("link");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public static MessageType isSupported(String msgType) throws UnsupportedMessageTypeException {
        for (MessageType t : MessageType.values()) {
            if (t.name().equalsIgnoreCase(msgType)) return t;
        }
        throw new UnsupportedMessageTypeException(msgType + " is not a supported type");
    }

    public String getType() {
        return type;
    }
}
