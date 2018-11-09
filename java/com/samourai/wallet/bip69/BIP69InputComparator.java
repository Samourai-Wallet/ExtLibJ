package com.samourai.wallet.bip69;

import org.bitcoinj.core.TransactionInput;

import java.util.Comparator;

public class BIP69InputComparator implements Comparator<TransactionInput> {

    public int compare(TransactionInput i1, TransactionInput i2) {
        byte[] h1 = i1.getOutpoint().getHash().getBytes();
        byte[] h2 = i2.getOutpoint().getHash().getBytes();

        long index1 = i1.getOutpoint().getIndex();
        long index2 = i2.getOutpoint().getIndex();

        return compare(h1, h2, index1, index2);
    }

    public int compare(byte[] h1, byte[] h2, long index1, long index2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

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

        if(index1 < index2)    {
            return BEFORE;
        }
        else if(index1 > index2)    {
            return AFTER;
        }
        else    {
            return EQUAL;
        }

    }

}