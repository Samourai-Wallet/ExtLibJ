package com.samourai.wallet.util.oauth;

import com.samourai.wallet.api.backend.beans.RefreshTokenResponse;

public interface OAuthApi {
    String oAuthRefresh(String refreshTokenStr) throws Exception;
    RefreshTokenResponse.Authorization oAuthAuthenticate(String apiKey) throws Exception;
}
