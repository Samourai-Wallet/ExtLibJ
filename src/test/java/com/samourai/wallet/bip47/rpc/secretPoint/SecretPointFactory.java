package com.samourai.wallet.bip47.rpc.secretPoint;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class SecretPointFactory implements ISecretPointFactory {

    private static SecretPointFactory instance = null;

    public static SecretPointFactory getInstance() {
        return instance;
    }

    @Override
    public ISecretPoint newSecretPoint(byte[] dataPrv, byte[] dataPub) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException {
        return new SecretPoint(dataPrv, dataPub);
    }

}
