package com.samourai.wallet.api.pairing;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PairingPayloadTest {
    private static ObjectMapper objectMapper;

    public PairingPayloadTest() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    public void test() throws Exception {
        String payload = "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"2.0.0\",\"network\":\"testnet\",\"mnemonic\":\"P4ks9PBEaiMy7EIrT9ktP7sywY96nGxc0c+E4d5\\/vBrZx6bOpsSqyGkEgqKxUgwLKBeZkF+MiYNAuOLPtPG\\/beYwSEma98V5qZL7F\\/dZIxPaAHmsOAbN0gc55sbSErZ+\",\"passphrase\":true}}";
        PairingPayload pairingPayload = parse(payload);
        pairingPayload.validate();
        Assertions.assertNull(pairingPayload.getDojo());
    }

    @Test
    public void testDojo() throws Exception {
        String payload = "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"2.0.0\",\"network\":\"testnet\",\"mnemonic\":\"P4ks9PBEaiMy7EIrT9ktP7sywY96nGxc0c+E4d5\\/vBrZx6bOpsSqyGkEgqKxUgwLKBeZkF+MiYNAuOLPtPG\\/beYwSEma98V5qZL7F\\/dZIxPaAHmsOAbN0gc55sbSErZ+\",\"passphrase\":true},\"dojo\":{\"apikey\":\"foo\",\"url\":\"http:\\/\\/foo.onion\\/test\\/v2\\/\"}}";
        PairingPayload pairingPayload = parse(payload);
        pairingPayload.validate();
        Assertions.assertEquals("foo", pairingPayload.getDojo().getApikey());
        Assertions.assertEquals("http://foo.onion/test/v2/", pairingPayload.getDojo().getUrl());
    }

    @Test
    public void parse_valid() throws Exception {
        String payload;

        // valid
        payload =
                "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"1.0.0\",\"network\":\"testnet\",\"mnemonic\":\"foo\"}}";
        parse(
                payload,
                PairingVersion.V1_0_0,
                PairingNetwork.TESTNET,
                "foo",
                null);

        // valid
        payload =
                "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"1.0.0\",\"network\":\"testnet\",\"mnemonic\":\"foo\"}}";
        parse(
                payload,
                PairingVersion.V1_0_0,
                PairingNetwork.TESTNET,
                "foo",
                null);

        // valid
        payload =
                "{\"pairing\": {\"type\": \"whirlpool.gui\",\"version\": \"1.0.0\",\"network\": \"mainnet\",\"mnemonic\": \"foo\"}}";
        parse(
                payload,
                PairingVersion.V1_0_0,
                PairingNetwork.MAINNET,
                "foo",
                null);

        // valid V2
        payload =
                "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"2.0.0\",\"network\":\"testnet\",\"mnemonic\":\"foo\",\"passphrase\":true}}";
        parse(
                payload,
                PairingVersion.V2_0_0,
                PairingNetwork.TESTNET,
                "foo",
                true);

        // valid V2 no passphrase
        payload =
                "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"2.0.0\",\"network\":\"testnet\",\"mnemonic\":\"foo\",\"passphrase\":false}}";
        parse(
                payload,
                PairingVersion.V2_0_0,
                PairingNetwork.TESTNET,
                "foo",
                false);
    }

    @Test
    public void parse_invalid() throws Exception {
        // missing 'pairing'
        try {
            String payload =
                    "{\"wrong\":{\"type\":\"whirlpool.gui\",\"version\":\"1.0.0\",\"network\":\"testnet\",\"mnemonic\":\"foo\"}}";
            parse(payload);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            // ok
        }

        // invalid type
        try {
            String payload =
                    "{\"pairing\":{\"type\":\"foo\",\"version\":\"1.0.0\",\"network\":\"testnet\",\"mnemonic\":\"foo\"}}";
            parse(payload, PairingVersion.V1_0_0, PairingNetwork.TESTNET, "foo", null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            // ok
        }

        // invalid version
        try {
            String payload =
                    "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"0.0.0\",\"network\":\"testnet\",\"mnemonic\":\"foo\"}}";
            parse(payload, PairingVersion.V1_0_0, PairingNetwork.TESTNET, "foo", null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            // ok
        }

        // invalid network
        try {
            String payload =
                    "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"1.0.0\",\"network\":\"wrong\",\"mnemonic\":\"foo\"}}";
            parse(payload, PairingVersion.V1_0_0, PairingNetwork.TESTNET, "foo", null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            // ok
        }

        // invalid mnemonic
        try {
            String payload =
                    "{\"pairing\":{\"type\":\"whirlpool.gui\",\"version\":\"1.0.0\",\"network\":\"testnet\",\"mnemonic\":\"\"}}";
            parse(payload, PairingVersion.V1_0_0, PairingNetwork.TESTNET, "foo", null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            // ok
        }
    }

    private void parse(
            String payload,
            PairingVersion pairingVersion,
            PairingNetwork pairingNetwork,
            String mnemonic,
            Boolean passphrase)
            throws Exception {
        PairingPayload pairingPayload = parse(payload);
        Assertions.assertEquals(pairingNetwork, pairingPayload.getPairing().getNetwork());
        Assertions.assertEquals(pairingVersion, pairingPayload.getPairing().getVersion());
        Assertions.assertEquals(mnemonic, pairingPayload.getPairing().getMnemonic());
        Assertions.assertEquals(passphrase, pairingPayload.getPairing().getPassphrase());
    }

    private PairingPayload parse(String json) throws Exception {
        PairingPayload pairingPayload = objectMapper.readValue(json, PairingPayload.class);
        pairingPayload.validate();
        return pairingPayload;
    }
}
