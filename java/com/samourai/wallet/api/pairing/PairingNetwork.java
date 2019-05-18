package com.samourai.wallet.api.pairing;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;

public enum PairingNetwork {
    MAINNET("mainnet"),
    TESTNET("testnet");

    private String value;

    PairingNetwork(String value) {
        this.value = value;
    }

    public static Optional<PairingNetwork> find(String value) {
        for (PairingNetwork item : PairingNetwork.values()) {
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