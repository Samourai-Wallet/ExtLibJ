package com.samourai.wallet.util;

import org.bouncycastle.util.encoders.Base64;

public class XORUtil {

  private static XORUtil instance = null;

  private XORUtil() {
    ;
  }

  public static XORUtil getInstance() {

    if (instance == null) {
      instance = new XORUtil();
    }

    return instance;
  }

  public byte[] decode(String data0, String data1) {

    byte[] xorSegments0 = Base64.decode(data0);
    byte[] xorSegments1 = Base64.decode(data1);

    return xor(xorSegments0, xorSegments1);
  }

  public String decodeAsString(String data0, String data1) throws Exception {
    byte[] decoded = decode(data0, data1);
    return new String(decoded, "UTF-8");
  }

  private byte[] xor(byte[] b0, byte[] b1) {

    byte[] ret = new byte[b0.length];

    for (int i = 0; i < b0.length; i++) {
      ret[i] = (byte) (b0[i] ^ b1[i]);
    }

    return ret;
  }

  public String[] encode(byte[] data) {

    String[] keySegments = new String[2];

    byte[] xorRandom = new byte[data.length];
    byte[] xorMatch = new byte[data.length];

    for (int i = 0; i < data.length; i++) {
      xorRandom[i] = (byte) (256 * Math.random());
      xorMatch[i] = (byte) (xorRandom[i] ^ data[i]);
    }

    keySegments[0] = Base64.toBase64String(xorRandom);
    keySegments[1] = Base64.toBase64String(xorMatch);

    return keySegments;
  }

  public String[] encode(String data) {
    return encode(data.getBytes());
  }
}
