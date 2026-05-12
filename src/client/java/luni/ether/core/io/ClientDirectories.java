package luni.ether.core.io;

import net.minecraft.client.Minecraft;

import java.io.File;

public class ClientDirectories {

    public static void init() {
        File etherDir = new File(Minecraft.getInstance().gameDirectory, "Ether/Themes");
        if (!etherDir.exists()) {
            etherDir.mkdirs();
        }
    }
}
