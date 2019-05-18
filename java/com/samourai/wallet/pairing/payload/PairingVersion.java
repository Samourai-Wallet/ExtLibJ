package com.samourai.wallet.pairing.payload;

import com.google.common.base.Optional;

public enum PairingVersion {
    V1_0_0("1.0.0");

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
  }