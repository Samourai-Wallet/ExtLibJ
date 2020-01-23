package com.samourai.wallet.api.backend;

public enum BackendServer {
  MAINNET(
      "https://api.samouraiwallet.com/v2",
      "http://d2oagweysnavqgcfsfawqwql2rwxend7xxpriq676lzsmtfwbt75qbqd.onion/v2"),
  TESTNET(
      "https://api.samouraiwallet.com/test/v2",
      "http://d2oagweysnavqgcfsfawqwql2rwxend7xxpriq676lzsmtfwbt75qbqd.onion/test/v2");

  private String backendUrlClear;
  private String backendUrlOnion;

  BackendServer(String backendUrlClear, String backendUrlOnion) {
    this.backendUrlClear = backendUrlClear;
    this.backendUrlOnion = backendUrlOnion;
  }

  public String getBackendUrl(boolean onion) {
    return onion ? backendUrlOnion : backendUrlClear;
  }

  public String getBackendUrlClear() {
    return backendUrlClear;
  }

  public String getBackendUrlOnion() {
    return backendUrlOnion;
  }

  public static BackendServer get(boolean isTestnet) {
    return isTestnet ? BackendServer.TESTNET : BackendServer.MAINNET;
  }
}
