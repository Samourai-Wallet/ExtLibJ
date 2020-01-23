package com.samourai.wallet.api.explorer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplorerApi {
  private Logger log = LoggerFactory.getLogger(ExplorerApi.class);

  private static final String EXPLORER_MAINNET = "https://blockstream.info/";
  private static final String EXPLORER_TESTNET = "https://blockstream.info/testnet/";

  private static final String URL_TX = "tx/";
  private static final String URL_ADDRESS = "address/";

  private boolean testnet;

  public ExplorerApi(boolean testnet) {
    this.testnet = testnet;
  }

  public String getUrl() {
    return testnet ? EXPLORER_TESTNET : EXPLORER_MAINNET;
  }

  public String getUrlTx() {
    return getUrl()+URL_TX;
  }

  public String getUrlTx(String txid) {
    return getUrl()+URL_TX+txid;
  }

  public String getUrlAddress() {
    return getUrl()+URL_ADDRESS;
  }

  public String getUrlAddress(String address) {
    return getUrl()+URL_ADDRESS+address;
  }
}
