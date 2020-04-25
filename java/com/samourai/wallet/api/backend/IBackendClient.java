package com.samourai.wallet.api.backend;

import com.samourai.wallet.api.backend.beans.HttpException;
import io.reactivex.Observable;
import java8.util.Optional;

import java.util.Map;

public interface IBackendClient {
  <T> Observable<Optional<T>> getJson(String url, Class<T> responseType, Map<String,String> headers) throws HttpException;

  <T> Observable<Optional<T>> postUrlEncoded(String url, Class<T> responseType, Map<String,String> headers, Map<String, String> body) throws HttpException;
}
