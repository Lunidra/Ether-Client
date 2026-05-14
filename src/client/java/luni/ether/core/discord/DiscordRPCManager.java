package luni.ether.core.discord;

import net.minecraft.client.Minecraft;

public class DiscordRPCManager {

    // YOUR DISCORD APP ID
    private static final String CLIENT_ID =
            "1160356145848909825";

    private static final DiscordIPCClient ipc =
            new DiscordIPCClient();

    private static boolean running = false;

    private static long startTimestamp;

    public static void start() {

        if (running) {
            return;
        }

        running = true;

        startTimestamp =
                System.currentTimeMillis() / 1000L;

        Thread thread =
                new Thread(() -> {

                    while (running) {

                        try {

                            if (!ipc.isConnected()) {

                                boolean connected =
                                        ipc.connect();

                                if (connected) {

                                    ipc.handshake(CLIENT_ID);

                                    System.out.println(
                                            "[EtherRPC] Connected to Discord."
                                    );
                                }
                            }

                            if (ipc.isConnected()) {
                                update();
                            }

                            Thread.sleep(5000);

                        } catch (Exception e) {

                            ipc.close();

                            try {
                                Thread.sleep(5000);
                            } catch (Exception ignored) {
                            }
                        }
                    }

                }, "Ether-Discord-RPC");

        thread.setDaemon(true);
        thread.start();

        System.out.println(
                "[EtherRPC] RPC Thread Started."
        );
    }

    public static void update() {

        try {

            Minecraft mc =
                    Minecraft.getInstance();

            String state;

            if (mc.getCurrentServer() != null) {

                state =
                        mc.getCurrentServer().ip;

            } else {

                state = "Singleplayer";
            }

            ipc.setActivity(
                    "Playing Minecraft with Ether",
                    state,
                    startTimestamp
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {

        running = false;

        ipc.close();

        System.out.println(
                "[EtherRPC] Shutdown."
        );
    }
}