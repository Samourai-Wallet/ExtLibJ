package com.samourai.wallet.api.pairing;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class PairingPayload {
  private static final Logger log = LoggerFactory.getLogger(PairingPayload.class);

  private PairingValue pairing;
  private PairingDojo dojo; // may be null

  public PairingPayload() {
      this.pairing = new PairingValue();
      this.dojo = null;
  }

  public PairingPayload(PairingType type, PairingVersion version, PairingNetwork network, String mnemonic, Boolean passphrase, PairingDojo dojo) {
    this.pairing = new PairingValue(type, version, network, mnemonic, passphrase);
    this.dojo = dojo;
  }

  protected void validate() throws Exception {
    if (pairing == null) {
      throw new Exception("Invalid pairing");
    }
    pairing.validate();
    if (dojo != null) {
        dojo.validate();
    }
  }

  public PairingValue getPairing() {
    return pairing;
  }

  public void setPairing(PairingValue pairing) {
    this.pairing = pairing;
  }

  public PairingDojo getDojo() {
    return dojo;
  }

  public void setDojo(PairingDojo dojo) {
    this.dojo = dojo;
  }

  public static class PairingValue {
    private PairingType type;
    private PairingVersion version;
    private PairingNetwork network;
    private String mnemonic;
    private Boolean passphrase; // NULL for V1

    public PairingValue() {

    }

    public PairingValue(PairingType type, PairingVersion version, PairingNetwork network, String mnemonic, Boolean passphrase) {
      this.type = type;
      this.version = version;
      this.network = network;
      this.mnemonic = mnemonic;
      this.passphrase = passphrase;
    }

    protected void validate() throws Exception {
      if (type == null) {
        throw new Exception("Invalid pairing.type");
      }

      if (version == null) {
        throw new Exception("Invalid pairing.version");
      }

      if (network == null) {
        throw new Exception("Invalid pairing.network");
      }

      if (StringUtils.isEmpty(mnemonic)) {
        throw new Exception("Invalid pairing.mnemonic");
      }
    }

    public PairingType getType() {
      return type;
    }

    public void setType(PairingType type) {
      this.type = type;
    }

    public PairingVersion getVersion() {
      return version;
    }

    public void setVersion(PairingVersion version) {
      this.version = version;
    }

    public PairingNetwork getNetwork() {
      return network;
    }

    public void setNetwork(PairingNetwork network) {
      this.network = network;
    }

    public String getMnemonic() {
      return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
      this.mnemonic = mnemonic;
    }

    public Boolean getPassphrase() {
      return passphrase;
    }

    public void setPassphrase(Boolean passphrase) {
      this.passphrase = passphrase;
    }
  }

  public static class PairingDojo {
      private String url; // may be null
      private String apikey; // may be null

      public PairingDojo() {
      }

      public PairingDojo(String url, String apikey) {
          this.url = url;
          this.apikey = apikey;
      }

      protected void validate() throws Exception {
          // url
          if (StringUtils.isEmpty(url)) {
              throw new Exception("Invalid pairing.url");
          }
          try {
              new URL(url);
          } catch(Exception e) {
              log.error("", e);
              throw new Exception("Invalid pairing.url");
          }

          // apikey
          if (StringUtils.isEmpty(apikey)) {
              throw new Exception("Invalid pairing.apikey");
          }
      }

      public String getUrl() {
          return url;
      }

      public void setUrl(String url) {
          this.url = url;
      }

      public String getApikey() {
          return apikey;
      }

      public void setApikey(String apikey) {
          this.apikey = apikey;
      }
  }
}
