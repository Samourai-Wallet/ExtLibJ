package com.samourai.wallet.bip69;

import org.bitcoinj.core.TransactionInput;

public class BIP69InputComparator extends BIP69InputComparatorGeneric<TransactionInput> {
    @Override
    protected byte[] getHash(TransactionInput i) {
        return i.getOutpoint().getHash().getBytes();
    }

    @Override
    protected long getIndex(TransactionInput i) {
        return i.getOutpoint().getIndex();
    }
}