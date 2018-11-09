package com.samourai.wallet.utils;

import com.samourai.wallet.hd.HD_Wallet;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;

import java.io.InputStream;
import java.security.SecureRandom;

public class TestUtils {
    private static final SecureRandom random = new SecureRandom();
    public static final String BIP39_ENGLISH_SHA256 = "ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db";

    public static HD_Wallet generateWallet(int purpose, byte[] seed, String passphrase, NetworkParameters networkParameters) throws Exception {
        InputStream wis = HD_Wallet.class.getResourceAsStream("/BIP39/en.txt");
        if (wis != null) {
            MnemonicCode mc = new MnemonicCode(wis, BIP39_ENGLISH_SHA256);

            // init BIP44 wallet for input
            HD_Wallet inputWallet = new HD_Wallet(purpose, mc, networkParameters, seed, passphrase, 1);

            wis.close();
            return inputWallet;
        }
        throw new Exception("wis is null");
    }

    public static HD_Wallet generateWallet(int purpose, NetworkParameters networkParameters) throws Exception {
        int nbWords = 12;
        // len == 16 (12 words), len == 24 (18 words), len == 32 (24 words)
        int len = (nbWords / 3) * 4;

        byte seed[] = new byte[len];
        random.nextBytes(seed);

        return generateWallet(purpose, seed, "test", networkParameters);
    }

}
