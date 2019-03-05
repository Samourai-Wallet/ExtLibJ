package com.samourai.wallet.psbt;

import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.samourai.wallet.util.FormatsUtilGeneric;
import com.samourai.wallet.util.Z85;

import static java.lang.System.exit;

//
// Partially Signed Bitcoin Transaction Format
//
public class PSBT {

   public static final int ENCODING_HEX = 0;
   public static final int ENCODING_BASE64 = 1;
   public static final int ENCODING_Z85 = 2;

   public static final byte PSBT_GLOBAL_UNSIGNED_TX = 0x00;
   //
   public static final byte PSBT_GLOBAL_REDEEM_SCRIPT = 0x01;
   public static final byte PSBT_GLOBAL_WITNESS_SCRIPT = 0x02;
   public static final byte PSBT_GLOBAL_BIP32_PUBKEY = 0x03;
   public static final byte PSBT_GLOBAL_NB_INPUTS = 0x04;

   public static final byte PSBT_IN_NON_WITNESS_UTXO = 0x00;
   public static final byte PSBT_IN_WITNESS_UTXO = 0x01;
   public static final byte PSBT_IN_PARTIAL_SIG = 0x02;
   public static final byte PSBT_IN_SIGHASH_TYPE = 0x03;
   public static final byte PSBT_IN_REDEEM_SCRIPT = 0x04;
   public static final byte PSBT_IN_WITNESS_SCRIPT = 0x05;
   public static final byte PSBT_IN_BIP32_DERIVATION = 0x06;
   public static final byte PSBT_IN_FINAL_SCRIPTSIG = 0x07;
   public static final byte PSBT_IN_FINAL_SCRIPTWITNESS = 0x08;

   public static final byte PSBT_OUT_REDEEM_SCRIPT = 0x00;
   public static final byte PSBT_OUT_WITNESS_SCRIPT = 0x01;
   public static final byte PSBT_OUT_BIP32_DERIVATION = 0x02;

   public static final String PSBT_MAGIC = "70736274";

   private static final int STATE_START = 0;
   private static final int STATE_GLOBALS = 1;
   private static final int STATE_INPUTS = 2;
   private static final int STATE_OUTPUTS = 3;
   private static final int STATE_END = 4;

   private static final int HARDENED = 0x80000000;

   private int currentState = 0;
   private int inputs = 0;
   private int outputs = 0;
   private boolean parseOK = false;

   private String strPSBT = null;
   private byte[] psbtBytes = null;
   private ByteBuffer psbtByteBuffer = null;
   private Transaction transaction = null;
   private List<PSBTEntry> psbtInputs = null;
   private List<PSBTEntry> psbtOutputs = null;

   public PSBT(String strPSBT, NetworkParameters params)   {

       if(!FormatsUtilGeneric.getInstance().isPSBT(strPSBT))    {
           return;
       }

       psbtInputs = new ArrayList<PSBTEntry>();
       psbtOutputs = new ArrayList<PSBTEntry>();

       if(FormatsUtilGeneric.getInstance().isBase64(strPSBT) && !FormatsUtilGeneric.getInstance().isHex(strPSBT))    {
           this.strPSBT = Hex.toHexString(Base64.decode(strPSBT));
       }
       else if(Z85.getInstance().isZ85(strPSBT) && !FormatsUtilGeneric.getInstance().isHex(strPSBT))   {
           this.strPSBT = Hex.toHexString(Z85.getInstance().decode(strPSBT));
       }
       else    {
           this.strPSBT = strPSBT;
       }

       psbtBytes = Hex.decode(this.strPSBT);
       psbtByteBuffer = ByteBuffer.wrap(psbtBytes);

       this.transaction = new Transaction(params);
   }

   public PSBT(byte[] psbt, NetworkParameters params)   {

       String strPSBT = Hex.toHexString(psbt);

       if(!FormatsUtilGeneric.getInstance().isPSBT(strPSBT))    {
           return;
       }

       psbtInputs = new ArrayList<PSBTEntry>();
       psbtOutputs = new ArrayList<PSBTEntry>();

       if(FormatsUtilGeneric.getInstance().isBase64(strPSBT) && !FormatsUtilGeneric.getInstance().isHex(strPSBT))    {
           this.strPSBT = Hex.toHexString(Base64.decode(strPSBT));
       }
       else if(Z85.getInstance().isZ85(strPSBT) && !FormatsUtilGeneric.getInstance().isHex(strPSBT))   {
           this.strPSBT = Hex.toHexString(Z85.getInstance().decode(strPSBT));
       }
       else    {
           this.strPSBT = strPSBT;
       }

       psbtBytes = Hex.decode(this.strPSBT);
       psbtByteBuffer = ByteBuffer.wrap(psbtBytes);

       this.transaction = new Transaction(params);
   }

   public PSBT()   {
       psbtInputs = new ArrayList<PSBTEntry>();
       psbtOutputs = new ArrayList<PSBTEntry>();
       this.transaction = new Transaction(MainNetParams.get());
   }

   public PSBT(Transaction transaction)   {
       psbtInputs = new ArrayList<PSBTEntry>();
       psbtOutputs = new ArrayList<PSBTEntry>();
       this.transaction = transaction;
   }

   public PSBT(NetworkParameters params)   {
       psbtInputs = new ArrayList<PSBTEntry>();
       psbtOutputs = new ArrayList<PSBTEntry>();
       transaction = new Transaction(params);
   }

   public PSBT(NetworkParameters params, int version)   {
       psbtInputs = new ArrayList<PSBTEntry>();
       psbtOutputs = new ArrayList<PSBTEntry>();
       transaction = new Transaction(params);
       transaction.setVersion(version);
   }

   //
   // reader
   //
   public void read() throws Exception    {

       int seenInputs = 0;
       int seenOutputs = 0;

       psbtBytes = Hex.decode(strPSBT);
       psbtByteBuffer = ByteBuffer.wrap(psbtBytes);

       PSBT.Log("--- ***** START ***** ---", true);
       PSBT.Log("---  PSBT length:" + psbtBytes.length + "---", true);
       PSBT.Log("--- parsing header ---", true);

       byte[] magicBuf = new byte[4];
       psbtByteBuffer.get(magicBuf);
       if(!PSBT.PSBT_MAGIC.equalsIgnoreCase(Hex.toHexString(magicBuf)))    {
           throw new Exception("Invalid magic value");
       }

       byte sep = psbtByteBuffer.get();
       if(sep != (byte)0xff)    {
           throw new Exception("Bad 0xff separator:" + Hex.toHexString(new byte[] { sep }));
       }

       currentState = STATE_GLOBALS;

       while(psbtByteBuffer.hasRemaining()) {

           if(currentState == STATE_GLOBALS)    {
               PSBT.Log("--- parsing globals ---", true);
           }
           else if(currentState == STATE_INPUTS)   {
               PSBT.Log("--- parsing inputs ---", true);
           }
           else if(currentState == STATE_OUTPUTS)   {
               PSBT.Log("--- parsing outputs ---", true);
           }
           else    {
               ;
           }

           PSBTEntry entry = parse();
           if(entry == null)    {
               PSBT.Log("parse returned null entry", true);
               exit(0);
           }
           entry.setState(currentState);

           if(entry.getKey() == null)    {         // length == 0
               switch (currentState)   {
                   case STATE_GLOBALS:
                       currentState = STATE_INPUTS;
                       break;
                   case STATE_INPUTS:
                       seenInputs++;
                       if(seenInputs == inputs)    {
                           currentState = STATE_OUTPUTS;
                       }
                       break;
                   case STATE_OUTPUTS:
                       seenOutputs++;
                       if(seenOutputs == outputs)    {
                           currentState = STATE_END;
                       }
                       break;
                   case STATE_END:
                       parseOK = true;
                       break;
                   default:
                       PSBT.Log("unknown state", true);
                       break;
               }
           }
           else if(currentState == STATE_GLOBALS)    {
               switch(entry.getKeyType()[0])    {
                   case PSBT.PSBT_GLOBAL_UNSIGNED_TX:
                       PSBT.Log("transaction", true);
                       transaction = new Transaction(getNetParams(), entry.getData());
                       inputs = transaction.getInputs().size();
                       outputs = transaction.getOutputs().size();
                       PSBT.Log(transaction.toString(), true);
                       break;
                   default:
                       PSBT.Log("not recognized key type:" + entry.getKeyType()[0], true);
                       break;
               }
           }
           else if(currentState == STATE_INPUTS)    {
               if(entry.getKeyType()[0] >= PSBT_IN_NON_WITNESS_UTXO && entry.getKeyType()[0] <= PSBT_IN_FINAL_SCRIPTWITNESS)    {
                   psbtInputs.add(entry);
               }
               else    {
                   PSBT.Log("not recognized key type:" + entry.getKeyType()[0], true);
               }
           }
           else if(currentState == STATE_OUTPUTS)    {
               if(entry.getKeyType()[0] >= PSBT_OUT_REDEEM_SCRIPT && entry.getKeyType()[0] <= PSBT_OUT_BIP32_DERIVATION)    {
                   psbtInputs.add(entry);
               }
               else    {
                   PSBT.Log("not recognized key type:" + entry.getKeyType()[0], true);
               }
           }
           else    {
               PSBT.Log("panic", true);
           }

       }

       if(currentState == STATE_END)   {
           PSBT.Log("--- ***** END ***** ---", true);
       }

       PSBT.Log("", true);

   }

   private PSBTEntry parse()    {

       PSBTEntry entry = new PSBTEntry();

       try {
           int keyLen = PSBT.readCompactInt(psbtByteBuffer);
           PSBT.Log("key length:" + keyLen, true);

           if(keyLen == 0x00)    {
               PSBT.Log("separator 0x00", true);
               return entry;
           }

           byte[] key = new byte[keyLen];
           psbtByteBuffer.get(key);
           PSBT.Log("key:" + Hex.toHexString(key), true);

           byte[] keyType = new byte[1];
           keyType[0] = key[0];
           PSBT.Log("key type:" + Hex.toHexString(keyType), true);

           byte[] keyData = null;
           if(key.length > 1)    {
               keyData = new byte[key.length - 1];
               System.arraycopy(key, 1, keyData, 0, keyData.length);
               PSBT.Log("key data:" + Hex.toHexString(keyData), true);
           }

           int dataLen = PSBT.readCompactInt(psbtByteBuffer);
           PSBT.Log("data length:" + dataLen, true);

           byte[] data = new byte[dataLen];
           psbtByteBuffer.get(data);
           PSBT.Log("data:" + Hex.toHexString(data), true);

           entry.setKey(key);
           entry.setKeyType(keyType);
           entry.setKeyData(keyData);
           entry.setData(data);

           return entry;

       }
       catch(Exception e) {
           PSBT.Log("Exception:" + e.getMessage(), true);
           e.printStackTrace();
           return null;
       }

   }

   //
   // writer
   //
   public void addInput(byte type, byte[] keydata, byte[] data) throws Exception {
       psbtInputs.add(populateEntry(type, keydata, data));
   }

   public void addOutput(byte type, byte[] keydata, byte[] data) throws Exception {
       psbtOutputs.add(populateEntry(type, keydata, data));
   }

   private PSBTEntry populateEntry(byte type, byte[] keydata, byte[] data) throws Exception {

       PSBTEntry entry = new PSBTEntry();
       entry.setKeyType(new byte[] { type });
       entry.setKey(new byte[] { type });
       if(keydata != null)    {
           entry.setKeyData(keydata);
       }
       entry.setData(data);

       return entry;
   }

   public byte[] serialize() throws IOException {

       byte[] serialized = transaction.bitcoinSerialize();
       byte[] txLen = PSBT.writeCompactInt(serialized.length);

       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       // magic
       baos.write(Hex.decode(PSBT.PSBT_MAGIC), 0, Hex.decode(PSBT.PSBT_MAGIC).length);
       // separator
       baos.write((byte)0xff);

       // globals
       baos.write(writeCompactInt(1L));                                // key length
       baos.write((byte)0x00);                                             // key
       baos.write(txLen, 0, txLen.length);                             // value length
       baos.write(serialized, 0, serialized.length);                   // value
       baos.write((byte)0x00);

       // inputs
       for(PSBTEntry entry : psbtInputs)   {
           int keyLen = 1;
           if(entry.getKeyData() != null)    {
               keyLen += entry.getKeyData().length;
           }
           baos.write(writeCompactInt(keyLen));
           baos.write(entry.getKey());
           if(entry.getKeyData() != null)    {
               baos.write(entry.getKeyData());
           }
           baos.write(writeCompactInt(entry.getData().length));
           baos.write(entry.getData());
       }
       baos.write((byte)0x00);

       // outputs
       for(PSBTEntry entry : psbtOutputs)   {
           int keyLen = 1;
           if(entry.getKeyData() != null)    {
               keyLen += entry.getKeyData().length;
           }
           baos.write(writeCompactInt(keyLen));
           baos.write(entry.getKey());
           if(entry.getKeyData() != null)    {
               baos.write(entry.getKeyData());
           }
           baos.write(writeCompactInt(entry.getData().length));
           baos.write(entry.getData());
       }
       baos.write((byte)0x00);

       // eof
       baos.write((byte)0x00);

       psbtBytes = baos.toByteArray();
       strPSBT = Hex.toHexString(psbtBytes);
       PSBT.Log("psbt:" + strPSBT, true);

       return psbtBytes;
   }

   //
   //
   //
   public void setTransactionVersion(int version) {
       if(transaction != null)    {
           transaction.setVersion(version);
       }
   }

   public NetworkParameters getNetParams() {
       if(transaction != null)    {
           return transaction.getParams();
       }
       else    {
           return MainNetParams.get();
       }
   }

   public List<PSBTEntry> getPsbtInputs() {
       return psbtInputs;
   }

   public void setPsbtInputs(List<PSBTEntry> psbtInputs) {
       this.psbtInputs = psbtInputs;
   }

   public List<PSBTEntry> getPsbtOutputs() {
       return psbtOutputs;
   }

   public void setPsbtOutputs(List<PSBTEntry> psbtOutputs) {
       this.psbtOutputs = psbtOutputs;
   }

   public Transaction getTransaction() {
       return transaction;
   }

   public void setTransaction(Transaction transaction) {
       this.transaction = transaction;
   }

   public void clear()  {
       transaction = new Transaction(getNetParams());
       psbtInputs.clear();
       psbtOutputs.clear();
       strPSBT = null;
       psbtBytes = null;
       psbtByteBuffer.clear();
   }

   //
   // utils
   //
   public String toString()    {
       try {
           return Hex.toHexString(serialize());
       }
       catch(IOException ioe) {
           return null;
       }
   }

   public String toBase64String() throws IOException    {
       return Base64.toBase64String(serialize());
   }

   public String toZ85String() throws IOException    {
       return Z85.getInstance().encode(serialize());
   }

   public static int readCompactInt(ByteBuffer psbtByteBuffer) throws Exception  {

       byte b = psbtByteBuffer.get();

       switch(b)    {
           case (byte)0xfd: {
               byte[] buf = new byte[2];
               psbtByteBuffer.get(buf);
               ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
               byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
               return byteBuffer.getShort();
           }
           case (byte)0xfe: {
               byte[] buf = new byte[4];
               psbtByteBuffer.get(buf);
               ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
               byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
               return byteBuffer.getInt();
           }
           case (byte)0xff: {
               byte[] buf = new byte[8];
               psbtByteBuffer.get(buf);
               ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
               byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
               throw new Exception("Data too long:" + byteBuffer.getLong());
           }
           default:
//                PSBT.Log("compact int value:" + "value:" + Hex.toHexString(new byte[] { b }), true);
               return (int)(b & 0xff);
       }

   }

   public static byte[] writeCompactInt(long val)   {

       ByteBuffer bb = null;

       if(val < 0xfdL)    {
           bb = ByteBuffer.allocate(1);
           bb.order(ByteOrder.LITTLE_ENDIAN);
           bb.put((byte)val);
       }
       else if(val < 0xffffL)   {
           bb = ByteBuffer.allocate(3);
           bb.order(ByteOrder.LITTLE_ENDIAN);
           bb.put((byte)0xfd);
           bb.put((byte)(val & 0xff));
           bb.put((byte)((val >> 8) & 0xff));
       }
       else if(val < 0xffffffffL)   {
           bb = ByteBuffer.allocate(5);
           bb.order(ByteOrder.LITTLE_ENDIAN);
           bb.put((byte)0xfe);
           bb.putInt((int)val);
       }
       else    {
           bb = ByteBuffer.allocate(9);
           bb.order(ByteOrder.LITTLE_ENDIAN);
           bb.put((byte)0xff);
           bb.putLong(val);
       }

       return bb.array();
   }

   public static Pair<Long, Byte[]> readSegwitInputUTXO(byte[] utxo)    {
       byte[] val = new byte[8];
       byte[] scriptPubKey = new byte[utxo.length - val.length];

       System.arraycopy(utxo, 0, val, 0, val.length);
       System.arraycopy(utxo, val.length, scriptPubKey, 0, scriptPubKey.length);

       ArrayUtils.reverse(val);
       long lval = Long.parseLong(Hex.toHexString(val), 16);

       int i = 0;
       Byte[] scriptPubKeyBuf = new Byte[scriptPubKey.length];
       for(byte b : scriptPubKey)   {
           scriptPubKeyBuf[i++] = b;
       }

       return Pair.of(Long.valueOf(lval), scriptPubKeyBuf);
   }

   public static byte[] writeSegwitInputUTXO(long value, byte[] scriptPubKey)    {

       byte[] ret = new byte[scriptPubKey.length + Long.BYTES];

       // long to byte array
       ByteBuffer xlat = ByteBuffer.allocate(Long.BYTES);
       xlat.order(ByteOrder.LITTLE_ENDIAN);
       xlat.putLong(0, value);
       byte[] val = new byte[Long.BYTES];
       xlat.get(val);

       System.arraycopy(val, 0, ret, 0, Long.BYTES);
       System.arraycopy(scriptPubKey, 0, ret, Long.BYTES, scriptPubKey.length);

       return ret;
   }

   public static String readBIP32Derivation(byte[] path) {

       byte[] dbuf = new byte[path.length];
       System.arraycopy(path, 0, dbuf, 0, path.length);
       ByteBuffer bb = ByteBuffer.wrap(dbuf);
       byte[] buf = new byte[4];

       // fingerprint
       bb.get(buf);
       byte[] fingerprint = new byte[4];
       System.arraycopy(buf, 0, fingerprint, 0, fingerprint.length);

       // purpose
       bb.get(buf);
       ArrayUtils.reverse(buf);
       ByteBuffer pbuf = ByteBuffer.wrap(buf);
       int purpose = pbuf.getInt();
       if(purpose >= HARDENED)    {
           purpose -= HARDENED;
       }

       // coin type
       bb.get(buf);
       ArrayUtils.reverse(buf);
       ByteBuffer tbuf = ByteBuffer.wrap(buf);
       int type = tbuf.getInt();
       if(type >= HARDENED)    {
           type -= HARDENED;
       }

       // account
       bb.get(buf);
       ArrayUtils.reverse(buf);
       ByteBuffer abuf = ByteBuffer.wrap(buf);
       int account = abuf.getInt();
       if(account >= HARDENED)    {
           account -= HARDENED;
       }

       // chain
       bb.get(buf);
       ArrayUtils.reverse(buf);
       ByteBuffer cbuf = ByteBuffer.wrap(buf);
       int chain = cbuf.getInt();

       // index
       bb.get(buf);
       ArrayUtils.reverse(buf);
       ByteBuffer ibuf = ByteBuffer.wrap(buf);
       int index = ibuf.getInt();

//       String ret = "m/" + purpose + "'/" + type + "'/" + account + "'/" + chain + "/" + index;
       // assume hardened values
       String ret = purpose + "/" + type + "/" + account + "/" + chain + "/" + index;

       return ret;
   }

   public static byte[] writeBIP32Derivation(byte[] fingerprint, int purpose, int type, int account, int chain, int index) {

       // fingerprint and integer values to BIP32 derivation buffer
       byte[] bip32buf = new byte[24];

       System.arraycopy(fingerprint, 0, bip32buf, 0, fingerprint.length);

       ByteBuffer xlat = ByteBuffer.allocate(Integer.BYTES);
       xlat.order(ByteOrder.LITTLE_ENDIAN);
       xlat.putInt(0, purpose + HARDENED);
       byte[] out = new byte[Integer.BYTES];
       xlat.get(out);
       System.arraycopy(out, 0, bip32buf, fingerprint.length, out.length);

       xlat.clear();
       xlat.order(ByteOrder.LITTLE_ENDIAN);
       xlat.putInt(0, type + HARDENED);
       xlat.get(out);
       System.arraycopy(out, 0, bip32buf, fingerprint.length + out.length, out.length);

       xlat.clear();
       xlat.order(ByteOrder.LITTLE_ENDIAN);
       xlat.putInt(0, account + HARDENED);
       xlat.get(out);
       System.arraycopy(out, 0, bip32buf, fingerprint.length + (out.length * 2), out.length);

       xlat.clear();
       xlat.order(ByteOrder.LITTLE_ENDIAN);
       xlat.putInt(0, chain);
       xlat.get(out);
       System.arraycopy(out, 0, bip32buf, fingerprint.length + (out.length * 3), out.length);

       xlat.clear();
       xlat.order(ByteOrder.LITTLE_ENDIAN);
       xlat.putInt(0, index);
       xlat.get(out);
       System.arraycopy(out, 0, bip32buf, fingerprint.length + (out.length * 4), out.length);

       return bip32buf;
   }

   public static void Log(String s, boolean eol)  {

       System.out.print(s);
       if(eol)    {
           System.out.print("\n");
       }

   }

}
