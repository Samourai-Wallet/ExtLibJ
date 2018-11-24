package com.samourai.wallet.util;

import com.samourai.wallet.segwit.SegwitAddress;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TxUtilTest {
    private static final TxUtil txUtil = TxUtil.getInstance();
    private static final NetworkParameters params = TestNet3Params.get();
    private static final CryptoTestUtil cryptoTestUtil = CryptoTestUtil.getInstance();

    private Transaction computeTxCoinbase(long value, Script outputScript) {
        Transaction tx = new Transaction(params);

        // add output
        tx.addOutput(Coin.valueOf(value), outputScript);

        // add input: coinbase
        int txCounter = 1;
        TransactionInput input =
            new TransactionInput(
                params, tx, new byte[] {(byte) txCounter, (byte) (txCounter++ >> 8)});
        tx.addInput(input);

        tx.verify();
        return tx;
    }

    private Transaction computeTx(ECKey inputKey, Script inputScript, TransactionOutPoint inputOutPoint, TransactionOutput transactionOutput) {
        Transaction tx = new Transaction(params);

        if (transactionOutput == null) {
            // add dummy output
            transactionOutput = new TransactionOutput(params, null, inputOutPoint.getValue(),
                cryptoTestUtil.generateSegwitAddress(params).getAddress());
            tx.addOutput(transactionOutput);
        }

        // add input
        tx.addSignedInput(inputOutPoint, inputScript, inputKey);

        tx.verify();
        return tx;
    }

    private void doFindInputPubkey(TransactionOutput linkedOutput, ECKey inputKey) {
        // spend linkedOutput
        TransactionOutPoint inputOutPoint = linkedOutput.getOutPointFor();
        inputOutPoint.setValue(linkedOutput.getValue());
        Script inputScript = linkedOutput.getScriptPubKey(); // equivaut à outputScript
        Transaction tx = computeTx(inputKey, inputScript, inputOutPoint, null);

        Callback<byte[]> fetchInputOutpointScriptBytes = new Callback<byte[]>() {
            @Override
            public byte[] execute() {
                return linkedOutput.getScriptBytes();
            }
        };

        // TEST
        byte[] pubkey = txUtil.findInputPubkey(tx, 0, fetchInputOutpointScriptBytes);

        // VERIFY
        Assertions.assertArrayEquals(inputKey.getPubKey(), pubkey);
    }

    @Test
    public void findInputPubkeyP2WPKH() throws Exception {
        SegwitAddress inputAddress = cryptoTestUtil.generateSegwitAddress(params);
        ECKey inputKey = inputAddress.getECKey();

        // spend coinbase -> P2WPKH
        Script outputScript = ScriptBuilder.createP2WPKHOutputScript(inputKey);
        Transaction tx = computeTxCoinbase(999999, outputScript);

        // test
        TransactionOutput txOutput = tx.getOutput(0);
        doFindInputPubkey(txOutput, inputKey);
    }

    @Test
    public void findInputPubkeyP2SHP2WPKH() throws Exception {
        SegwitAddress inputAddress = cryptoTestUtil.generateSegwitAddress(params);
        ECKey inputKey = inputAddress.getECKey();
        long value = 999999;

        // spend coinbase -> P2SHP2WPKH
        Script ouputScriptP2WPKH = ScriptBuilder.createP2WPKHOutputScript(inputKey);
        Script outputScript = ScriptBuilder.createP2SHOutputScript(ouputScriptP2WPKH);
        Transaction tx = computeTxCoinbase(value, outputScript);

        // test
        TransactionOutput txOutput = tx.getOutput(0);
        doFindInputPubkey(txOutput, inputKey);
    }

    @Test
    public void findInputPubkeyP2PKH() throws Exception {
        ECKey inputKey = new ECKey();

        // spend coinbase -> P2PKH
        Address inputAddressP2PKH = new Address(params, inputKey.getPubKeyHash());
        Script outputScript = ScriptBuilder.createOutputScript(inputAddressP2PKH);
        Transaction tx = computeTxCoinbase(999999, outputScript);

        // test
        TransactionOutput txOutput = tx.getOutput(0);
        doFindInputPubkey(txOutput, inputKey);
    }

    @Test
    public void findInputPubkeyP2PK() throws Exception {
        ECKey inputKey = new ECKey();

        // spend coinbase -> P2PK
        Script outputScript = ScriptBuilder.createOutputScript(inputKey);
        Transaction tx = computeTxCoinbase(999999, outputScript);

        // test
        TransactionOutput txOutput = tx.getOutput(0);
        doFindInputPubkey(txOutput, inputKey);
    }
}
