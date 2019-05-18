package com.samourai.wallet.api.backend;

import com.samourai.wallet.api.backend.beans.HttpException;

import java.util.Map;

public interface IBackendClient {
  <T> T parseJson(String url, Class<T> entityClass) throws HttpException;

  void postUrlEncoded(String url, Map<String, String> body) throws HttpException;
}
