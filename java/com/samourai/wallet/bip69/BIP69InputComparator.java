package com.samourai.wallet.bip69;

import org.bitcoinj.core.TransactionInput;

import java.util.Comparator;

public class BIP69InputComparator implements Comparator<TransactionInput> {

    public int compare(TransactionInput i1, TransactionInput i2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        byte[] h1 = i1.getOutpoint().getHash().getBytes();
        byte[] h2 = i2.getOutpoint().getHash().getBytes();

        int pos = 0;
        while(pos < h1.length && pos < h2.length)    {

            byte b1 = h1[pos];
            byte b2 = h2[pos];

            if((b1 & 0xff) < (b2 & 0xff))    {
                return BEFORE;
            }
            else if((b1 & 0xff) > (b2 & 0xff))    {
                return AFTER;
            }
            else    {
                pos++;
            }

        }

        if(i1.getOutpoint().getIndex() < i2.getOutpoint().getIndex())    {
            return BEFORE;
        }
        else if(i1.getOutpoint().getIndex() > i2.getOutpoint().getIndex())    {
            return AFTER;
        }
        else    {
            return EQUAL;
        }

    }

}