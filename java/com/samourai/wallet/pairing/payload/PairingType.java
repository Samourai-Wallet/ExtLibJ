package com.samourai.wallet.pairing.payload;

import com.google.common.base.Optional;

public enum PairingType {
    WHIRLPOOL_GUI("whirlpool.gui");

    private String value;

    PairingType(String value) {
        this.value = value;
    }

    public static Optional<PairingType> find(String value) {
      for (PairingType item : PairingType.values()) {
          if (item.value.equals(value)) {
              return Optional.of(item);
          }
      }
    return Optional.absent();
    }
  }