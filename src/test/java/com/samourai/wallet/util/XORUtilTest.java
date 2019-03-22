package com.samourai.wallet.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XORUtilTest {
  private static final XORUtil xorUtil = XORUtil.getInstance();

  @Test
  public void encodeDecode() throws Exception {
    String data = "samourai";

    // encode
    String[] encoded = xorUtil.encode(data);
    Assertions.assertEquals(2, encoded.length);
    System.out.println(encoded[0]+"\n"+encoded[1]);

    // decode
    Assertions.assertEquals(data, xorUtil.decodeAsString(encoded[0], encoded[1]));

  }
}
