package luni.ether.core.discord;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DiscordIPCClient {

    private static final int OPCODE_HANDSHAKE = 0;
    private static final int OPCODE_FRAME = 1;
    private boolean connected = false;

    private RandomAccessFile pipe;

    public boolean connect() {

        for (int i = 0; i < 10; i++) {

            try {

                String path =
                        "\\\\.\\pipe\\discord-ipc-" + i;

                pipe = new RandomAccessFile(path, "rw");

                connected = true;
                return true;

            } catch (Exception ignored) {
            }
        }

        connected = false;
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

        pipe.write(bytes);
    }

    private void writeInt(int value)
            throws IOException {

        pipe.write(value & 0xFF);
        pipe.write((value >> 8) & 0xFF);
        pipe.write((value >> 16) & 0xFF);
        pipe.write((value >> 24) & 0xFF);
    }

    public void close() {
        connected = false;

        try {

            if (pipe != null) {
                pipe.close();
            }

        } catch (Exception ignored) {
        }
    }
}