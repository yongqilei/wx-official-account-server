package com.ms.wx.service.impl;

import com.ms.wx.config.WxServerProperties;
import com.ms.wx.constant.WxServiceConstant;
import com.ms.wx.exception.InvalidAppIdException;
import com.ms.wx.exception.WxServiceException;
import com.ms.wx.service.WxCommonService;
import com.ms.wx.utils.PKCS7Encoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Service
@ConfigurationProperties
public class WxCommonServiceImpl implements WxCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxCommonServiceImpl.class);

    private final WxServerProperties wxProperties;

    private final byte[] aesKey;

    public WxCommonServiceImpl(WxServerProperties wxProperties) {
        this.wxProperties = wxProperties;
        if (StringUtils.isEmpty(wxProperties.getAesKey())) {
            LOGGER.debug("==========FYI: Current environment is non-prod environment, we are not using encryption for messages.==========");
        }
        this.aesKey = Base64.decodeBase64(wxProperties.getAesKey() + "=");
    }

    /**
     * Signature validation, if the request is from WeChat then return true otherwise return false
     * WeChat server sends a set of parameters including signature
     * @param signature Signature generated on WeChat Server side
     * @param nonce A random number
     * @param timestamp Timestamp
     * @return True for the request is from WeChat otherwise return false
     * @throws RuntimeException Throws exception if the validation fails
     */
    @Override
    public boolean checkSignature(String signature, String nonce, String timestamp, String echoStr) throws WxServiceException {
        String[] parameters = new String[] {wxProperties.getToken(), timestamp, nonce};
        Arrays.sort(parameters);
        String validationStr = DigestUtils.sha1Hex(String.join("", parameters));
        LOGGER.debug("Signature derived from params: {}", validationStr);
        if (!StringUtils.equals(signature, validationStr)) {
            LOGGER.debug("Original Signature is {}, but derived signature is {}", signature, validationStr);
            throw new WxServiceException(400, "Signature is invalid, please make sure the signature is issued by WeChat or check if you have proper configuration.");
        }
        return true;
    }

    @Override
    public String requestAccessToken() {
        return null;
    }

    @Override
    public String decryptMessage(String message) {
        byte[] original = null;
        try {
            Cipher cipher = Cipher.getInstance(WxServiceConstant.AES_CBC_NO_PADDING);
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, WxServiceConstant.ALGO_AES);
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            byte[] encrypted = Base64.decodeBase64(message);

            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new WxServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error happened during decryption, cause: " + e.getMessage(), e);
        }

        // remove padding
        byte[] bytes = PKCS7Encoder.decode(original);

        byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

        int xmlLength = recoverNetworkBytesOrder(networkOrder);
        String xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength),
                WxServiceConstant.CHARSET_UTF8);
        String fromAppId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length),
                WxServiceConstant.CHARSET_UTF8);

        if (!fromAppId.equals(wxProperties.getAppId())) {
            throw new InvalidAppIdException();
        }

        LOGGER.debug(xmlContent);

        return xmlContent;
    }

    @Override
    public String encryptMessage(String message) {
        return null;
    }

    // 还原4个字节的网络字节序
    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }
}
