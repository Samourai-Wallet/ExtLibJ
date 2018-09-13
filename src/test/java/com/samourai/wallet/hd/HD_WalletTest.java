package com.samourai.wallet.hd;

import com.samourai.wallet.utils.TestUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class HD_WalletTest {
    private static final NetworkParameters params = TestNet3Params.get();

    @Test
    public void testHdWallet() throws Exception {
        InputStream wis = HD_Wallet.class.getResourceAsStream("/BIP39/en.txt");
        MnemonicCode mc = new MnemonicCode(wis, TestUtils.BIP39_ENGLISH_SHA256);

        HD_Wallet hdWallet1 = new HD_Wallet(44, mc, params, "foo1".getBytes(), "test1", 1);
        HD_Wallet hdWallet2 = new HD_Wallet(44, mc, params, "foo1".getBytes(), "test2", 1);

        HD_Wallet hdWallet3 = new HD_Wallet(44, mc, params, "foo2".getBytes(), "test1", 1);
        HD_Wallet hdWallet4 = new HD_Wallet(44, mc, params, "foo2".getBytes(), "test2", 1);

        HD_Wallet hdWallet1Copy = new HD_Wallet(44, hdWallet1, 1);

        // verify
        Assertions.assertArrayEquals(new String[]{"tpubDCLffiuyQ6v4AhNLp1FJiJYcuJv1pdA14dunQdhfpUWpYmkAZuhEnNNubo3fxMb3iWKb3jpkpWspH5YBRSzwfDuiCrsJR4UEXnobLJ3mKJa"}, hdWallet1.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDCLffiuyQ6v4AhNLp1FJiJYcuJv1pdA14dunQdhfpUWpYmkAZuhEnNNubo3fxMb3iWKb3jpkpWspH5YBRSzwfDuiCrsJR4UEXnobLJ3mKJa"}, hdWallet1Copy.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDCWCxiYrpwtjMzdftG9pdcMEj4mRqwpRvVGGzduu4eT9ZjLRPsHTbTsejNz1WufxcY55PMVabkAaYuP76Qau576XZu8qAP8TpFT2Skc1k2X"}, hdWallet2.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDDr2YdixLfDwauFXHYoM92pKjh8bL1RBrF6XqfpsibUoUCj5ouj44CV3ufBk79rhv2L8tXWyH9m4DshUTdcZ1iRtVhXTBcN9ksoJk3jDksV"}, hdWallet3.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDDn8QvxpH2z7gb4acz6iwqLBHfbSWkFnLi3eZ1gLyGSYbyD2NaFq8cW6SNkQF22uf2Den9ZqKXxfUynNo1ieMHEktR9h9sbbh4ijbdXCLiU"}, hdWallet4.getXPUBs());

        Assertions.assertEquals("mrZ4DHwdzK4pAuKnoVL4vaeHeYe8e12hn2", hdWallet1.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mrZ4DHwdzK4pAuKnoVL4vaeHeYe8e12hn2", hdWallet1Copy.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mxQEdFhaXGWf3a6G4M5Ti11uwUXBEQW2FZ", hdWallet2.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mwTMnoSzutrExZWd9zJRtehXBwbEerpdtM", hdWallet3.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("moupnWvhQxzFZWRQSPoMaeMY6RPGsMCnQ6", hdWallet4.getAccount(0).getChain(0).getAddressAt(0).getAddressString());

        Assertions.assertEquals("mpL7ecpEyzUSr1y2x8HgxzN4ZRaVS7mFKQ", hdWallet1.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mpL7ecpEyzUSr1y2x8HgxzN4ZRaVS7mFKQ", hdWallet1Copy.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("miStZQEnDz895mxQD9yCUXh7PqEwza8Ezj", hdWallet2.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mszNy5PjqR6Fn8bwj89Ns1BkqjENhBWsyq", hdWallet3.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("n3VnQGNEy6XGzJmK9d4tfSJWFDbc62uRf5", hdWallet4.getAccount(0).getChain(1).getAddressAt(0).getAddressString());

        Assertions.assertEquals("mg6xV9QGhkxqrTFeBpLKnJ3ERbW7qPFXwd", hdWallet1.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("mg6xV9QGhkxqrTFeBpLKnJ3ERbW7qPFXwd", hdWallet1Copy.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("mmftBoRdUdP79S9dBh2gYzyNzpqHsT12nR", hdWallet2.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("mttGD9kAtcA4a4Uu2WC8AZf1tdM8VdqvC1", hdWallet3.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("mmXjbaYTVSp9bUTLdkQF7QrYsRAFbqUMxc", hdWallet4.getAccount(0).getChain(1).getAddressAt(1).getAddressString());

    }

}
