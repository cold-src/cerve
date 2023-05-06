package coldsrc.cerve.security;

/**
 * Constants for the standard encryption Cerve uses.
 */
public final class StandardEncryption {

    // key constants
    public static final int RSA_KEY_LENGTH = 1024;
    public static final int AES_KEY_LENGTH = 128;

    // encryption utility profiles
    public static final SymmetricEncryptionProfile  PROFILE_SYMMETRIC  = newSymmetricEncryptionProfile();
    public static final AsymmetricEncryptionProfile PROFILE_ASYMMETRIC = newAsymmetricEncryptionProfile();

    /**
     * Creates a new asymmetric encryption profile following
     * the general protocol standard.
     *
     * @return The profile.
     */
    public static AsymmetricEncryptionProfile newAsymmetricEncryptionProfile() {
        return new AsymmetricEncryptionProfile("RSA", "ECB", "PKCS1Padding", "RSA", RSA_KEY_LENGTH);
    }

    /**
     * Creates a new symmetric encryption profile following
     * the general protocol standard.
     *
     * @return The profile.
     */
    public static SymmetricEncryptionProfile newSymmetricEncryptionProfile() {
        return new SymmetricEncryptionProfile("AES", "ECB", "PKCS5Padding", "AES", AES_KEY_LENGTH);
    }

}
