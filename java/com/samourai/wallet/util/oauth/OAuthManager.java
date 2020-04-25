package com.samourai.wallet.util.oauth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.samourai.wallet.api.backend.beans.RefreshTokenResponse;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class OAuthManager {
    private OAuthImpl oAuthImpl;
    private String apiKey;

    private DecodedJWT accessToken;
    private DecodedJWT refreshToken;

    public OAuthManager(String apiKey) {
        this.oAuthImpl = new OAuthImpl();
        this.apiKey = apiKey;
    }

    public Observable<String> computeAccessToken(OAuthApi oAuthApi) throws Exception {
        if (accessToken != null) {
            boolean valid = oAuthImpl.validate(accessToken);
            if (valid) {
                // accessToken is valid
                return Observable.fromArray(oAuthImpl.tokenToString(accessToken));
            }
        }
        return newAccessToken(oAuthApi);
    }

    public Observable<String> newAccessToken(OAuthApi oAuthApi) throws Exception {
        if (refreshToken != null) {
            boolean valid = oAuthImpl.validate(refreshToken);
            if (valid) {
                // refreshToken is valid => refresh
                Observable<String> observable = oAuthApi.oAuthRefresh(oAuthImpl.tokenToString(refreshToken));
                return observable.map(new Function<String, String>() {
                    @Override
                    public String apply(String accessTokenStr) {
                        accessToken = oAuthImpl.decode(accessTokenStr);
                        return oAuthImpl.tokenToString(accessToken);
                    }
                });
            }
        }

        // no refreshToken => authenticate
        Observable<RefreshTokenResponse.Authorization> observable = oAuthApi.oAuthAuthenticate(apiKey);
        return observable.map(new Function<RefreshTokenResponse.Authorization, String>() {
            @Override
            public String apply(RefreshTokenResponse.Authorization auth) throws Exception {
                accessToken = oAuthImpl.decode(auth.access_token);
                refreshToken = oAuthImpl.decode(auth.refresh_token);
                return oAuthImpl.tokenToString(accessToken);
            }
        });
    }
}
