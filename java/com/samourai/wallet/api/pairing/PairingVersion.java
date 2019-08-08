package com.samourai.wallet.api.pairing;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;

public enum PairingVersion {
    V1_0_0("1.0.0"),
    V2_0_0("2.0.0"),
    V3_0_0("3.0.0");

    private String value;

    PairingVersion(String value) {
        this.value = value;
    }

    public static Optional<PairingVersion> find(String value) {
      for (PairingVersion item : PairingVersion.values()) {
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