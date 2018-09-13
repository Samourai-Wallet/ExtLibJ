package com.samourai.wallet.bip47;

import com.samourai.wallet.bip47.rpc.*;
import com.samourai.wallet.bip47.rpc.secretPoint.ISecretPointFactory;
import com.samourai.wallet.hd.HD_Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class BIP47UtilGeneric {

    private static BIP47UtilGeneric instance = null;

    public static BIP47UtilGeneric getInstance() {

        if(instance == null) {
            instance = new BIP47UtilGeneric();
        }

        return instance;
    }

    public HD_Address getNotificationAddress(BIP47Wallet wallet) {
        return wallet.getAccount(0).getNotificationAddress();
    }

    public HD_Address getNotificationAddress(BIP47Wallet wallet, int account) {
        return wallet.getAccount(account).getNotificationAddress();
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getPaymentCode(BIP47Wallet wallet) throws AddressFormatException   {
        String payment_code = wallet.getAccount(0).getPaymentCode();
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code);
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getPaymentCode(BIP47Wallet wallet, int account) throws AddressFormatException   {
        String payment_code = wallet.getAccount(account).getPaymentCode();
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code);
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getFeaturePaymentCode(BIP47Wallet wallet) throws AddressFormatException   {
        PaymentCode payment_code = getPaymentCode(wallet);
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code.makeSamouraiPaymentCode());
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getFeaturePaymentCode(BIP47Wallet wallet, int account) throws AddressFormatException   {
        PaymentCode payment_code = getPaymentCode(wallet, account);
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code.makeSamouraiPaymentCode());
    }

    public PaymentAddress getReceiveAddress(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        HD_Address address = wallet.getAccount(0).addressAt(idx);
        return getPaymentAddress(pcode, 0, address, params);
    }

    public PaymentAddress getReceiveAddress(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        HD_Address address = wallet.getAccount(account).addressAt(idx);
        return getPaymentAddress(pcode, 0, address, params);
    }

    public String getReceivePubKey(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params, ISecretPointFactory secretPointFactory) throws AddressFormatException, NotSecp256k1Exception, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PaymentAddress paymentAddress = getReceiveAddress(wallet, pcode, idx, params);
        return Hex.toHexString(paymentAddress.getReceiveECKey(secretPointFactory).getPubKey());
    }

    public String getReceivePubKey(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params, ISecretPointFactory secretPointFactory) throws AddressFormatException, NotSecp256k1Exception, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PaymentAddress paymentAddress = getReceiveAddress(wallet, account, pcode, idx, params);
        return Hex.toHexString(paymentAddress.getReceiveECKey(secretPointFactory).getPubKey());
    }

    public PaymentAddress getSendAddress(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        HD_Address address = wallet.getAccount(0).addressAt(0);
        return getPaymentAddress(pcode, idx, address, params);
    }

    public PaymentAddress getSendAddress(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        HD_Address address = wallet.getAccount(account).addressAt(0);
        return getPaymentAddress(pcode, idx, address, params);
    }

    public String getSendPubKey(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params, ISecretPointFactory secretPointFactory) throws AddressFormatException, NotSecp256k1Exception, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PaymentAddress paymentAddress = getSendAddress(wallet, pcode, idx, params);
        return Hex.toHexString(paymentAddress.getSendECKey(secretPointFactory).getPubKey());
    }

    public String getSendPubKey(BIP47Wallet wallet, int account, PaymentCode pcode, int idx, NetworkParameters params, ISecretPointFactory secretPointFactory) throws AddressFormatException, NotSecp256k1Exception, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PaymentAddress paymentAddress = getSendAddress(wallet, account, pcode, idx, params);
        return Hex.toHexString(paymentAddress.getSendECKey(secretPointFactory).getPubKey());
    }

    public byte[] getIncomingMask(BIP47Wallet wallet, byte[] pubkey, byte[] outPoint, NetworkParameters params, ISecretPointFactory secretPointFactory) throws AddressFormatException, Exception    {

        HD_Address notifAddress = getNotificationAddress(wallet);
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, notifAddress.getPrivateKeyString());
        ECKey inputKey = dpk.getKey();
        byte[] privkey = inputKey.getPrivKeyBytes();
        byte[] mask = com.samourai.wallet.bip47.rpc.PaymentCode.getMask(secretPointFactory.newSecretPoint(privkey, pubkey).ECDHSecretAsBytes(), outPoint);

        return mask;
    }

    public byte[] getIncomingMask(BIP47Wallet wallet, int account, byte[] pubkey, byte[] outPoint, NetworkParameters params, ISecretPointFactory secretPointFactory) throws AddressFormatException, Exception    {

        HD_Address notifAddress = getNotificationAddress(wallet, account);
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, notifAddress.getPrivateKeyString());
        ECKey inputKey = dpk.getKey();
        byte[] privkey = inputKey.getPrivKeyBytes();
        byte[] mask = com.samourai.wallet.bip47.rpc.PaymentCode.getMask(secretPointFactory.newSecretPoint(privkey, pubkey).ECDHSecretAsBytes(), outPoint);

        return mask;
    }

    public PaymentAddress getPaymentAddress(PaymentCode pcode, int idx, HD_Address address, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, address.getPrivateKeyString());
        ECKey eckey = dpk.getKey();
        PaymentAddress paymentAddress = new PaymentAddress(pcode, idx, eckey.getPrivKeyBytes(), params);
        return paymentAddress;
    }

}
