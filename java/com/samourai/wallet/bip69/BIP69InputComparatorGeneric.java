package com.samourai.wallet.bip69;

import java.util.Comparator;

public abstract class BIP69InputComparatorGeneric<T> implements Comparator<T> {
    protected abstract byte[] getHash(T i);
    protected abstract long getIndex(T i);

    public int compare(T i1, T i2) {
        byte[] h1 = getHash(i1);
        byte[] h2 = getHash(i2);

        long index1 = getIndex(i1);
        long index2 = getIndex(i2);

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