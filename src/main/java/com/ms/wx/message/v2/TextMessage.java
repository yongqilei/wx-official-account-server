package com.ms.wx.message.v2;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.ms.wx.message.BaseMessage;

public class TextMessage extends BaseMessage {

    @JacksonXmlProperty(localName = "Content")
    @JacksonXmlCData
    private String content;

    public TextMessage() {
    }

    public TextMessage(String toUserName, String fromUserName, Long createTime, String msgType) {
        this(toUserName, fromUserName, createTime, msgType, "");
    }

    public TextMessage(String toUserName, String fromUserName, Long createTime, String msgType, String content) {
        super(toUserName, fromUserName, createTime, msgType);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
