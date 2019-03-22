package com.samourai.wallet.crypto;

import com.samourai.wallet.util.CharSequenceX;
import com.samourai.wallet.util.FeeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AESUtilTest {

  @Test
  public void encryptDecrypt() throws Exception {
    doEncryptDecrypt("all all all all all all all all all all all all", "secret");
  }

  @Test
  public void decrypt() throws Exception {
    String encrypted = "t6MNj4oCb9T54lKWNAF274Hg72E0q0uJooUwKjzGD+ysWsFv8Ib47ubdnjStkeJ/G9UltiERHAm1tKRtHbaJiA==";
    CharSequenceX passwordx = new CharSequenceX("secret");
    String decrypted = AESUtil.decrypt(encrypted, passwordx);
    Assertions.assertEquals("all all all all all all all all all all all all", decrypted);
  }

  private void doEncryptDecrypt(String cleartext, String password) throws Exception {
    // valid for password
    CharSequenceX passwordx = new CharSequenceX(password);
    String encrypted = AESUtil.encrypt(cleartext, passwordx);
    String decrypted = AESUtil.decrypt(encrypted, passwordx);

    System.out.println("encrypted: :" +encrypted+":");
    System.out.println("decrypted: " +decrypted);
    Assertions.assertEquals(cleartext, decrypted);
    Assertions.assertNotEquals(cleartext, encrypted);

    // invalid for wrong password
    String encryptedWrong = AESUtil.encrypt(cleartext, passwordx);
    try {
      String decryptedWrong = AESUtil.decrypt(encryptedWrong, new CharSequenceX("wrong"));
      Assertions.assertNotEquals(cleartext, decryptedWrong);
    } catch(Exception e) {
      // ok
    }
  }
}
