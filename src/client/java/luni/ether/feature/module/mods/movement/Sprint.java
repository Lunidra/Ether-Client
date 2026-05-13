package luni.ether.feature.module.mods.movement;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.feature.setting.impl.BooleanSetting;
import net.minecraft.client.Minecraft;

public class Sprint extends Module {
    private final Minecraft mc = Minecraft.getInstance();

    private final BooleanSetting vanilla = new BooleanSetting("Vanilla", true);

    public Sprint() {
        super("Sprint", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if(!mc.player.input.hasForwardImpulse()) {
            return;
        }

        if (vanilla.get()) {
            if (mc.player.getSpeed() > 0) {
                mc.player.setSprinting(true);
            }
        }else {
            mc.player.setSprinting(false);
        }

    }
}
