package luni.ether.feature.account.minecraft;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores Microsoft/Minecraft account tokens on disk, encrypted at rest
 * (see LocalTokenCrypto for what that does and doesn't protect against).
 * Both the data file and the key file are locked to owner-only permissions.
 */
public class MsAccountStorage {

    private static final Gson GSON = new Gson();

    private static File getFile() {
        return new File(Minecraft.getInstance().gameDirectory, "ether_microsoft_accounts.enc");
    }

    private static File getKeyFile() {
        return new File(Minecraft.getInstance().gameDirectory, "ether_microsoft_accounts.key");
    }

    private static File getLegacyPlaintextFile() {
        return new File(Minecraft.getInstance().gameDirectory, "ether_microsoft_accounts.json");
    }

    public static List<MsAccount> load() {
        try {
            File file = getFile();

            if (file.exists()) {
                String encrypted = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                String json = LocalTokenCrypto.decrypt(getKeyFile(), encrypted);

                List<MsAccount> result = GSON.fromJson(json, new TypeToken<List<MsAccount>>(){}.getType());
                return result != null ? result : new ArrayList<>();
            }

            // One-time migration from the old unencrypted file, if present.
            File legacy = getLegacyPlaintextFile();
            if (legacy.exists()) {
                String json = Files.readString(legacy.toPath(), StandardCharsets.UTF_8);
                List<MsAccount> result = GSON.fromJson(json, new TypeToken<List<MsAccount>>(){}.getType());
                List<MsAccount> accounts = result != null ? result : new ArrayList<>();

                save(accounts); // write out encrypted copy
                Files.deleteIfExists(legacy.toPath()); // remove the plaintext copy
                return accounts;
            }

            return new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void save(List<MsAccount> accounts) {
        try {
            File file = getFile();
            File keyFile = getKeyFile();

            String json = GSON.toJson(accounts);
            String encrypted = LocalTokenCrypto.encrypt(keyFile, json);

            Files.writeString(file.toPath(), encrypted, StandardCharsets.UTF_8);
            LocalTokenCrypto.lockDownPermissions(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}