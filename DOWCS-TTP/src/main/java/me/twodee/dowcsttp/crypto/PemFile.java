package me.twodee.dowcsttp.crypto;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

public class PemFile {

    private static void write(File file, Key key, String description) throws IOException {
        Security.addProvider(new BouncyCastleProvider());

        PemObject pemObject = new PemObject(description, key.getEncoded());
        try (PemWriter pemWriter = new PemWriter(new FileWriter(file))) {
            pemWriter.writeObject(pemObject);
        }
    }

    public static void writePrivateKey(File file, Key key) throws IOException {
        write(file, key, "PRIVATE KEY");
    }

    public static void writePublicKey(File file, Key key) throws IOException {
        write(file, key, "PUBLIC KEY");
    }

    private static PublicKey readPublicKey(Reader reader) throws IOException {
        Security.addProvider(new BouncyCastleProvider());

        PEMParser pemParser = new PEMParser(reader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
        return converter.getPublicKey(publicKeyInfo);
    }

    public static PublicKey readPublicKey(String keyString) throws IOException {
        return readPublicKey(new StringReader(keyString));
    }


    public static PublicKey readPublicKey(File file) throws IOException {
        try (FileReader keyReader = new FileReader(file)) {
           return readPublicKey(keyReader);
        }
    }

    public static PrivateKey readPrivateKey(File file) throws IOException {
        try (FileReader keyReader = new FileReader(file)) {
            return readPrivateKey(keyReader);
        }
    }

    public static PrivateKey readPrivateKey(String keyString) throws IOException {
        return readPrivateKey(new StringReader(keyString));
    }

    private static PrivateKey readPrivateKey(Reader reader) throws IOException {
        Security.addProvider(new BouncyCastleProvider());

        PEMParser pemParser = new PEMParser(reader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());

        return converter.getPrivateKey(privateKeyInfo);
    }
}
