package me.twodee.dowcsttp.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class CryptoUtils {
    public static String generateSafeToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    public static byte[] encryptECIES(Key key, String data) throws EncryptionFailed {
        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher iesCipher = Cipher.getInstance("ECIES", BouncyCastleProvider.PROVIDER_NAME);
            iesCipher.init(Cipher.ENCRYPT_MODE, key);
            return iesCipher.doFinal(data.getBytes());
        } catch (Throwable e) {
            throw new EncryptionFailed(e);
        }
    }
}
