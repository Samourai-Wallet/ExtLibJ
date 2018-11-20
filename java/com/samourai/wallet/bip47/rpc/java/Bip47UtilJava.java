package com.samourai.wallet.bip47.rpc.java;

import com.samourai.wallet.bip47.BIP47UtilGeneric;
import com.samourai.wallet.bip47.rpc.secretPoint.ISecretPoint;
import com.samourai.wallet.bip47.rpc.secretPoint.ISecretPointFactory;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class Bip47UtilJava extends BIP47UtilGeneric {

  private static Bip47UtilJava instance;

  public static Bip47UtilJava getInstance() {
    if (instance == null) {
      instance = new Bip47UtilJava();
    }
    return instance;
  }

  private static final ISecretPointFactory secretPointFactory =
      new ISecretPointFactory() {
        @Override
        public ISecretPoint newSecretPoint(byte[] dataPrv, byte[] dataPub)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException,
                InvalidKeyException {
          return new SecretPointJava(dataPrv, dataPub);
        }
      };

  private Bip47UtilJava() {
    super(secretPointFactory);
  }
}
