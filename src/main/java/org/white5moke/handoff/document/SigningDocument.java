package org.white5moke.handoff.document;

import org.json.JSONObject;
import org.white5moke.handoff.client.Ez;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

    public static SigningDocument fromJson(JSONObject json) throws NoSuchAlgorithmException, DataFormatException,
            InvalidKeySpecException {
        SigningDocument signingDoc = new SigningDocument();

        String privS = json.getString(JSON_PRIV_KEY).strip();
        //System.out.println("privS: " + privS);
        byte[] privBs = Ez.getInstance().notEz(json.getString(JSON_PRIV_KEY).strip());
        byte[] pubBs = Ez.getInstance().notEz(json.getString(JSON_PUB_KEY).strip());

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privBs);
        KeyFactory factory = KeyFactory.getInstance(SigningDocument.ALGORITHM);
        PrivateKey privateKey = factory.generatePrivate(spec);

        X509EncodedKeySpec spec1 = new X509EncodedKeySpec(pubBs);
        PublicKey publicKey = factory.generatePublic(spec1);

        signingDoc.setPublicKey(publicKey);
        signingDoc.setPrivateKey(privateKey);

        return signingDoc;
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