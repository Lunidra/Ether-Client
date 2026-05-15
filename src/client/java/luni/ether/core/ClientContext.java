package luni.ether.core;

import luni.ether.core.config.ConfigManager;
import luni.ether.core.event.EventBus;
import luni.ether.core.event.impl.TickEvent;
import luni.ether.core.input.InputManager;
import luni.ether.core.window.TitleManager;
import luni.ether.feature.chat.ChatClient;
import luni.ether.feature.module.ModuleManager;
import luni.ether.ui.hud.HUDManager;
import luni.ether.ui.notification.NotificationManager;
import luni.ether.ui.render.UIRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class ClientContext {

    private final EventBus eventBus;
    private final InputManager inputManager;
    private final TitleManager titleManager;
    private final ModuleManager moduleManager;
    private final HUDManager hudManager;

    public ClientContext() {
        this.eventBus = new EventBus();
        this.inputManager = new InputManager();
        this.titleManager = new TitleManager();
        this.moduleManager = new ModuleManager();
        this.hudManager = new HUDManager();
    }

    public void init() {
        titleManager.init();
        moduleManager.init();
        ConfigManager.load();
        ConfigManager.loadHud(
                EtherClient.get()
                        .getContext()
                        .getHudManager()
        );
        ConfigManager.loadTheme();
        ChatClient.get().connect();
        inputManager.init();



        //  THIS IS THE MISSING LINK
        HudRenderCallback.EVENT.register((ctx, delta) -> {

            UIRenderer renderer = new UIRenderer(ctx);

            int mouseX = (int) Minecraft.getInstance().mouseHandler.xpos();
            int mouseY = (int) Minecraft.getInstance().mouseHandler.ypos();

            this.getHudManager().render(renderer, mouseX, mouseY);

        });

        //TODO CLIENT TICK EVENT
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ChatClient.get().tick();
            if (client.player == null) return;


            eventBus.post(new TickEvent());;
        });

    }

    // === Getters ===
    public EventBus getEventBus() {
        return eventBus;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public TitleManager getTitleManager() {
        return titleManager;
    }

    public ModuleManager getModuleManager() { return moduleManager; }

    public HUDManager getHudManager() {
        return hudManager;
    }


}