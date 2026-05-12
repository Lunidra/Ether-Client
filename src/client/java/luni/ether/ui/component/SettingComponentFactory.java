package luni.ether.ui.component;

import luni.ether.feature.setting.Setting;
import luni.ether.feature.setting.impl.BooleanSetting;
import luni.ether.feature.setting.impl.KeybindSetting;
import luni.ether.feature.setting.impl.ModeSetting;
import luni.ether.feature.setting.impl.NumberSetting;
import luni.ether.ui.clickgui.component.impl.KeybindComponent;
import luni.ether.ui.clickgui.component.impl.ModeComponent;
import luni.ether.ui.clickgui.component.impl.SliderComponent;
import luni.ether.ui.clickgui.component.impl.ToggleComponent;

public class SettingComponentFactory {

    public static SettingComponent create(Setting s) {

        if (s instanceof BooleanSetting b) {
            return new ToggleComponent(b);
        }

        if (s instanceof NumberSetting n) {
            return new SliderComponent(n);
        }

        if (s instanceof ModeSetting m) {
            return new ModeComponent(m);
        }

        if (s instanceof KeybindSetting k) {
            return new KeybindComponent(k);
        }

        return null;
    }
}