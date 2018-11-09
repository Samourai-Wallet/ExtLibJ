package com.samourai.wallet.hd;

import com.samourai.wallet.utils.TestUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

public class HD_WalletTest {

    @Test
    public void testHdWalletTestnet() throws Exception {
        NetworkParameters params = TestNet3Params.get();
        InputStream wis = HD_Wallet.class.getResourceAsStream("/BIP39/en.txt");
        MnemonicCode mc = new MnemonicCode(wis, TestUtils.BIP39_ENGLISH_SHA256);

        HD_Wallet hdWallet1 = new HD_Wallet(44, mc, params, "foo1".getBytes(), "test1", 1);
        HD_Wallet hdWallet2 = new HD_Wallet(44, mc, params, "foo1".getBytes(), "test2", 1);

        HD_Wallet hdWallet3 = new HD_Wallet(44, mc, params, "foo2".getBytes(), "test1", 1);
        HD_Wallet hdWallet4 = new HD_Wallet(44, mc, params, "foo2".getBytes(), "test2", 1);

        HD_Wallet hdWallet1Copy = new HD_Wallet(44, hdWallet1, 1);

        // verify
        Assertions.assertArrayEquals(new String[]{"tpubDC8qnx32oEZVipETHVHJhybpkTrgadwLGQZqd2nz9VPVu63gH4R6BHKB1UB4DYwXNu37Dtw1JmADsZQ75upg4Dy8aBCBCR28mDuC86DKueS"}, hdWallet1.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDC8qnx32oEZVipETHVHJhybpkTrgadwLGQZqd2nz9VPVu63gH4R6BHKB1UB4DYwXNu37Dtw1JmADsZQ75upg4Dy8aBCBCR28mDuC86DKueS"}, hdWallet1Copy.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDCjWUQzx4WG2igBV4MtRbYg8ZkgsSs3LBs4Y1rBmF8MbFeGCcvFFPmqLgbpr1bNy37fnwsfm9SCkgcUYBw4hgVFzLsfwxoCqWkBiWhbU9Ry"}, hdWallet2.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDCCzbVg6SzMF9fYUTuRbqPdcUFu2erTn2iHHgTp1jxu7Ve9zxLSvPVdfxR6nRT6GE5ShqM35eTfKXMhncSHCkrZzTEdgbZ8tFs63ix6S1fC"}, hdWallet3.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"tpubDDK5Q8DUwp9TvNZjf6QBAUuxk2QAgcmpVVpxSHVYc4fDBuCpwuyQnzDDAtimCBLDrQXrX3R8KKoyjWjVABZtfE41FWeYSVKndfKCvmXrhD6"}, hdWallet4.getXPUBs());

        Assertions.assertEquals("n3VABKp2wDB3mLHw6xH6SgVMWzFGU9u169", hdWallet1.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("n3VABKp2wDB3mLHw6xH6SgVMWzFGU9u169", hdWallet1Copy.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("myrDKvdCUNAMEoxWT4r3116i21R93s5vUV", hdWallet2.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mpZYLcbacAdehXp3max1h2Lubk4GRnnpLj", hdWallet3.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mhAaH3UGm6NYeHHvHx3KRGGCddwYdBj3VH", hdWallet4.getAccount(0).getChain(0).getAddressAt(0).getAddressString());

        Assertions.assertEquals("mt6uJ69jhbFGzAgrb6RnEqhwrd7tTKjxF7", hdWallet1.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mt6uJ69jhbFGzAgrb6RnEqhwrd7tTKjxF7", hdWallet1Copy.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mnTHShP3iqCBYnxDnnBswpaHW3gMnVQyYq", hdWallet2.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mq8cBiuaRRPUyue22TMbWerhruXrJmfGZY", hdWallet3.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("mnEntMG6XAqwvH9C1UbHiKgwdWFKAEgHtu", hdWallet4.getAccount(0).getChain(1).getAddressAt(0).getAddressString());

        Assertions.assertEquals("muimRQFJKMJM1pTminJxiD5HrPgSu257tX", hdWallet1.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("muimRQFJKMJM1pTminJxiD5HrPgSu257tX", hdWallet1Copy.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("n2HYGk5jk4YoRQrBeMNkE5RKHegRgVyU9M", hdWallet2.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("mpZbQ8syt9MNRh9duiwVaEsVYwQE5E6r5p", hdWallet3.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("mmiiiy2gW4KFdKXQJC93p3jpjS7FXpi8Lq", hdWallet4.getAccount(0).getChain(1).getAddressAt(1).getAddressString());

    }

    @Test
    public void testHdWalletMainnet() throws Exception {
        NetworkParameters params = MainNetParams.get();
        InputStream wis = HD_Wallet.class.getResourceAsStream("/BIP39/en.txt");
        MnemonicCode mc = new MnemonicCode(wis, TestUtils.BIP39_ENGLISH_SHA256);

        HD_Wallet hdWallet1 = new HD_Wallet(44, mc, params, "foo1".getBytes(), "test1", 1);
        HD_Wallet hdWallet2 = new HD_Wallet(44, mc, params, "foo1".getBytes(), "test2", 1);

        HD_Wallet hdWallet3 = new HD_Wallet(44, mc, params, "foo2".getBytes(), "test1", 1);
        HD_Wallet hdWallet4 = new HD_Wallet(44, mc, params, "foo2".getBytes(), "test2", 1);

        HD_Wallet hdWallet1Copy = new HD_Wallet(44, hdWallet1, 1);

        // verify
        Assertions.assertArrayEquals(new String[]{"xpub6By39V6HgpxbtuBVMpGDWPDFaBpMqEewX1KV45eXUZkvoV5TVgr9dvi5MkxtRrdovbngSAJtHR3mau3a2b9hmnTR9G7zjXozwqDBaHFPT5j"}, hdWallet1.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"xpub6By39V6HgpxbtuBVMpGDWPDFaBpMqEewX1KV45eXUZkvoV5TVgr9dvi5MkxtRrdovbngSAJtHR3mau3a2b9hmnTR9G7zjXozwqDBaHFPT5j"}, hdWallet1Copy.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"xpub6C8aSUjB7fwH6CSpS5AjRh1sPwfmrZKNNrfye5rkijhFpSfiKeSNT2CpVLuDzQiipdYAmmyi4eLXritVhYjfBfeEWJPXUrUEEHrcgnEH7wX"}, hdWallet2.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"xpub6DUQ2PuGdPGVK74fqMpFw7UxQa2wLcv8JcWEV7mjNgiuiv4NjgsxukpDfd6xaeuU87oEGx16k3w1XhCs4mmK8GybS6n9W5hvAvCtyxB9nLV"}, hdWallet3.getXPUBs());
        Assertions.assertArrayEquals(new String[]{"xpub6DQVth98Zm2fQnsjAo7djuzoxYVnXMkio5TMCTdCdMgergYKJMQjzAqGCLfciX5fs7gkAa3xnS8cnoHmQ9sQTqnTppQPULwN778KqdRemf5"}, hdWallet4.getXPUBs());

        Assertions.assertEquals("1C36vErfBHdZPnrB5vMh6fRxnZ3RfRr8eW", hdWallet1.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("1C36vErfBHdZPnrB5vMh6fRxnZ3RfRr8eW", hdWallet1Copy.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("1HtHLCcbiF5QGTceLn75t5ob5UvUGH8VeF", hdWallet2.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("1GwQVkN26sQzBT31SRL44jVCKwzXkguiWb", hdWallet3.getAccount(0).getChain(0).getAddressAt(0).getAddressString());
        Assertions.assertEquals("19PsVTqibwYznPwnippykj9DERnZz2h5Xd", hdWallet4.getAccount(0).getChain(0).getAddressAt(0).getAddressString());

        Assertions.assertEquals("19pAMZjGAy3C4uVREZKK959jhRynUJ6hhD", hdWallet1.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("19pAMZjGAy3C4uVREZKK959jhRynUJ6hhD", hdWallet1Copy.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("13vwGM9oQxgtJfUnVazpecUnXqeF5gmf5o", hdWallet2.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("1DURg2Jm2Pf1128L1ZB135yRyjdfskuS5R", hdWallet3.getAccount(0).getChain(1).getAddressAt(0).getAddressString());
        Assertions.assertEquals("1Nyq7DHGA562DCHhS46WqX6BPDzuDGTjYk", hdWallet4.getAccount(0).getChain(1).getAddressAt(0).getAddressString());

        Assertions.assertEquals("1b1C6KHtjXb5Ln2UFMwxNpuZbuQsmdrGv", hdWallet1.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("1b1C6KHtjXb5Ln2UFMwxNpuZbuQsmdrGv", hdWallet1Copy.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("179vtkLefbwrNKg1U84Jj5m48qEavS55De", hdWallet2.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("1ENJv6fC5aionx1HJwDkLeSh2dkRXtxtnQ", hdWallet3.getAccount(0).getChain(1).getAddressAt(1).getAddressString());
        Assertions.assertEquals("171nJXTUgRNtpMyivBRsHVeE1RZYeR8i2D", hdWallet4.getAccount(0).getChain(1).getAddressAt(1).getAddressString());

    }

}
