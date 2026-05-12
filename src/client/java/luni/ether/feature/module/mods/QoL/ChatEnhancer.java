package luni.ether.feature.module.mods.QoL;
import luni.ether.feature.module.Category;
import luni.ether.feature.module.Module;
import luni.ether.feature.chat.ChatClient;
import net.minecraft.client.Minecraft;

public class ChatEnhancer extends Module {

    public static ChatEnhancer INSTANCE;

    public boolean timestamps = true;
    public boolean highlightMentions = true;
    public boolean highlightSelf = true;
    private Minecraft mc = Minecraft.getInstance();

    public ChatEnhancer() {
        super("ChatEnhancer", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        ChatClient.get().notifyUser("Chat Enhancer Enabled!");
        mc.player.playSound(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(), 0.8f, 1.5f);
    }

    @Override
    public void onDisable() {
        ChatClient.get().notifyUser("Chat Enhancer Disabled!");
        mc.player.playSound(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_PLING.value(), 1f, 1.2f);
    }
}