package com.iflytek.obu.hunan.vrbt.migu.music.decode;

import com.sun.crypto.provider.SunJCE;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;

public class Decoder {
    static {
        Security.addProvider(new SunJCE());
    }

    private static byte[] toByte(String strIn) {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i += 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    private Key getKey(byte[] arrBTmp) {
        byte[] arrB = new byte[8];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; ++i) {
            arrB[i] = arrBTmp[i];
        }
        return new SecretKeySpec(arrB, "DES");
    }

    public byte[] decode(String rawKey, String content) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        Key key = this.getKey(rawKey.getBytes("UTF8"));
        cipher.init(2, key);
        byte[] bytes = toByte(content);
        return cipher.doFinal(bytes);
    }
}
