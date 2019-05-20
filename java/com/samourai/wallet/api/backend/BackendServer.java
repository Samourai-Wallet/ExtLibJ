package com.samourai.wallet.api.backend;

public enum BackendServer {
  MAINNET("https://api.samouraiwallet.com"),
  TESTNET("https://api.samouraiwallet.com/test");

  private String backendUrl;

  BackendServer(String backendUrl) {
    this.backendUrl = backendUrl;
  }

  public String getBackendUrl() {
    return backendUrl;
  }
}
