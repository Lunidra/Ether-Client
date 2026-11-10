package luni.ether.feature.module.mods.QoL;

import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.feature.setting.impl.BooleanSetting;
import net.minecraft.client.Minecraft;

public class ChatEnhancer extends Module {

    public static ChatEnhancer INSTANCE;


    private Minecraft mc = Minecraft.getInstance();

    public ChatEnhancer() {
        super("ChatEnhancer", Category.RENDER);
        INSTANCE = this;
    }

    public final BooleanSetting timestamps =
            addSetting(new BooleanSetting(
                    "Timestamps",
                    true
            ));

    public final BooleanSetting highlightMentions =
            addSetting(new BooleanSetting(
                    "HighlightMentions",
                    true
            ));

    public final BooleanSetting highlightSelf =
            addSetting(new BooleanSetting(
                    "HighlightSelf",
                    true
            ));
    public final BooleanSetting Versions = addSetting(new BooleanSetting("Display Game Versions", true));

    @Override
    public void onEnable() {
        // Chat functionality removed
    }

    @Override
    public void onDisable() {
        // Chat functionality removed
    }
}