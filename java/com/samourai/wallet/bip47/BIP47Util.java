package com.samourai.wallet.bip47;

import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.bip47.rpc.NotSecp256k1Exception;
import com.samourai.wallet.bip47.rpc.PaymentAddress;
import com.samourai.wallet.bip47.rpc.PaymentCode;
import com.samourai.wallet.hd.HD_Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;

public class BIP47Util {

    private static BIP47Util instance = null;

    public static BIP47Util getInstance() {

        if(instance == null) {
            instance = new BIP47Util();
        }

        return instance;
    }

    public HD_Address getNotificationAddress(BIP47Wallet wallet) {
        return wallet.getAccount(0).getNotificationAddress();
    }

    public com.samourai.wallet.bip47.rpc.PaymentCode getPaymentCode(BIP47Wallet wallet) throws AddressFormatException   {
        String payment_code = wallet.getAccount(0).getPaymentCode();
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code);
    }

    public PaymentCode getFeaturePaymentCode(BIP47Wallet wallet) throws AddressFormatException   {
        PaymentCode payment_code = getPaymentCode(wallet);
        return new com.samourai.wallet.bip47.rpc.PaymentCode(payment_code.makeSamouraiPaymentCode());
    }

    public PaymentAddress getReceiveAddress(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        HD_Address address = wallet.getAccount(0).addressAt(idx);
        return getPaymentAddress(pcode, 0, address, params);
    }

    public PaymentAddress getSendAddress(BIP47Wallet wallet, PaymentCode pcode, int idx, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        HD_Address address = wallet.getAccount(0).addressAt(0);
        return getPaymentAddress(pcode, idx, address, params);
    }

    public byte[] getIncomingMask(BIP47Wallet wallet, byte[] pubkey, byte[] outPoint, NetworkParameters params) throws AddressFormatException, Exception    {

        HD_Address notifAddress = getNotificationAddress(wallet);
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, notifAddress.getPrivateKeyString());
        ECKey inputKey = dpk.getKey();
        byte[] privkey = inputKey.getPrivKeyBytes();
        byte[] mask = com.samourai.wallet.bip47.rpc.PaymentCode.getMask(new com.samourai.wallet.bip47.rpc.SecretPoint(privkey, pubkey).ECDHSecretAsBytes(), outPoint);

        return mask;
    }

    public PaymentAddress getPaymentAddress(PaymentCode pcode, int idx, HD_Address address, NetworkParameters params) throws AddressFormatException, NotSecp256k1Exception {
        DumpedPrivateKey dpk = new DumpedPrivateKey(params, address.getPrivateKeyString());
        ECKey eckey = dpk.getKey();
        PaymentAddress paymentAddress = new PaymentAddress(pcode, idx, eckey.getPrivKeyBytes(), params);
        return paymentAddress;
    }

}
