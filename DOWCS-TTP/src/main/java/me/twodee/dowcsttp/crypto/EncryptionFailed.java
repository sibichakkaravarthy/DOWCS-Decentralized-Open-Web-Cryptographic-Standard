package me.twodee.dowcsttp.crypto;

public class EncryptionFailed extends Throwable {
    public EncryptionFailed(Throwable e) {
        super("Something went wrong with the encryption", e);
    }
}
