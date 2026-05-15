package luni.ether.core;

import luni.ether.core.config.ConfigManager;
import luni.ether.core.discord.DiscordRPCManager;
import luni.ether.core.input.KeybindHandler;
import luni.ether.core.io.ClientDirectories;

import luni.ether.feature.chat.ChatClient;
import luni.ether.feature.module.mods.render.HUD;
import luni.ether.ui.hud.impl.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EtherClient implements ClientModInitializer {

	public static final String NAME = "Ether";
	public static final String VERSION = "1.2";
	public static final String BUILD = "05x26-b5_ALPHA";
	public static final String MC_VER = "26.1";

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
			this.getContext().getHudManager().register(new ArrayListComponent());
			this.getContext().getEventBus().register(new KeybindHandler());

			DiscordRPCManager.start();

			var moduleManager =
					this.getContext()
							.getModuleManager();

			HUD hud =
					(HUD) moduleManager.getByName("HUD");

			HudCoordComponent coordinates =
					new HudCoordComponent();

			coordinates.setSetting(hud.coordinates);

			this.getContext()
					.getHudManager()
					.register(coordinates);

			HudFpsComponent fps =
					new HudFpsComponent();

			fps.setSetting(hud.fps);

			this.getContext()
					.getHudManager()
					.register(fps);

			HudPingComponent ping =
					new HudPingComponent();

			ping.setSetting(hud.ping);

			this.getContext()
					.getHudManager()
					.register(ping);

			HudPlaytimeComponent playtime =
					new HudPlaytimeComponent();

			playtime.setSetting(hud.sessionTime);

			this.getContext()
					.getHudManager()
					.register(playtime);

			ClientDirectories.init();
			LOGGER.info("Client Started!");
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			ConfigManager.save();
			ChatClient.get().shutdown();
			DiscordRPCManager.shutdown();
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