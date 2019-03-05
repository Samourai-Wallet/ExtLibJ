package com.samourai.wallet.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FeeUtilTest {
  private static final FeeUtil feeUtil = FeeUtil.getInstance();

  @Test
  public void estimatedSizeSegwit() throws Exception {
    Assertions.assertEquals(129, feeUtil.estimatedSizeSegwit(0, 0, 1, 1, 0));
    Assertions.assertEquals(202, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 0));
    Assertions.assertEquals(282, feeUtil.estimatedSizeSegwit(1, 0, 0, 1, 1));

    Assertions.assertEquals(367, feeUtil.estimatedSizeSegwit(0, 0, 3, 3, 0));
    Assertions.assertEquals(486, feeUtil.estimatedSizeSegwit(0, 0, 4, 4, 0));
    Assertions.assertEquals(605, feeUtil.estimatedSizeSegwit(0, 0, 5, 5, 0));
  }

  @Test
  public void estimatedFeeSegwit() throws Exception {
    Assertions.assertEquals(135, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 0));
    Assertions.assertEquals(129, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 1));
    Assertions.assertEquals(1290, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 10));
    Assertions.assertEquals(12900, feeUtil.estimatedFeeSegwit(0, 0, 1, 1, 0, 100));

    Assertions.assertEquals(2020, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 0, 10));
    Assertions.assertEquals(2820, feeUtil.estimatedFeeSegwit(1, 0, 0, 1, 1, 10));
    Assertions.assertEquals(3670, feeUtil.estimatedFeeSegwit(0, 0, 3, 3, 0, 10));
    Assertions.assertEquals(4860, feeUtil.estimatedFeeSegwit(0, 0, 4, 4, 0, 10));
    Assertions.assertEquals(6050, feeUtil.estimatedFeeSegwit(0, 0, 5, 5, 0, 10));
  }
}
