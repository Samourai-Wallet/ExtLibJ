package com.samourai.wallet.api.backend;

import com.samourai.wallet.api.backend.beans.HttpException;
import com.samourai.wallet.api.backend.beans.MultiAddrResponse;
import com.samourai.wallet.api.backend.beans.RefreshTokenResponse;
import com.samourai.wallet.api.backend.beans.UnspentResponse;
import com.samourai.wallet.util.oauth.OAuthApi;
import com.samourai.wallet.util.oauth.OAuthManager;
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
  private OAuthManager oAuthManager;

  public BackendApi(IBackendClient httpClient, String urlBackend, String apiKey) {
    this.httpClient = httpClient;
    this.urlBackend = urlBackend;
    this.oAuthManager = (apiKey != null ? new OAuthManager(apiKey) : null);
    if (log.isDebugEnabled()) {
      String oAuthStr = oAuthManager != null ? "yes" : "no";
      log.debug("urlBackend=" + urlBackend + ", oAuth=" + oAuthStr);
    }
  }

  public List<UnspentResponse.UnspentOutput> fetchUtxos(String zpub) throws Exception {
    String url = computeAuthUrl(urlBackend + URL_UNSPENT + zpub);
    if (log.isDebugEnabled()) {
      log.debug("fetchUtxos: " + url);
    }
    Map<String,String> headers = computeHeaders();
    UnspentResponse unspentResponse = httpClient.getJson(url, UnspentResponse.class, headers);
    List<UnspentResponse.UnspentOutput> unspentOutputs =
        new ArrayList<UnspentResponse.UnspentOutput>();
    if (unspentResponse.unspent_outputs != null) {
      unspentOutputs = Arrays.asList(unspentResponse.unspent_outputs);
    }
    return unspentOutputs;
  }

  public List<MultiAddrResponse.Address> fetchAddresses(String zpub) throws Exception {
    String url = computeAuthUrl(urlBackend + URL_MULTIADDR + zpub);
    if (log.isDebugEnabled()) {
      log.debug("fetchAddress: " + url);
    }
    Map<String,String> headers = computeHeaders();
    MultiAddrResponse multiAddrResponse = httpClient.getJson(url, MultiAddrResponse.class, headers);
    List<MultiAddrResponse.Address> addresses = new ArrayList<MultiAddrResponse.Address>();
    if (multiAddrResponse.addresses != null) {
      addresses = Arrays.asList(multiAddrResponse.addresses);
    }
    return addresses;
  }

  public MultiAddrResponse.Address fetchAddress(String zpub) throws Exception {
    List<MultiAddrResponse.Address> addresses = fetchAddresses(zpub);
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

  public MinerFee fetchMinerFee() throws Exception {
    String url = computeAuthUrl(urlBackend + URL_MINER_FEES);
    Map<String,String> headers = computeHeaders();
    Map<String, Integer> feeResponse = httpClient.getJson(url, Map.class, headers);
    if (feeResponse == null) {
      throw new Exception("Invalid miner fee response from server");
    }
    return new MinerFee(feeResponse);
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
    if (oAuthManager != null) {
      // add auth token
      headers.put("Authorization", "Bearer " + oAuthManager.computeAccessToken(this));
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
  public RefreshTokenResponse.Authorization oAuthAuthenticate(String apiKey) throws Exception {
    String url = getUrlBackend() + URL_GET_AUTH_LOGIN;
    if (log.isDebugEnabled()) {
      log.debug("tokenAuthenticate");
    }
    Map<String, String> postBody = new HashMap<String, String>();
    postBody.put("apikey", apiKey);
    RefreshTokenResponse response =
            getHttpClient().postUrlEncoded(url, RefreshTokenResponse.class, null, postBody);

    if (response.authorizations == null|| StringUtils.isEmpty(response.authorizations.access_token)) {
      throw new Exception("Authorization refused. Invalid apiKey?");
    }
    return response.authorizations;
  }

  @Override
  public String oAuthRefresh(String refreshTokenStr) throws Exception {
    String url = getUrlBackend() + URL_GET_AUTH_REFRESH;
    if (log.isDebugEnabled()) {
      log.debug("tokenRefresh");
    }
    Map<String, String> postBody = new HashMap<String, String>();
    postBody.put("rt", refreshTokenStr);
    RefreshTokenResponse response =
            getHttpClient().postUrlEncoded(url, RefreshTokenResponse.class, null, postBody);

    if (response.authorizations == null || StringUtils.isEmpty(response.authorizations.access_token)) {
      throw new Exception("Authorization refused. Invalid apiKey?");
    }
    return response.authorizations.access_token;
  }
}
