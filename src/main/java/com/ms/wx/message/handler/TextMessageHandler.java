package com.ms.wx.message.handler;

import com.ms.wx.message.MessageType;
import com.ms.wx.message.PlainMessageReq;
import com.ms.wx.message.annotation.MessageConsumer;

@MessageConsumer(MessageType.TEXT)
public class TextMessageHandler implements MessageHandler {
    @Override
    public void handleMessage(PlainMessageReq message) {

    }
}
