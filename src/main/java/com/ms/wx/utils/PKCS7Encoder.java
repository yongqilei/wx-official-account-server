package com.ms.wx.utils;

import com.ms.wx.constant.WxServiceConstant;

import java.util.Arrays;

/**
 * Provided by WeChat.
 */
public class PKCS7Encoder {
    private static final int BLOCK_SIZE = 32;

    public static byte[] encode(int count) {
        int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
        char padChr = chr(amountToPad);
        return String.valueOf(padChr).repeat(amountToPad).getBytes(WxServiceConstant.CHARSET_UTF8);
    }

    public static byte[] decode(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    private static char chr(int a) {
        byte res = (byte) (a & 0xFF);
        return (char) res;
    }
}
