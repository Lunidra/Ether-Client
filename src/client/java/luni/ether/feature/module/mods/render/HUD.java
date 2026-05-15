package luni.ether.feature.module.mods.render;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.feature.setting.impl.BooleanSetting;

public class HUD extends Module {

    public final BooleanSetting coordinates =
            addSetting(new BooleanSetting(
                    "Coordinates",
                    true
            ));

    public final BooleanSetting fps =
            addSetting(new BooleanSetting(
                    "FPS",
                    true
            ));

    public final BooleanSetting ping =
            addSetting(new BooleanSetting(
                    "Ping",
                    true
            ));

    public final BooleanSetting sessionTime =
            addSetting(new BooleanSetting(
                    "SessionTime",
                    true
            ));

    public HUD() {
        super("HUD", Category.RENDER);

        setEnabled(true);
    }

    @Override
    public void toggle() {
        // prevent disabling
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(true);
    }
}