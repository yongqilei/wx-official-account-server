package com.ms.wx.service;

import com.ms.wx.message.BaseMessage;

public interface WxMessageService {
    BaseMessage handleMessage(String message);
}
