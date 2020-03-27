package com.samourai.wallet.psbt;

import com.samourai.wallet.segwit.SegwitAddress;
import com.samourai.wallet.psbt.PSBT;
import com.samourai.wallet.psbt.PSBTEntry;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.bouncycastle.util.encoders.Hex;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PSBTTest {

    private static String strPSBT = "70736274 FF01009A 02000000 0232A139 2B4777AA AE109030 4246D564 E99D68EB E3D35F12 A77FBAE7 281D0A93 E1010000 0000FDFF FFFF67D3 8F936062 1FF49887 38FEB31D DEE2E618 2CFCDD4A B68721F4 D49CADBA 82EE0100 000000FD FFFFFF02 C222480A 00000000 16001498 79AF465E D7CF0054 276217D2 E9130767 F6369100 4B26A039 00000016 00149032 58C94687 540B468C 3763F348 941FAB40 7AF9A777 19000001 011FC049 6E0A0000 00001600 1407AF7C FEC74560 0DA9BFFE BC6A1DB2 9D42EA9A CE220602 F6017FE8 A221D141 CE5FE7FB D3636F59 45434246 B5A02CC4 CFC4B58B B3B2C3FC 180D8C85 AB540000 80010000 80000000 80000000 00080000 00000101 1FD32400 A0390000 00160014 C583F828 737798EE 6AB85B1D 919339BE BEC95CD0 22060215 5F483277 2ADB0D18 CFFBE94A 8A97BCB1 D952C68A B1992E94 808DA7AE 1A8CB018 0D8C85AB 54000080 01000080 00000080 01000000 04000000 00220202 F45169DB 34C54A85 F36BB454 BF0782D6 D0B0E8EC 6B8E2C4B 973AB6D5 8553A202 180D8C85 AB540000 80010000 80000000 80010000 00050000 000000";

    @Test
    public void testParse() {

        try {

            PSBT psbtIn = new PSBT(strPSBT.replaceAll(" ", ""), TestNet3Params.get());
            psbtIn.setDebug(true);
            psbtIn.read();

            Assertions.assertTrue(psbtIn.isParseOK());
        }
        catch(Exception e) {
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testParseAndInput() {

        try {

            PSBT psbtIn = new PSBT(strPSBT.replaceAll(" ", ""), TestNet3Params.get());
            psbtIn.read();

            Transaction tx = psbtIn.getTransaction();

            PSBT psbtOut = new PSBT(tx);
            psbtOut.setPsbtInputs(psbtIn.getPsbtInputs());
            psbtOut.setPsbtOutputs(psbtIn.getPsbtOutputs());
            byte[] psbtOutBuf = psbtOut.serialize();

            Assertions.assertTrue(org.bouncycastle.util.encoders.Hex.toHexString(psbtOutBuf).toLowerCase().equals(strPSBT.replaceAll(" ", "").toLowerCase()));

        }
        catch(Exception e) {
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testInputsAndOutputsCounts() {

        try {

            PSBT psbtIn = new PSBT(strPSBT.replaceAll(" ", ""), TestNet3Params.get());
            psbtIn.read();

            Assertions.assertTrue(psbtIn.getTransaction().getInputs().size() == 2);
            Assertions.assertTrue(psbtIn.getTransaction().getOutputs().size() == 2);

            Assertions.assertTrue(psbtIn.getInputCount() == 2);
            Assertions.assertTrue(psbtIn.getOutputCount() == 2);

        }
        catch(Exception e) {
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testInputs() {

        try {

            PSBT psbtIn = new PSBT(strPSBT.replaceAll(" ", ""), TestNet3Params.get());
            psbtIn.read();

            PSBTEntry testEntry = null;
            for(PSBTEntry entry : psbtIn.getPsbtInputs()) {

                if(entry.getKey() == null) {
                    continue;
                }

                if(org.bouncycastle.util.encoders.Hex.toHexString(entry.getKeyType()).equals("01")) {
                    byte[] data = entry.getData();
                    byte[] amount = new byte[8];
                    byte[] scriptpubkey = new byte[data.length - 8];
                    System.arraycopy(data, 0, amount, 0, 8);
                    System.arraycopy(data, 8, scriptpubkey, 0, data.length - 8);
                    ByteBuffer bb = ByteBuffer.wrap(amount);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    Assertions.assertTrue(175000000L == bb.getLong());
                    Assertions.assertTrue("16001407af7cfec745600da9bffebc6a1db29d42ea9ace".equalsIgnoreCase(org.bouncycastle.util.encoders.Hex.toHexString(scriptpubkey)));
                }
                else if(org.bouncycastle.util.encoders.Hex.toHexString(entry.getKeyType()).equals("06")) {
                    byte[] keydata = entry.getKeyData();
                    Assertions.assertTrue("tb1qq7hhelk8g4sqm2dll67x58djn4pw4xkwx040qg".equals(new SegwitAddress(keydata, TestNet3Params.get()).getBech32AsString()));
                    break;
                }

            }

        }
        catch(Exception e) {
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testOutputs() {

        try {

            PSBT psbtIn = new PSBT(strPSBT.replaceAll(" ", ""), TestNet3Params.get());
            psbtIn.read();

            PSBTEntry testEntry = null;
            for(PSBTEntry entry : psbtIn.getPsbtOutputs()) {

                if(entry.getKey() == null) {
                    continue;
                }

                if(org.bouncycastle.util.encoders.Hex.toHexString(entry.getKeyType()).equals("02")) {
                    testEntry = entry;
                    break;
                }

            }

            if(testEntry != null) {
                PSBTEntry entry = testEntry;

                byte[] data = entry.getData();
                byte[] fp = new byte[4];
                System.arraycopy(data, 0, fp, 0, 4);
                // fingerprint
                Assertions.assertTrue("0d8c85ab".equalsIgnoreCase(Hex.toHexString(fp)));
                Assertions.assertTrue(data.length == 24);
                int nb = data.length / 4;
                for(int i = 1; i < nb; i++ ) {
                    byte[] segment = new byte[4];
                    System.arraycopy(data, i * 4, segment, 0, 4);
                    switch(i) {
                        case 1:
                            Assertions.assertTrue("54000080".equals(org.bouncycastle.util.encoders.Hex.toHexString(segment)));
                            break;
                        case 2:
                            Assertions.assertTrue("01000080".equals(org.bouncycastle.util.encoders.Hex.toHexString(segment)));
                            break;
                        case 3:
                            Assertions.assertTrue("00000080".equals(org.bouncycastle.util.encoders.Hex.toHexString(segment)));
                            break;
                        case 4:
                            Assertions.assertTrue("01000000".equals(org.bouncycastle.util.encoders.Hex.toHexString(segment)));
                            break;
                        case 5:
                            Assertions.assertTrue("05000000".equals(org.bouncycastle.util.encoders.Hex.toHexString(segment)));
                            break;
                         default:
                             Assertions.assertTrue(false);
                             break;
                    }
                }
                byte[] keydata = entry.getKeyData();
                Assertions.assertTrue("tb1qnpu673j76l8sq4p8vgta96gnqanlvd53vhsk5j".equals(new SegwitAddress(keydata, TestNet3Params.get()).getBech32AsString()));
            }
            else {
                Assertions.assertTrue(false);
            }

        }
        catch(Exception e) {
            Assertions.assertTrue(false);
        }

    }

}
