package com.ms.wx.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.wx.exception.UnsupportedMessageTypeException;
import com.ms.wx.exception.WxServiceException;
import com.ms.wx.message.*;
import com.ms.wx.message.v2.TextMessage;
import com.ms.wx.service.WxCommonService;
import com.ms.wx.service.WxMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class WxMessageServiceImpl implements WxMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxMessageServiceImpl.class);

    private final ObjectMapper objectMapper;

    private final WxCommonService wxCommonService;

    private final MessageHandlerProvider messageHandlerProvider;

    public WxMessageServiceImpl(ObjectMapper objectMapper, WxCommonService wxCommonService, MessageHandlerProvider messageHandlerProvider) {
        this.objectMapper = objectMapper;
        this.wxCommonService = wxCommonService;
        this.messageHandlerProvider = messageHandlerProvider;
    }

    /**
     * Handle message from subscribers
     * @param message XML format contains necessary information
     * @return {@link BaseMessage} Automatic reply
     */
    @Override
    public BaseMessage handleMessage(String message) {
        LOGGER.debug("Received message: {}", message);
        EncryptedMessage req;
        try {
            req = objectMapper.readValue(message, EncryptedMessage.class);
        } catch (JsonProcessingException e) {
            throw new WxServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to parse request message properly, please check the format.", e);
        }

        LOGGER.debug(req.toString());

        LOGGER.debug("Message to: {}", req.getToUserName());
        LOGGER.debug("Encrypted: {}", req.getEncrypt());

        // decrypt message
        String decrypted = wxCommonService.decryptMessage(req.getEncrypt());

        LOGGER.debug("Decrypted message: {}", decrypted);

        PlainMessageReq plainMessage;
        try {
            plainMessage = objectMapper.readValue(decrypted, PlainMessageReq.class);
        } catch (JsonProcessingException e) {
            throw new WxServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to parse request message properly, please check the format.", e);
        }

        try {
            messageHandlerProvider.getMessageHandler(MessageType.isSupported(plainMessage.getMsgType())).handleMessage(plainMessage);
        } catch (UnsupportedMessageTypeException e) {
            throw new WxServiceException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
        }

        return new TextMessage(decrypted, req.getToUserName(), System.currentTimeMillis(), MessageType.TEXT.getType(), "success");
    }
}
