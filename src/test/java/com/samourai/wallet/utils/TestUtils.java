package com.samourai.wallet.utils;

import com.samourai.wallet.bip47.rpc.BIP47Wallet;
import com.samourai.wallet.hd.HD_Wallet;
import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.segwit.bech32.Bech32UtilGeneric;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.KeyChainGroup;

public class TestUtils {
    private static final SecureRandom random = new SecureRandom();
    public static final String BIP39_ENGLISH_SHA256 = "ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db";

    public static HD_Wallet generateWallet(int purpose, byte[] seed, String passphrase, NetworkParameters networkParameters) throws Exception {
        InputStream wis = HD_Wallet.class.getResourceAsStream("/BIP39/en.txt");
        if (wis != null) {
            MnemonicCode mc = new MnemonicCode(wis, BIP39_ENGLISH_SHA256);

            // init BIP44 wallet for input
            HD_Wallet inputWallet = new HD_Wallet(purpose, mc, networkParameters, seed, passphrase, 1);

            wis.close();
            return inputWallet;
        }
        throw new Exception("wis is null");
    }

    public static HD_Wallet generateWallet(int purpose, NetworkParameters networkParameters) throws Exception {
        int nbWords = 12;
        // len == 16 (12 words), len == 24 (18 words), len == 32 (24 words)
        int len = (nbWords / 3) * 4;

        byte seed[] = new byte[len];
        random.nextBytes(seed);

        return generateWallet(purpose, seed, "test", networkParameters);
    }

    public static BIP47Wallet generateBip47Wallet(NetworkParameters networkParameters) throws Exception {
        HD_Wallet bip44Wallet = generateWallet(44, networkParameters);
        BIP47Wallet bip47Wallet = new BIP47Wallet(47, bip44Wallet, 1);
        return bip47Wallet;
    }

    public static SegwitAddress generateSegwitAddress(NetworkParameters params) {
        KeyChainGroup kcg = new KeyChainGroup(params);
        DeterministicKey utxoKey = kcg.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        SegwitAddress segwitAddress = new SegwitAddress(utxoKey, params);
        return segwitAddress;
    }

    public static TransactionOutPoint generateTransactionOutPoint(String toAddress, long amount, NetworkParameters params) throws Exception {
        // generate transaction with bitcoinj
        Transaction transaction = new Transaction(params);

        // add output
        TransactionOutput transactionOutput =
            Bech32UtilGeneric.getInstance().getTransactionOutput(toAddress, amount, params);
        transaction.addOutput(transactionOutput);

        // add coinbase input
        int txCounter = 1;
        TransactionInput transactionInput =
            new TransactionInput(
                params, transaction, new byte[] {(byte) txCounter, (byte) (txCounter++ >> 8)});
        transaction.addInput(transactionInput);

        return transactionOutput.getOutPointFor();
    }

}
