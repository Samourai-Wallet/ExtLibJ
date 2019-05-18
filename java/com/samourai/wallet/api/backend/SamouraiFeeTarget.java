package com.samourai.wallet.api.backend;

public enum SamouraiFeeTarget {
  BLOCKS_2("2"),
  BLOCKS_4("4"),
  BLOCKS_6("6"),
  BLOCKS_12("12"),
  BLOCKS_24("24");

  private String value;

  SamouraiFeeTarget(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
