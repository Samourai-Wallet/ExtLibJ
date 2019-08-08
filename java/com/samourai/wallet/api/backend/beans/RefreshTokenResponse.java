package com.samourai.wallet.api.backend.beans;

public class RefreshTokenResponse {
  public Authorization authorizations;

  public RefreshTokenResponse() {}

  public static class Authorization {
    public String access_token;
    public String refresh_token;

    public Authorization(){}
  }
}
