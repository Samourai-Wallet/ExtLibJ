package com.samourai.wallet.api.backend;

import java.util.Map;

public class SamouraiFee {
  private Map<String, Integer> feesResponse;

  public SamouraiFee(Map<String, Integer> feesResponse) {
    this.feesResponse = feesResponse;
  }

  public int get(SamouraiFeeTarget feeTarget) {
    int fee = feesResponse.get(feeTarget.getValue());
    return fee;
  }
}
