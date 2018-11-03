package com.samourai.wallet.segwit.bech32;

import org.apache.commons.lang3.tuple.Pair;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;

import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;

public class Bech32UtilGeneric {

    private static Bech32UtilGeneric instance = null;

    private Bech32UtilGeneric() { ; }

    public static Bech32UtilGeneric getInstance() {

        if(instance == null) {
            instance = new Bech32UtilGeneric();
        }

        return instance;
    }

    public boolean isBech32Script(String script) {
        return isP2WPKHScript(script) || isP2WSHScript(script);
    }

    public boolean isP2WPKHScript(String script) {
        return script.startsWith("0014") && script.length() == (20 * 2 + 2 * 2);
    }

    public boolean isP2WSHScript(String script) {
        return script.startsWith("0020") && script.length() == (32 * 2 + 2 * 2);
    }

    public String getAddressFromScript(String script, NetworkParameters params) throws Exception    {

        String hrp = null;
        if(params instanceof TestNet3Params)    {
            hrp = "tb";
        }
        else    {
            hrp = "bc";
        }

        return Bech32Segwit.encode(hrp, (byte)0x00, Hex.decode(script.substring(4).getBytes()));
    }

    public String getAddressFromScript(Script script, NetworkParameters params) throws Exception    {

        String hrp = null;
        if(params instanceof TestNet3Params)    {
            hrp = "tb";
        }
        else    {
            hrp = "bc";
        }

        byte[] buf = script.getProgram();
        byte[] scriptBytes = new byte[buf.length - 2];
        System.arraycopy(buf, 2, scriptBytes, 0, scriptBytes.length);

        return Bech32Segwit.encode(hrp, (byte)0x00, scriptBytes);
    }

    public TransactionOutput getTransactionOutput(String address, long value, NetworkParameters params) throws Exception {
        byte[] scriptPubKey = computeScriptPubKey(address, params);
        return new TransactionOutput(params, null, Coin.valueOf(value), scriptPubKey);
    }

    public byte[] computeScriptPubKey(String address, NetworkParameters params) throws Exception {
        // decode bech32
        boolean isTestNet = !(params instanceof MainNetParams);
        Pair<Byte, byte[]> pair = Bech32Segwit.decode(isTestNet ? "tb" : "bc", address);

        // get scriptPubkey
        return Bech32Segwit.getScriptPubkey(pair.getLeft(), pair.getRight());
    }

}
