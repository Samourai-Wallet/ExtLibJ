package com.samourai.wallet.bip47.rpc.impl;

import com.samourai.wallet.bip47.BIP47UtilGeneric;

public class Bip47Util extends BIP47UtilGeneric {

    public Bip47Util() {
        super(SecretPointFactory.getInstance());
    }
}
