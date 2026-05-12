package luni.ether.feature.account.minecraft;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MsAccountStorage {
    private static File getFile() {
        return new File(Minecraft.getInstance().gameDirectory, "ether_microsoft_accounts.json");
    }

    private static final Gson GSON = new Gson();


    public static List<MsAccount> load() {

        try {

            File file = getFile();

            if (!file.exists()) return new ArrayList<>();

            FileReader reader = new FileReader(file);

            List<MsAccount> result = GSON.fromJson(
                    reader,
                    new TypeToken<List<MsAccount>>(){}.getType()
            );

            reader.close();

            return result != null ? result : new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void save(List<MsAccount> accounts) {
        File file = getFile();
        try {

            FileWriter writer = new FileWriter(file);

            GSON.toJson(accounts, writer);

            writer.close();

        } catch (Exception ignored) {}

    }
}