package me.twodee.dowcsttp;

import me.twodee.dowcsttp.crypto.CryptoUtils;
import me.twodee.dowcsttp.crypto.PemFile;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptoTest {

    @Test
    void keyPairGenerationTest() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        kpg.initialize(new ECGenParameterSpec("curve25519"));


        KeyPair keyPair = kpg.generateKeyPair();
        Cipher iesCipher = Cipher.getInstance("ECIES", BouncyCastleProvider.PROVIDER_NAME);
        iesCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] result = iesCipher.doFinal("some days".getBytes());
        System.out.println(new String(result));

        iesCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        assertThat(new String(iesCipher.doFinal(result))).isEqualTo("some days");
    }

    @Test
    void testPubKeyExporting() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        kpg.initialize(new ECGenParameterSpec("curve25519"));

        KeyPair keyPair = kpg.generateKeyPair();
        PemFile.writePublicKey(new File("ec.pub"), keyPair.getPublic());

        Key readKey = PemFile.readPublicKey(new File("ec.pub"));

        assertThat(readKey).isEqualTo(keyPair.getPublic());
    }

    @Test
    void testPrivateKeyExporting() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        kpg.initialize(new ECGenParameterSpec("curve25519"));

        KeyPair keyPair = kpg.generateKeyPair();
        PemFile.writePrivateKey(new File("ec.pem"), keyPair.getPrivate());

        Key readKey = PemFile.readPrivateKey(new File("ec.pem"));

        assertThat(readKey).isEqualTo(keyPair.getPrivate());
    }

    @Test
    void testRandomString() {
            System.out.println(CryptoUtils.generateSafeToken(256));
    }
}
