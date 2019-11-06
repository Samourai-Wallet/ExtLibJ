package com.samourai.wallet.bip69;

import org.bitcoinj.core.TransactionOutput;

public class BIP69OutputComparator extends BIP69OutputComparatorGeneric<TransactionOutput> {
    @Override
    protected long getValue(TransactionOutput i) {
        return i.getValue().getValue();
    }

    @Override
    protected byte[] getScriptBytes(TransactionOutput i) {
        return i.getScriptBytes();
    }
}