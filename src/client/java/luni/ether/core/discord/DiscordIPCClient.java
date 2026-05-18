package luni.ether.core.discord;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.SocketChannel;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DiscordIPCClient {

    private static final int OPCODE_HANDSHAKE = 0;
    private static final int OPCODE_FRAME = 1;
    private boolean connected = false;

    private OutputStream output;
    private InputStream input;
    private SocketChannel unixChannel;

    private final boolean windows =
            System.getProperty("os.name")
                    .toLowerCase()
                    .contains("win");

    public boolean connect() {

        connected = false;

        for (int i = 0; i < 10; i++) {

            try {

                // WINDOWS
                if (windows) {

                    String path =
                            "\\\\.\\pipe\\discord-ipc-" + i;

                    var pipe =
                            new java.io.RandomAccessFile(path, "rw");

                    input = new java.io.BufferedInputStream(
                            new java.io.FileInputStream(
                                    pipe.getFD()
                            )
                    );

                    output = new java.io.BufferedOutputStream(
                            new java.io.FileOutputStream(
                                    pipe.getFD()
                            )
                    );

                    connected = true;

                    System.out.println(
                            "[EtherRPC] Connected via Windows IPC."
                    );

                    return true;
                }

                // LINUX / UNIX
                String runtime =
                        System.getenv("XDG_RUNTIME_DIR");

                String[] basePaths = new String[] {
                        runtime,
                        "/tmp",

                        // Flatpak Discord
                        runtime + "/app/com.discordapp.Discord",

                        // Vesktop
                        runtime + "/app/dev.vencord.Vesktop",

                        // Canary
                        runtime + "/app/com.discordapp.DiscordCanary"
                };

                for (String base : basePaths) {

                    if (base == null) {
                        continue;
                    }

                    File socketFile =
                            new File(base,
                                    "discord-ipc-" + i);

                    if (!socketFile.exists()) {
                        continue;
                    }

                    System.out.println(
                            "[EtherRPC] Trying IPC: "
                                    + socketFile.getAbsolutePath()
                    );

                    UnixDomainSocketAddress address =
                            UnixDomainSocketAddress.of(
                                    socketFile.toPath()
                            );

                    unixChannel =
                            SocketChannel.open(
                                    StandardProtocolFamily.UNIX
                            );

                    unixChannel.connect(address);

                    input =
                            java.nio.channels.Channels.newInputStream(
                                    unixChannel
                            );

                    output =
                            java.nio.channels.Channels.newOutputStream(
                                    unixChannel
                            );

                    connected = true;

                    System.out.println(
                            "[EtherRPC] Connected via "
                                    + socketFile.getAbsolutePath()
                    );

                    return true;
                }

            } catch (Exception e) {

                System.out.println(
                        "[EtherRPC] IPC attempt failed: "
                                + e.getMessage()
                );
            }
        }

        System.out.println(
                "[EtherRPC] Discord not found."
        );

        return false;
    }

    public boolean isConnected() {
        return connected;
    }

    public void handshake(String clientId)
            throws IOException {

        JsonObject json = new JsonObject();

        json.addProperty("v", 1);
        json.addProperty("client_id", clientId);

        send(OPCODE_HANDSHAKE, json.toString());
    }

    public void setActivity(
            String details,
            String state,
            long startTimestamp
    ) throws IOException {

        JsonObject timestamps =
                new JsonObject();

        timestamps.addProperty(
                "start",
                startTimestamp
        );

        JsonObject activity =
                new JsonObject();

        activity.addProperty(
                "details",
                details
        );

        activity.addProperty(
                "state",
                state
        );

        activity.add(
                "timestamps",
                timestamps
        );

        activity.addProperty(
                "large_image",
                "ether"
        );

        activity.addProperty(
                "large_text",
                "Ether Client"
        );

        JsonObject args =
                new JsonObject();

        args.add(
                "activity",
                activity
        );

        args.addProperty(
                "pid",
                ProcessHandle.current().pid()
        );

        JsonObject payload =
                new JsonObject();

        payload.addProperty(
                "cmd",
                "SET_ACTIVITY"
        );

        payload.add(
                "args",
                args
        );

        payload.addProperty(
                "nonce",
                UUID.randomUUID().toString()
        );

        send(OPCODE_FRAME, payload.toString());
    }

    private void send(int opcode, String json)
            throws IOException {

        byte[] bytes =
                json.getBytes(StandardCharsets.UTF_8);

        writeInt(opcode);
        writeInt(bytes.length);

        output.write(bytes);
        output.flush();
    }

    private void writeInt(int value)
            throws IOException {

        output.write(value & 0xFF);
        output.write((value >> 8) & 0xFF);
        output.write((value >> 16) & 0xFF);
        output.write((value >> 24) & 0xFF);
    }

    public void close() {
        connected = false;

        try {

            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (unixChannel != null) {
                unixChannel.close();
            }

        } catch (Exception ignored) {
        }
    }
}