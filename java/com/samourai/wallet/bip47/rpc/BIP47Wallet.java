package com.samourai.wallet.bip47.rpc;

import com.samourai.wallet.hd.HD_Wallet;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.util.List;

/**
 *
 * BIP47Wallet.java : BIP47 wallet
 *
 */
public class BIP47Wallet extends HD_Wallet {

    private BIP47Account mAccount = null;

    /**
     * Constructor for wallet.
     *
     * @param int purpose
     * @param MnemonicCode mc mnemonic code object
     * @param NetworkParameters params
     * @param byte[] seed seed for this wallet
     * @param String passphrase optional BIP39 passphrase
     * @param int nbAccounts number of accounts to create
     *
     */
    public BIP47Wallet(int purpose, MnemonicCode mc, NetworkParameters params, byte[] seed, String passphrase, int nbAccounts) throws MnemonicException.MnemonicLengthException {

        super(purpose, mc, params, seed, passphrase, nbAccounts);

        mAccount = new BIP47Account(params, mRoot, 0);

    }

    /**
     * Constructor for wallet.
     *
     * @param int purpose
     * @param HD_Wallet hdWallet to copy from
     * @param int nbAccounts
     *
     */
    public BIP47Wallet(int purpose, HD_Wallet hdWallet, int nbAccounts) {

        super(purpose, hdWallet, nbAccounts);

        mAccount = new BIP47Account(mParams, mRoot, 0);

    }

    /**
     * Return account for submitted account id.
     *
     * @param int accountId
     *
     * @return Account
     *
     */
    public BIP47Account getAccount(int accountId) {
        return mAccount;
    }

}
