package com.samourai.wallet.bip69;

import org.bitcoinj.core.TransactionOutput;

import java.util.Comparator;

public class BIP69OutputComparator implements Comparator<TransactionOutput> {

    public int compare(TransactionOutput o1, TransactionOutput o2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if(o1.getValue().compareTo(o2.getValue()) > 0) {
            return AFTER;
        }
        else if(o1.getValue().compareTo(o2.getValue()) < 0) {
            return BEFORE;
        }
        else    {

            byte[] b1 = o1.getScriptBytes();
            byte[] b2 = o2.getScriptBytes();

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