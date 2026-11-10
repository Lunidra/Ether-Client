package luni.ether.core;

import luni.ether.core.config.ConfigManager;
import luni.ether.core.event.EventBus;
import luni.ether.core.event.impl.TickEvent;
import luni.ether.core.input.InputManager;
import luni.ether.core.window.TitleManager;
import luni.ether.feature.module.ModuleManager;
import luni.ether.ui.hud.HUDManager;
import luni.ether.ui.notification.NotificationManager;
import luni.ether.ui.notification.NotificationRenderer;
import luni.ether.ui.render.UIRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class ClientContext {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ClientContext.class);

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
        LOGGER.info("[Ether] Initializing client context...");
        titleManager.init();
        moduleManager.init();
        ConfigManager.load();
        ConfigManager.loadHud(
                EtherClient.get()
                        .getContext()
                        .getHudManager()
        );
        ConfigManager.loadTheme();
        inputManager.init();

        LOGGER.info("[Ether] Core components initialized");

        //  THIS IS THE MISSING LINK
        HudRenderCallback.EVENT.register((ctx, delta) -> {

            UIRenderer renderer = new UIRenderer(ctx);

            int mouseX = (int) Minecraft.getInstance().mouseHandler.xpos();
            int mouseY = (int) Minecraft.getInstance().mouseHandler.ypos();

            this.getHudManager().render(renderer, mouseX, mouseY);

            NotificationRenderer.render(renderer);

        });

        //TODO CLIENT TICK EVENT
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

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