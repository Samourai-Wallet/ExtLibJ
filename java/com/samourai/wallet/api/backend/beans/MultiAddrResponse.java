package com.samourai.wallet.api.backend.beans;

public class MultiAddrResponse {
  public Address[] addresses;

  public MultiAddrResponse() {}

  public static class Address {
    public String address;
    public long final_balance;
    public int account_index;
    public int change_index;
  }
}
