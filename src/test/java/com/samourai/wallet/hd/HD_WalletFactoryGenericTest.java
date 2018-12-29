package com.samourai.wallet.hd;

import com.samourai.wallet.hd.java.HD_WalletFactoryJava;
import java.math.BigInteger;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HD_WalletFactoryGenericTest {
    private HD_WalletFactoryJava hdWalletFactory = HD_WalletFactoryJava.getInstance();

    @Test
    public void restoreWallet_words() throws Exception {
        NetworkParameters params = MainNetParams.get();
        HD_Wallet hdWallet;
        String passphrase = "TREZOR";

        // https://github.com/trezor/python-mnemonic/blob/master/vectors.json

        hdWallet = hdWalletFactory.restoreWallet("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about", passphrase, 1, params);
        Assertions.assertEquals("00000000000000000000000000000000", hdWallet.getSeedHex());
        Assertions.assertEquals(passphrase, hdWallet.getPassphrase());
        Assertions.assertEquals(new BigInteger("98271371290466655911529653016182302339914803975553744677741258513646170498272"), hdWallet.mRoot.getPrivKey());

        hdWallet = hdWalletFactory.restoreWallet("legal winner thank year wave sausage worth useful legal winner thank yellow", passphrase, 1, params);
        Assertions.assertEquals("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f", hdWallet.getSeedHex());
        Assertions.assertEquals(passphrase, hdWallet.getPassphrase());
        Assertions.assertEquals(new BigInteger("12662160214494740211391410586234565047416291165663256642181573049330307149082"), hdWallet.mRoot.getPrivKey());

        hdWallet = hdWalletFactory.restoreWallet("letter advice cage absurd amount doctor acoustic avoid letter advice cage above", passphrase, 1, params);
        Assertions.assertEquals("80808080808080808080808080808080", hdWallet.getSeedHex());
        Assertions.assertEquals(passphrase, hdWallet.getPassphrase());
        Assertions.assertEquals(new BigInteger("63978822330767128043945360358261997375960365869251665953739239749472592461997"), hdWallet.mRoot.getPrivKey());

        hdWallet = hdWalletFactory.restoreWallet("void come effort suffer camp survey warrior heavy shoot primary clutch crush open amazing screen patrol group space point ten exist slush involve unfold", passphrase, 1, params);
        Assertions.assertEquals("f585c11aec520db57dd353c69554b21a89b20fb0650966fa0a9d6f74fd989d8f", hdWallet.getSeedHex());
        Assertions.assertEquals(passphrase, hdWallet.getPassphrase());
        Assertions.assertEquals(new BigInteger("11902135336207312062323482070347138053483447968293492732980096496846405108646"), hdWallet.mRoot.getPrivKey());
    }

    @Test
    public void computeSeedFromWords() throws Exception {
        NetworkParameters params = MainNetParams.get();
        HD_Wallet hdWallet;
        byte[] mSeed;
        String passphrase = "TREZOR";

        // https://github.com/trezor/python-mnemonic/blob/master/vectors.json

        mSeed = hdWalletFactory.computeSeedFromWords("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about");
        Assertions.assertEquals("00000000000000000000000000000000", org.bouncycastle.util.encoders.Hex.toHexString(mSeed));

        mSeed = hdWalletFactory.computeSeedFromWords("legal winner thank year wave sausage worth useful legal winner thank yellow");
        Assertions.assertEquals("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f", org.bouncycastle.util.encoders.Hex.toHexString(mSeed));

        mSeed = hdWalletFactory.computeSeedFromWords("letter advice cage absurd amount doctor acoustic avoid letter advice cage above");
        Assertions.assertEquals("80808080808080808080808080808080", org.bouncycastle.util.encoders.Hex.toHexString(mSeed));

        mSeed = hdWalletFactory.computeSeedFromWords("void come effort suffer camp survey warrior heavy shoot primary clutch crush open amazing screen patrol group space point ten exist slush involve unfold");
        Assertions.assertEquals("f585c11aec520db57dd353c69554b21a89b20fb0650966fa0a9d6f74fd989d8f", org.bouncycastle.util.encoders.Hex.toHexString(mSeed));
    }
}
