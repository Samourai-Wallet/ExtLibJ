package com.samourai.wallet.bip47.rpc.secretPoint;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface ISecretPoint {

    byte[] ECDHSecretAsBytes() throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, NoSuchProviderException;

}