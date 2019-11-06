package com.samourai.wallet.util.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class OAuthImpl {
  private Logger log = LoggerFactory.getLogger(OAuthImpl.class);

  public boolean validate(DecodedJWT token) {
    // check expiration
    boolean valid = token.getExpiresAt().after(new Date());
    if (log.isDebugEnabled()) {
      log.debug(
          "accessToken is "
              + (valid ? "VALID" : "EXPIRED")
              + ", expiresAt="
              + token.getExpiresAt());
    }
    return valid;
  }

  public DecodedJWT decode(String tokenStr) {
    return JWT.decode(tokenStr);
  }

  public String tokenToString(DecodedJWT token) {
    return token.getToken();
  }
}
