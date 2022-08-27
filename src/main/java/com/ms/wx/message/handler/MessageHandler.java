package com.ms.wx.message.handler;

import com.ms.wx.message.MessageType;
import com.ms.wx.message.PlainMessageReq;

import java.util.HashMap;
import java.util.Map;

public interface MessageHandler {

    void handleMessage(PlainMessageReq message);
}
