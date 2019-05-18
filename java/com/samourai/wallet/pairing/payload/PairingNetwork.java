package com.samourai.wallet.pairing.payload;

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
  }