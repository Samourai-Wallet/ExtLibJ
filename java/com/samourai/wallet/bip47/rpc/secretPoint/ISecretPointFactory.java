package com.samourai.wallet.bip47.rpc.secretPoint;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public interface ISecretPointFactory {

    ISecretPoint newSecretPoint(byte[] dataPrv, byte[] dataPub) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException;

}
