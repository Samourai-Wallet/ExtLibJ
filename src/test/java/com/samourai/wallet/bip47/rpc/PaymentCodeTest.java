package com.samourai.wallet.bip47.rpc;

import com.samourai.wallet.bip47.rpc.impl.Bip47Util;
import com.samourai.wallet.bip47.rpc.impl.SecretPoint;
import com.samourai.wallet.bip47.rpc.secretPoint.ISecretPoint;
import com.samourai.wallet.hd.HD_Address;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.utils.TestUtils;
import java.nio.ByteBuffer;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PaymentCodeTest {
    private static final NetworkParameters params = TestNet3Params.get();
    private static final Bip47Util bip47Util = new Bip47Util();

    @Test
    public void testPaymentCode() throws Exception {
        HD_Wallet bip44Wallet1 = TestUtils.generateWallet(44, params);
        HD_Wallet bip44Wallet2 = TestUtils.generateWallet(44, params);

        BIP47Wallet bip47Wallet1 = new BIP47Wallet(47, bip44Wallet1, 1);
        BIP47Wallet bip47Wallet2 = new BIP47Wallet(47, bip44Wallet2, 1);

        PaymentCode paymentCode1 = new PaymentCode(bip47Wallet1.getAccount(0).getPaymentCode());
        PaymentCode paymentCode2 = new PaymentCode(bip47Wallet2.getAccount(0).getPaymentCode());

        int idx = 0;

        // calculate send addresses
        SegwitAddress sendAddress1 = bip47Util.getSendAddress(bip47Wallet1, paymentCode2, idx, params).getSegwitAddressSend();
        SegwitAddress sendAddress2 = bip47Util.getSendAddress(bip47Wallet2, paymentCode1, idx, params).getSegwitAddressSend();

        // calculate receive addresses
        SegwitAddress receiveAddress1 = bip47Util.getReceiveAddress(bip47Wallet1, paymentCode2, idx, params).getSegwitAddressReceive();
        SegwitAddress receiveAddress2 = bip47Util.getReceiveAddress(bip47Wallet2, paymentCode1, idx, params).getSegwitAddressReceive();

        // mutual confrontation should give same result
        Assertions.assertEquals(sendAddress1.getBech32AsString(), receiveAddress2.getBech32AsString());
        Assertions.assertEquals(receiveAddress1.getBech32AsString(), sendAddress2.getBech32AsString());
    }

    @Test
    public void testXorMask() throws Exception {
        SegwitAddress inputAddress = TestUtils.generateSegwitAddress(params);
        TransactionOutPoint inputOutPoint = TestUtils.generateTransactionOutPoint(inputAddress.getBech32AsString(), 999999, params);
        ECKey inputKey = inputAddress.getECKey();

        byte[] data = ByteBuffer.allocate(64).putInt(1234).array();

        ECKey secretWalletKey = new ECKey();

        // mask
        ISecretPoint secretPointMask = new SecretPoint(inputKey.getPrivKeyBytes(), secretWalletKey.getPubKey());
        byte[] dataMasked = PaymentCode.xorMask(data, secretPointMask, inputOutPoint);

        // unmask
        ISecretPoint secretPointUnmask = new SecretPoint(secretWalletKey.getPrivKeyBytes(), inputKey.getPubKey());
        byte[] dataUnmasked = PaymentCode.xorMask(dataMasked, secretPointUnmask, inputOutPoint);

        // verify
        Assertions.assertArrayEquals(data, dataUnmasked);
    }

    @Test
    public void testXorMaskClientServer() throws Exception {
        SegwitAddress inputAddress = TestUtils.generateSegwitAddress(params);
        TransactionOutPoint inputOutPoint = TestUtils.generateTransactionOutPoint(inputAddress.getBech32AsString(), 999999, params);
        ECKey inputKey = inputAddress.getECKey();

        byte[] data = ByteBuffer.allocate(64).putInt(1234).array();

        BIP47Wallet bip47Wallet = TestUtils.generateBip47Wallet(params);
        String paymentCodeStr = bip47Wallet.getAccount(0).getPaymentCode();

        // mask: client side
        HD_Address notifAddressCli = new PaymentCode(paymentCodeStr).notificationAddress(params);
        ISecretPoint secretPointMask = new SecretPoint(inputKey.getPrivKeyBytes(), notifAddressCli.getPubKey());
        byte[] dataMasked = PaymentCode.xorMask(data, secretPointMask, inputOutPoint);

        // unmask: server side
        HD_Address notifAddressServer = bip47Wallet.getAccount(0).getNotificationAddress();
        ISecretPoint secretPointUnmask = new SecretPoint(notifAddressServer.getECKey().getPrivKeyBytes(), inputKey.getPubKey());
        byte[] dataUnmasked = PaymentCode.xorMask(dataMasked, secretPointUnmask, inputOutPoint);

        // verify
        Assertions.assertArrayEquals(data, dataUnmasked);
    }

}
