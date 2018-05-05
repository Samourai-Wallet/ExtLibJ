package com.samourai.wallet.bip47.rpc;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class SecretPoint {

    private PrivateKey privKey = null;
    private PublicKey pubKey = null;

    private KeyFactory kf = null;

    private static String providerName;

    private static Class classECParam;
    private static Object params;
    private static Object paramsCurve;
    private static Method methodCurveDecodePoint;

    private static Class classECPrivateKeySpec;
    private static Constructor constructorECPrivateKeySpec;
    private static Class classECPoint;

    private static Class classECPublicKeySpec;
    private static Constructor constructorECPublicKeySPec;

    static {
        try {
            // Android => spongycastle
            providerName = "SC";
            initStatic("org.spongycastle.");
        }
        catch(Exception e) {
            try {
                // java => bouncycastle
                providerName = "BC";
                initStatic("org.bouncycastle.");
            }
            catch (Exception ee) {
                throw new RuntimeException(ee);
            }
        }
    }

    private static void initStatic(String packageName) throws Exception {
        // load provider
        Class<Provider> classProvider = (Class<Provider>)Class.forName(packageName + "jce.provider.BouncyCastleProvider");
        Security.addProvider(classProvider.getDeclaredConstructor().newInstance());

        // pointers to classes, methods & constructors
        classECParam = Class.forName(packageName + "jce.spec.ECParameterSpec");
        Class classECNamedCurveTable = Class.forName(packageName + "jce.ECNamedCurveTable");
        params = classECNamedCurveTable.getMethod("getParameterSpec", String.class).invoke(null, "secp256k1");
        paramsCurve = classECParam.getMethod("getCurve").invoke(params);
        Class classECCurve = Class.forName(packageName + "math.ec.ECCurve");
        methodCurveDecodePoint = classECCurve.getMethod("decodePoint", byte[].class);

        classECPrivateKeySpec = Class.forName(packageName + "jce.spec.ECPrivateKeySpec");
        constructorECPrivateKeySpec = classECPrivateKeySpec.getConstructor(BigInteger.class, classECParam);

        classECPublicKeySpec = Class.forName(packageName + "jce.spec.ECPublicKeySpec");
        classECPoint = Class.forName(packageName + "math.ec.ECPoint");
        constructorECPublicKeySPec = classECPublicKeySpec.getConstructor(classECPoint, classECParam);
    }

    public SecretPoint(byte[] dataPrv, byte[] dataPub) throws InvalidKeySpecException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, NoSuchProviderException {
        kf = KeyFactory.getInstance("ECDH", providerName);
        privKey = loadPrivateKey(dataPrv);
        pubKey = loadPublicKey(dataPub);
    }

    public byte[] ECDHSecretAsBytes() throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, NoSuchProviderException    {
        return ECDHSecret().getEncoded();
    }

    private SecretKey ECDHSecret() throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, NoSuchProviderException    {

        KeyAgreement ka = KeyAgreement.getInstance("ECDH", providerName);
        ka.init(privKey);
        ka.doPhase(pubKey, true);
        SecretKey secret = ka.generateSecret("AES");

        return secret;
    }

    private PublicKey loadPublicKey(byte[] data) throws InvalidKeySpecException    {
        KeySpec pubKey;
        try {
            Object point = methodCurveDecodePoint.invoke(paramsCurve, data);
            pubKey = (KeySpec)constructorECPublicKeySPec.newInstance(point, params);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        return kf.generatePublic(pubKey);
    }

    private PrivateKey loadPrivateKey(byte[] data) throws InvalidKeySpecException  {
        KeySpec prvKey;
        try {
            prvKey = (KeySpec)constructorECPrivateKeySpec.newInstance(new BigInteger(1, data), params);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        return kf.generatePrivate(prvKey);
    }

}
