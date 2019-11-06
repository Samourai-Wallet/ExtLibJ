package com.samourai.wallet.bip69;

import com.google.common.primitives.Longs;

import java.util.Comparator;

public abstract class BIP69OutputComparatorGeneric<T> implements Comparator<T> {
    protected abstract byte[] getScriptBytes(T i);
    protected abstract long getValue(T i);

    public int compare(T o1, T o2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        long o1Value = getValue(o1);
        long o2Value = getValue(o2);

        if(Longs.compare(o1Value, o2Value) > 0) {
            return AFTER;
        }
        else if(Longs.compare(o1Value, o2Value) < 0) {
            return BEFORE;
        }
        else    {

            byte[] b1 = getScriptBytes(o1);
            byte[] b2 = getScriptBytes(o2);

            int pos = 0;
            while(pos < b1.length && pos < b2.length)    {

                if((b1[pos] & 0xff) < (b2[pos] & 0xff))    {
                    return BEFORE;
                }
                else if((b1[pos] & 0xff) > (b2[pos] & 0xff))    {
                    return AFTER;
                }

                pos++;
            }

            if(b1.length < b2.length)    {
                return BEFORE;
            }
            else if(b1.length > b2.length)    {
                return AFTER;
            }
            else    {
                return EQUAL;
            }

        }

    }

}