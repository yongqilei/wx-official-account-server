package com.ms.wx.service;

import com.ms.wx.exception.WxServiceException;

public interface WxCommonService {

    boolean checkSignature(String signature, String nonce, String timestamp, String echoStr) throws WxServiceException;
    String requestAccessToken();
    String decryptMessage(String message);
    String encryptMessage(String message);
}
