package com.samourai.wallet.api.backend;

import com.samourai.wallet.api.backend.beans.HttpException;
import com.samourai.wallet.api.backend.beans.MultiAddrResponse;
import com.samourai.wallet.api.backend.beans.RefreshTokenResponse;
import com.samourai.wallet.api.backend.beans.UnspentResponse;
import com.samourai.wallet.util.oauth.OAuthApi;
import com.samourai.wallet.util.oauth.OAuthManager;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import java8.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BackendApi implements OAuthApi {
  private Logger log = LoggerFactory.getLogger(BackendApi.class);

  private static final String URL_UNSPENT = "/unspent?active=";
  private static final String URL_MULTIADDR = "/multiaddr?active=";
  private static final String URL_INIT_BIP84 = "/xpub";
  private static final String URL_MINER_FEES = "/fees";
  private static final String URL_PUSHTX = "/pushtx/";
  private static final String URL_GET_AUTH_LOGIN = "/auth/login";
  private static final String URL_GET_AUTH_REFRESH = "/auth/refresh";

  private IBackendClient httpClient;
  private String urlBackend;
  private Optional<OAuthManager> oAuthManager;

  public BackendApi(IBackendClient httpClient, String urlBackend, Optional<OAuthManager> oAuthManager) {
    this.httpClient = httpClient;
    this.urlBackend = urlBackend;
    this.oAuthManager = oAuthManager;
    if (log.isDebugEnabled()) {
      String oAuthStr = oAuthManager.isPresent() ? "yes" : "no";
      log.debug("urlBackend=" + urlBackend + ", oAuth=" + oAuthStr);
    }
  }

  public Observable<List<UnspentResponse.UnspentOutput>> fetchUtxos(String zpub) throws Exception {
    String url = computeAuthUrl(urlBackend + URL_UNSPENT + zpub);
    if (log.isDebugEnabled()) {
      log.debug("fetchUtxos: " + url);
    }
    Map<String,String> headers = computeHeaders();
    Observable<Optional<UnspentResponse>> observable = httpClient.getJson(url, UnspentResponse.class, headers);
    return observable.map(new Function<Optional<UnspentResponse>, List<UnspentResponse.UnspentOutput>>() {
      @Override
      public List<UnspentResponse.UnspentOutput> apply(Optional<UnspentResponse> unspentResponseOptional) throws Exception {
        List<UnspentResponse.UnspentOutput> unspentOutputs = new ArrayList<UnspentResponse.UnspentOutput>();

        UnspentResponse unspentResponse = unspentResponseOptional.get();
        if (unspentResponse.unspent_outputs != null) {
          unspentOutputs = Arrays.asList(unspentResponse.unspent_outputs);
        }
        return unspentOutputs;
      }
    });
  }

  public Observable<List<MultiAddrResponse.Address>> fetchAddresses(String zpub) throws Exception {
    String url = computeAuthUrl(urlBackend + URL_MULTIADDR + zpub);
    if (log.isDebugEnabled()) {
      log.debug("fetchAddress: " + url);
    }
    Map<String,String> headers = computeHeaders();
    Observable<Optional<MultiAddrResponse>> observable = httpClient.getJson(url, MultiAddrResponse.class, headers);
    return observable.map(new Function<Optional<MultiAddrResponse>, List<MultiAddrResponse.Address>>() {
      @Override
      public List<MultiAddrResponse.Address> apply(Optional<MultiAddrResponse> multiAddrResponseOptional) throws Exception {
        List<MultiAddrResponse.Address> addresses = new ArrayList<MultiAddrResponse.Address>();

        MultiAddrResponse multiAddrResponse = multiAddrResponseOptional.get();
        if (multiAddrResponse.addresses != null) {
          addresses = Arrays.asList(multiAddrResponse.addresses);
        }
        return addresses;
      }
    });
  }

  public Observable<MultiAddrResponse.Address> fetchAddress(final String zpub) throws Exception {
    Observable<List<MultiAddrResponse.Address>> observable = fetchAddresses(zpub);
    return observable.map(new Function<List<MultiAddrResponse.Address>, MultiAddrResponse.Address>() {
      @Override
      public MultiAddrResponse.Address apply(List<MultiAddrResponse.Address> addresses) throws Exception {
        if (addresses.size() != 1) {
          throw new Exception("Address count=" + addresses.size());
        }
        MultiAddrResponse.Address address = addresses.get(0);

        if (log.isDebugEnabled()) {
          log.debug(
                  "fetchAddress "
                          + zpub
                          + ": account_index="
                          + address.account_index
                          + ", change_index="
                          + address.change_index);
        }
        return address;
      }
    });
  }

  public void initBip84(String zpub) throws Exception {
    String url = computeAuthUrl(urlBackend + URL_INIT_BIP84);
    if (log.isDebugEnabled()) {
      log.debug("initBip84: zpub=" + zpub);
    }
    Map<String,String> headers = computeHeaders();
    Map<String, String> postBody = new HashMap<String, String>();
    postBody.put("xpub", zpub);
    postBody.put("type", "new");
    postBody.put("segwit", "bip84");
    httpClient.postUrlEncoded(url, Void.class, headers, postBody);
  }

  public Observable<MinerFee> fetchMinerFee() throws Exception {
    String url = computeAuthUrl(urlBackend + URL_MINER_FEES);
    Map<String,String> headers = computeHeaders();
    Observable<Optional<Map>> observable = httpClient.getJson(url, Map.class, headers);
    return observable.map(new Function<Optional<Map>, MinerFee>() {
      @Override
      public MinerFee apply(Optional<Map> feeResponseOptional) throws Exception {
        Map<String, Integer> feeResponse = feeResponseOptional.get();
        if (feeResponse == null) {
          throw new Exception("Invalid miner fee response from server");
        }
        return new MinerFee(feeResponse);
      }
    });
  }

  public void pushTx(String txHex) throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("pushTx... " + txHex);
    } else {
      log.info("pushTx...");
    }
    String url = computeAuthUrl(urlBackend + URL_PUSHTX);
    Map<String,String> headers = computeHeaders();
    Map<String, String> postBody = new HashMap<String, String>();
    postBody.put("tx", txHex);
    try {
      httpClient.postUrlEncoded(url, Void.class, headers, postBody);
    } catch (HttpException e) {
      if (log.isDebugEnabled()) {
        log.error("pushTx failed", e);
      }
      log.error(
          "PushTx failed: response="
              + e.getResponseBody()
              + ". error="
              + e.getMessage()
              + " for txHex="
              + txHex);
      throw new Exception(
          "PushTx failed (" + e.getResponseBody() + ") for txHex=" + txHex);
    }
  }

  public boolean testConnectivity() {
    try {
      fetchMinerFee();
      return true;
    } catch (Exception e) {
      log.error("", e);
      return false;
    }
  }

  protected Map<String,String> computeHeaders() throws Exception {
    Map<String,String> headers = new HashMap<String, String>();
    if (oAuthManager.isPresent()) {
      // add auth token
      headers.put("Authorization", "Bearer " + oAuthManager.get().getOAuthAccessToken(this));
    }
    return headers;
  }

  protected String computeAuthUrl(String  url) throws Exception {
    // override for auth support
    return url;
  }

  protected IBackendClient getHttpClient() {
    return httpClient;
  }

  public String getUrlBackend() {
    return urlBackend;
  }

  // OAuthAPI

  @Override
  public Observable<RefreshTokenResponse.Authorization> oAuthAuthenticate(String apiKey) throws Exception {
    String url = getUrlBackend() + URL_GET_AUTH_LOGIN;
    if (log.isDebugEnabled()) {
      log.debug("tokenAuthenticate");
    }
    Map<String, String> postBody = new HashMap<String, String>();
    postBody.put("apikey", apiKey);
    Observable<Optional<RefreshTokenResponse>> observable =
            getHttpClient().postUrlEncoded(url, RefreshTokenResponse.class, null, postBody);
    return observable.map(new Function<Optional<RefreshTokenResponse>, RefreshTokenResponse.Authorization>() {
      @Override
      public RefreshTokenResponse.Authorization apply(Optional<RefreshTokenResponse> refreshTokenResponseOptional) throws Exception {
        RefreshTokenResponse response = refreshTokenResponseOptional.get();
        if (response.authorizations == null|| StringUtils.isEmpty(response.authorizations.access_token)) {
          throw new Exception("Authorization refused. Invalid apiKey?");
        }
        return response.authorizations;
      }
    });
  }

  @Override
  public Observable<String> oAuthRefresh(String refreshTokenStr) throws Exception {
    String url = getUrlBackend() + URL_GET_AUTH_REFRESH;
    if (log.isDebugEnabled()) {
      log.debug("tokenRefresh");
    }
    Map<String, String> postBody = new HashMap<String, String>();
    postBody.put("rt", refreshTokenStr);
    Observable<Optional<RefreshTokenResponse>> observable =
            getHttpClient().postUrlEncoded(url, RefreshTokenResponse.class, null, postBody);
    return observable.map(new Function<Optional<RefreshTokenResponse>, String>() {
      @Override
      public String apply(Optional<RefreshTokenResponse> refreshTokenResponseOptional) throws Exception {
        RefreshTokenResponse response = refreshTokenResponseOptional.get();
        if (response.authorizations == null || StringUtils.isEmpty(response.authorizations.access_token)) {
          throw new Exception("Authorization refused. Invalid apiKey?");
        }
        return response.authorizations.access_token;
      }
    });
  }
}
