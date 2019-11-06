package com.samourai.wallet.api.backend;

import java.util.Map;

public class MinerFee {
  private Map<String, Integer> feesResponse;

  public MinerFee(Map<String, Integer> feesResponse) {
    this.feesResponse = feesResponse;
  }

  public int get(MinerFeeTarget feeTarget) {
    int fee = feesResponse.get(feeTarget.getValue());
    return fee;
  }
}
