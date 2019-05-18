package com.samourai.wallet.api.pairing;

import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonValue
    public String getValue() {
        return value;
    }
  }