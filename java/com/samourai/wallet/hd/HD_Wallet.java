package com.samourai.wallet.hd;

import com.google.common.base.Joiner;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.*;

import java.util.ArrayList;
import java.util.List;

public class HD_Wallet {

    private byte[] mSeed = null;
    private String strPassphrase = null;
    private List<String> mWordList = null;

    protected DeterministicKey mRoot = null; // null when created from xpub

    protected ArrayList<HD_Account> mAccounts = null;

    protected NetworkParameters mParams = null;

    private HD_Wallet() { ; }

    /*
    create from seed+passphrase
     */
    public HD_Wallet(int purpose, MnemonicCode mc, NetworkParameters mParams, byte[] mSeed, String strPassphrase, int nbAccounts) throws MnemonicException.MnemonicLengthException {
        this(purpose, mc.toMnemonic(mSeed), mParams, mSeed, strPassphrase, nbAccounts);
    }

    protected HD_Wallet(int purpose, List<String> wordList, NetworkParameters mParams, byte[] mSeed, String strPassphrase, int nbAccounts) {
        this(mSeed, strPassphrase, wordList, mParams);

        // compute rootKey for accounts
        this.mRoot = computeRootKey(purpose, mWordList, strPassphrase);

        // create accounts
        mAccounts = new ArrayList<HD_Account>();
        for(int i = 0; i < nbAccounts; i++) {
            String acctName = String.format("account %02d", i);
            mAccounts.add(new HD_Account(mParams, mRoot, acctName, i));
        }
    }

    protected HD_Wallet(int purpose, HD_Wallet inputWallet, int nbAccounts) {
        this(purpose, inputWallet.getWordList(), inputWallet.getNetworkParameters(), inputWallet.getSeed(), inputWallet.getPassphrase(), nbAccounts);
    }

    /*
    create from account xpub key(s)
     */
    public HD_Wallet(NetworkParameters params, String[] xpub) throws AddressFormatException {

        mParams = params;
        mAccounts = new ArrayList<HD_Account>();
        for(int i = 0; i < xpub.length; i++) {
            mAccounts.add(new HD_Account(mParams, xpub[i], "", i));
        }

    }

    protected HD_Wallet(byte[] mSeed, String strPassphrase, List<String> mWordList, NetworkParameters mParams) {
        this.mSeed = mSeed;
        this.strPassphrase = strPassphrase;
        this.mWordList = mWordList;
        this.mParams = mParams;
    }

    private static DeterministicKey computeRootKey(int purpose, List<String> mWordList, String strPassphrase) {
        byte[] hd_seed = MnemonicCode.toSeed(mWordList, strPassphrase);
        DeterministicKey mKey = HDKeyDerivation.createMasterPrivateKey(hd_seed);
        DeterministicKey t1 = HDKeyDerivation.deriveChildKey(mKey, purpose|ChildNumber.HARDENED_BIT);
        DeterministicKey rootKey = HDKeyDerivation.deriveChildKey(t1, ChildNumber.HARDENED_BIT);
        return rootKey;
    }

    public String getSeedHex() {
        return org.bouncycastle.util.encoders.Hex.toHexString(mSeed);
    }

    public byte[] getSeed() { return mSeed; }

    public String getMnemonic() {
        return Joiner.on(" ").join(mWordList);
    }

    public String getPassphrase() {
        return strPassphrase;
    }

    public List<HD_Account> getAccounts() {
        return mAccounts;
    }

    public HD_Account getAccount(int accountId) {
        return mAccounts.get(accountId);
    }

    public HD_Account getAccountAt(int accountIdx) {
        return new HD_Account(mParams, mRoot, "", accountIdx);
    }

    public void addAccount() {
        String strName = String.format("Account %d", mAccounts.size());
        mAccounts.add(new HD_Account(mParams, mRoot, strName, mAccounts.size()));
    }

    public String[] getXPUBs() {

        String[] ret = new String[mAccounts.size()];

        for(int i = 0; i < mAccounts.size(); i++) {
            ret[i] = mAccounts.get(i).xpubstr();
        }

        return ret;
    }

    public NetworkParameters getNetworkParameters() {
        return mParams;
    }

    public List<String> getWordList() {
        return mWordList;
    }
}
