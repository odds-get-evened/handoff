package org.white5moke.handoff.document;

import io.leonard.Base58;
import org.json.JSONObject;
import org.white5moke.handoff.client.Ez;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.zip.DataFormatException;

public class SigningDocument {
    public static final String ALGORITHM = "EC";
    public static final int KEY_SIZE = 256;
    public static final String SIGNING_ALGORITHM = "SHA256withECDSA";
    private static final String JSON_PRIV_KEY = "priv";
    private static final String JSON_PUB_KEY = "pub";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private Ez pubEz = Ez.getInstance();
    private Ez privEz = Ez.getInstance();

    public SigningDocument() {}

    public KeyPair generate() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);
        SecureRandom ran = SecureRandom.getInstance("SHA1PRNG");
        gen.initialize(KEY_SIZE, ran);
        KeyPair pair = gen.generateKeyPair();
        setPrivateKey(pair.getPrivate());
        setPublicKey(pair.getPublic());

        return pair;
    }

    public KeyPair fromJson(JSONObject json) throws NoSuchAlgorithmException, DataFormatException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);

        System.out.println("ez:  " + json.getString(JSON_PRIV_KEY));
        System.out.println("nez: " + Base64.getEncoder().encodeToString(Ez.getInstance().notEz(json.getString(JSON_PRIV_KEY))));

        return null;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String toString() {
        JSONObject j = new JSONObject();

        j.put(JSON_PRIV_KEY, privEz.ez(getPrivateKey().getEncoded()));
        j.put(JSON_PUB_KEY, pubEz.ez(getPublicKey().getEncoded()));

        return j.toString();
    }
}
