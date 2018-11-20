package com.samourai.wallet.hd.java;

import com.samourai.wallet.hd.HD_WalletFactoryGeneric;
import java.io.IOException;
import java.io.InputStream;
import org.bitcoinj.crypto.MnemonicCode;

public class HD_WalletFactoryJava extends HD_WalletFactoryGeneric {
  private static HD_WalletFactoryJava instance = null;

  public HD_WalletFactoryJava() {
    super(computeMnemonicCode());
  }

  public static HD_WalletFactoryJava getInstance() {
    if(instance == null) {
      instance = new HD_WalletFactoryJava();
    }
    return instance;
  }

  public static MnemonicCode computeMnemonicCode() {
    try {
      InputStream wis = HD_WalletFactoryJava.class
          .getResourceAsStream(HD_WalletFactoryJava.BIP39_ENGLISH_FILENAME);
      MnemonicCode mc = new MnemonicCode(wis, HD_WalletFactoryJava.BIP39_ENGLISH_SHA256);
      wis.close();
      return mc;
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
