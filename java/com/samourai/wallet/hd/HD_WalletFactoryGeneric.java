package com.samourai.wallet.hd;

import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.util.FormatsUtilGeneric;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

public class HD_WalletFactoryGeneric {
  public static final String BIP39_ENGLISH_SHA256 = "ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db";
  public static final String BIP39_ENGLISH_FILENAME = "/BIP39/en.txt";

  private MnemonicCode mc;

  public HD_WalletFactoryGeneric(MnemonicCode mc) {
    this.mc = mc;
  }

  public HD_Wallet restoreWallet(String data, String passphrase, int nbAccounts, NetworkParameters params)
      throws AddressFormatException, DecoderException,
          MnemonicException.MnemonicLengthException, MnemonicException.MnemonicWordException,
          MnemonicException.MnemonicChecksumException {

    HD_Wallet hdw = null;

    if (passphrase == null) {
      passphrase = "";
    }

    if (data.matches(FormatsUtilGeneric.XPUB)) {
      String[] xpub = data.split(":");
      hdw = new HD_Wallet(params, xpub);
    } else if (data.matches(FormatsUtilGeneric.HEX) && data.length() % 4 == 0) {
      byte[] seed = Hex.decodeHex(data.toCharArray());
      hdw = new HD_Wallet(44, mc, params, seed, passphrase, nbAccounts);
    } else {
      byte[] seed = computeSeedFromWords(data);
      hdw = new HD_Wallet(44, mc, params, seed, passphrase, nbAccounts);
    }
    return hdw;
  }

  public byte[] computeSeedFromWords(String data) throws AddressFormatException,
      MnemonicException.MnemonicLengthException, MnemonicException.MnemonicWordException,
      MnemonicException.MnemonicChecksumException {
    data = data.toLowerCase().replaceAll("[^a-z]+", " "); // only use for BIP39 English
    List<String> words = Arrays.asList(data.trim().split("\\s+"));
    return computeSeedFromWords(words);
  }

  public byte[] computeSeedFromWords(List<String> words) throws AddressFormatException,
      MnemonicException.MnemonicLengthException, MnemonicException.MnemonicWordException,
      MnemonicException.MnemonicChecksumException {
    byte[] seed = mc.toEntropy(words);
    return seed;
  }

  public BIP47Wallet getBIP47(String seed, String passphrase, NetworkParameters params) throws MnemonicException.MnemonicLengthException {
    BIP47Wallet hdw47 = new BIP47Wallet(47, mc, params, org.bouncycastle.util.encoders.Hex.decode(seed), passphrase, 1);
    return hdw47;
  }

  public HD_Wallet getHD(int purpose, byte[] seed, String passphrase, NetworkParameters params) throws MnemonicException.MnemonicLengthException {
    HD_Wallet hdw = new HD_Wallet(purpose, mc, params, seed, passphrase, 1);
    return hdw;
  }

  public HD_Wallet getBIP49(byte[] seed, String passphrase, NetworkParameters params) throws MnemonicException.MnemonicLengthException {
    return getHD(49, seed, passphrase, params);
  }

  public HD_Wallet getBIP84(byte[] seed, String passphrase, NetworkParameters params) throws MnemonicException.MnemonicLengthException {
    return getHD(84, seed, passphrase, params);
  }
}
