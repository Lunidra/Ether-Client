package luni.ether.core.event.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import luni.ether.core.event.Event;
import net.minecraft.client.Camera;


public class Render3DEvent extends Event {

    private final PoseStack matrices;
    private final Camera camera;

    public Render3DEvent(PoseStack matrices, Camera camera) {
        this.matrices = matrices;
        this.camera = camera;
    }

    public PoseStack getMatrices() {
        return matrices;
    }

    public Camera getCamera() {
        return camera;
    }
}