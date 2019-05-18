package com.samourai.wallet.api.backend.beans;

import com.samourai.wallet.api.backend.beans.UnspentResponse.UnspentOutput;

import java.util.Comparator;

public class UnspentOutputComparator implements Comparator<UnspentOutput> {

  @Override
  public int compare(UnspentOutput o1, UnspentOutput o2) {
    return o1.value - o2.value > 0 ? 1 : -1;
  }
}
