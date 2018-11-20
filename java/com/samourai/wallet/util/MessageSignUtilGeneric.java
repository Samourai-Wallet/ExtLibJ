package com.samourai.wallet.util;

import java.security.SignatureException;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;

public class MessageSignUtilGeneric {

    private static MessageSignUtilGeneric instance = null;

    private MessageSignUtilGeneric() { ; }

    public static MessageSignUtilGeneric getInstance() {

        if(instance == null) {
            instance = new MessageSignUtilGeneric();
        }

        return instance;
    }

    public boolean verifySignedMessage(String address, String strMessage, String strSignature, NetworkParameters params) throws SignatureException {

        if(address == null || strMessage == null || strSignature == null)    {
            return false;
        }

        ECKey ecKey = signedMessageToKey(strMessage, strSignature);
        if(ecKey != null)   {
            return ecKey.toAddress(params).toString().equals(address);
        }
        else    {
            return false;
        }
    }

    public String signMessage(ECKey key, String strMessage) {

        if(key == null || strMessage == null || !key.hasPrivKey())    {
            return null;
        }

        return key.signMessage(strMessage);
    }

    private String signMessageArmored(ECKey key, String strMessage, NetworkParameters params) {

        String sig = signMessage(key, strMessage);
        String ret = null;

        if(sig != null)    {
            ret = "-----BEGIN BITCOIN SIGNED MESSAGE-----\n";
            ret += strMessage;
            ret += "\n";
            ret += "-----BEGIN BITCOIN SIGNATURE-----\n";
            ret += "Version: Bitcoin-qt (1.0)\n";
            ret += "Address: " + key.toAddress(params).toString() + "\n\n";
            ret += sig;
            ret += "\n";
            ret += "-----END BITCOIN SIGNATURE-----\n";
        }

        return ret;
    }

    private ECKey signedMessageToKey(String strMessage, String strSignature) throws SignatureException {

        if(strMessage == null || strSignature == null)    {
            return null;
        }

        return ECKey.signedMessageToKey(strMessage, strSignature);
    }

}
