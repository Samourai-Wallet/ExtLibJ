package com.samourai.wallet.util;

import com.samourai.wallet.segwit.SegwitAddress;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;

public class TxUtil {
  private static TxUtil instance = null;

  public static TxUtil getInstance() {
    if(instance == null) {
      instance = new TxUtil();
    }
    return instance;
  }

  public void signInputSegwit(Transaction tx, int inputIdx, ECKey ecKey, long spendAmount, NetworkParameters params) {
    final SegwitAddress segwitAddress = new SegwitAddress(ecKey, params);
    final Script redeemScript = segwitAddress.segWitRedeemScript();
    final Script scriptCode = redeemScript.scriptCode();

    TransactionSignature sig =
        tx.calculateWitnessSignature(
            inputIdx, ecKey, scriptCode, Coin.valueOf(spendAmount), Transaction.SigHash.ALL, false);
    final TransactionWitness witness = new TransactionWitness(2);
    witness.setPush(0, sig.encodeToBitcoin());
    witness.setPush(1, ecKey.getPubKey());
    tx.setWitness(inputIdx, witness);
  }

  /*
  public void verifySignInputSegwit(Transaction tx, int inputIdx) {
    final ScriptBuilder sigScript = new ScriptBuilder();
    sigScript.data(redeemScript.getProgram());
    tx.getInput(inputIdx).setScriptSig(sigScript.build());
    tx.getInput(inputIdx).getScriptSig().correctlySpends(tx, inputIdx, scriptPubKey, connectedOutput.getValue(), Script.ALL_VERIFY_FLAGS);
  }*/

  public Integer findInputIndex(Transaction tx, String txoHash, long txoIndex) {
    for (int i = 0; i < tx.getInputs().size(); i++) {
      TransactionInput input = tx.getInput(i);
      TransactionOutPoint outPoint = input.getOutpoint();
      if (outPoint.getHash().toString().equals(txoHash) && outPoint.getIndex() == txoIndex) {
        return i;
      }
    }
    return null;
  }


}
