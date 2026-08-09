package luni.ether.feature.account.minecraft;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/**
 * Encrypts the locally stored Microsoft account JSON at rest.
 * <p>
 * Threat model / honesty check: this protects against casual disk access —
 * someone browsing the game directory, a backup tool, or a bulk "grab every
 * .json with 'token' in it" scraper — because the key file and the data
 * file are both locked to owner-only permissions. It does NOT protect
 * against an attacker who already has the same OS-user-level access as the
 * player (e.g. other malware running as that user), since the key has to
 * live somewhere readable by the game process itself. Real protection
 * against that requires OS keychain integration (Credential Manager /
 * Keychain / Secret Service), which is a bigger lift — flagged separately.
 */
final class LocalTokenCrypto {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;
    private static final int IV_BYTES = 12;
    private static final int KEY_BITS = 256;

    private LocalTokenCrypto() {}

    static String encrypt(File keyFile, String plaintext) throws Exception {
        SecretKey key = loadOrCreateKey(keyFile);

        byte[] iv = new byte[IV_BYTES];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    static String decrypt(File keyFile, String encoded) throws Exception {
        SecretKey key = loadOrCreateKey(keyFile);

        byte[] combined = Base64.getDecoder().decode(encoded);
        byte[] iv = new byte[IV_BYTES];
        byte[] cipherText = new byte[combined.length - IV_BYTES];
        System.arraycopy(combined, 0, iv, 0, IV_BYTES);
        System.arraycopy(combined, IV_BYTES, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));

        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
    }

    private static SecretKey loadOrCreateKey(File keyFile) throws Exception {
        if (keyFile.exists()) {
            byte[] keyBytes = Base64.getDecoder().decode(
                    Files.readString(keyFile.toPath(), StandardCharsets.UTF_8).trim()
            );
            return new SecretKeySpec(keyBytes, "AES");
        }

        byte[] keyBytes = new byte[KEY_BITS / 8];
        new SecureRandom().nextBytes(keyBytes);

        Files.writeString(keyFile.toPath(), Base64.getEncoder().encodeToString(keyBytes), StandardCharsets.UTF_8);
        lockDownPermissions(keyFile);

        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Best-effort restriction to owner-only read/write. Uses POSIX
     * permissions where available (Linux/macOS); falls back to the
     * cross-platform File API (which still denies "others" on POSIX
     * systems and has partial effect on Windows) where POSIX isn't
     * supported.
     */
    static void lockDownPermissions(File file) {
        try {
            if (file.toPath().getFileSystem().supportedFileAttributeViews().contains("posix")) {
                Files.setPosixFilePermissions(
                        file.toPath(),
                        Set.of(
                                java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                                java.nio.file.attribute.PosixFilePermission.OWNER_WRITE
                        )
                );
                return;
            }
        } catch (Exception ignored) {
            // fall through to best-effort fallback below
        }

        //noinspection ResultOfMethodCallIgnored
        file.setReadable(false, false);
        //noinspection ResultOfMethodCallIgnored
        file.setReadable(true, true);
        //noinspection ResultOfMethodCallIgnored
        file.setWritable(false, false);
        //noinspection ResultOfMethodCallIgnored
        file.setWritable(true, true);
    }
}
