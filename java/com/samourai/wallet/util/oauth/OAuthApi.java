package com.samourai.wallet.util.oauth;

import com.samourai.wallet.api.backend.beans.RefreshTokenResponse;
import io.reactivex.Observable;

public interface OAuthApi {
    Observable<String> oAuthRefresh(String refreshTokenStr) throws Exception;
    Observable<RefreshTokenResponse.Authorization> oAuthAuthenticate(String apiKey) throws Exception;
}
