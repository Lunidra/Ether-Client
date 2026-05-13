package luni.ether.core;

import luni.ether.core.config.ConfigManager;
import luni.ether.core.input.KeybindHandler;
import luni.ether.core.io.ClientDirectories;

import luni.ether.feature.chat.ChatClient;
import luni.ether.ui.hud.impl.ArrayListComponent;
import luni.ether.ui.hud.impl.Watermark;
import luni.ether.ui.hud.impl.HudCoordComponent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EtherClient implements ClientModInitializer {

	public static final String NAME = "Ether";
	public static final String VERSION = "1.0";
	public static final String BUILD = "04x26-1b_ALPHA";
	public static final String MC_VER = "1.21.11";

	public static final String MOD_ID = NAME;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static EtherClient instance;
	private ClientContext context;

	public static EtherClient get() {
		return instance;
	}

	@Override
	public void onInitializeClient() {
		instance = this;

		init();

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			this.getContext().getHudManager().register(new Watermark());
			this.getContext().getHudManager().register(new HudCoordComponent());
			this.getContext().getHudManager().register(new ArrayListComponent());
			this.getContext().getEventBus().register(new KeybindHandler());

			ClientDirectories.init();
			LOGGER.info("Client Started!");
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			ConfigManager.save();
			ChatClient.get().shutdown();
		});
	}

	private void init() {
		this.context = new ClientContext();
		this.context.init();
	}



	public ClientContext getContext() {
		return context;
	}
}