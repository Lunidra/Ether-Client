package luni.ether.feature.module.mods.render;

import luni.ether.feature.module.Module;
import luni.ether.feature.module.Category;
import net.minecraft.client.Minecraft;

public class Freelook extends Module {

    private float yaw;
    private float pitch;

    public Freelook() {
        super("Freelook", Category.RENDER);

    }

    @Override
    public void onEnable() {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            yaw = player.getYRot();
            pitch = player.getXRot();
        }
    }

    public void update(float dx, float dy) {
        yaw += dx * 0.15f;
        pitch += dy * 0.15f;

        pitch = Math.max(-90, Math.min(90, pitch));
    }

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}