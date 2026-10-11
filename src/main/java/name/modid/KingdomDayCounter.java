package name.modid;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.tysontheember.emberstextapi.immersivemessages.api.ImmersiveMessage;
import net.tysontheember.emberstextapi.integration.EmbersTextFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class KingdomDayCounter implements ModInitializer {
	public static final String MOD_ID = "kingdom-day-counter";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution
		Registry.register(BuiltInRegistries.SOUND_EVENT, KingdomDayCounter.id("kingdom_day"), SoundEvent.createVariableRangeEvent(KingdomDayCounter.id("kingdom_day")));
		PayloadTypeRegistry.playS2C().register(NewDayPayload.TYPE, NewDayPayload.CODEC);

        var ref = new Object() {
			String romanDay = "";
            long previousDay = -1;
        };
		ServerTickEvents.END_WORLD_TICK.register(server -> {
			long day = server.getDayTime() / 24000;
            if (ref.previousDay != day) {
				ref.previousDay = day;
				for (ServerPlayer player : server.players()) {
					ref.romanDay = translateDay(day);
					final ImmersiveMessage msg = ImmersiveMessage.fromMarkup(160, String.format("[scale value=2.7][fade in=80 out=50] <circle frequency=0.2 radius=0.5><wave amplitude=0.5 frequency=0.25 wavelength=10><color value=000000><outline width=0.1 alpha=0.3 color=FFFFFF>%s</outline></color></circle></wave>", ref.romanDay));
					EmbersTextFacade.INSTANCE.send(player, msg);
					sendSoundToClient(player);
				}
			}
		});
	}

	private void sendSoundToClient(ServerPlayer player) {
		LOGGER.info("Sending new day sound to client: {}", player.getName().getString());
		ServerPlayNetworking.send(player, new NewDayPayload());
	}

	private String translateDay(long day) {
		StringBuilder translatedString = new StringBuilder();
		Map<String, Integer> romanNumerals = new java.util.LinkedHashMap<>();
		romanNumerals.put("M", 1000);
		romanNumerals.put("CM", 900);
		romanNumerals.put("D", 500);
		romanNumerals.put("CD", 400);
		romanNumerals.put("C", 100);
		romanNumerals.put("XC", 90);
		romanNumerals.put("L", 50);
		romanNumerals.put("XL", 40);
		romanNumerals.put("X", 10);
		romanNumerals.put("IX", 9);
		romanNumerals.put("V", 5);
		romanNumerals.put("IV", 4);
		romanNumerals.put("I", 1);

		int remaining = (int) day;
		while (remaining > 0) {
			for (Map.Entry<String, Integer> entry : romanNumerals.entrySet()) {
				if (remaining >= entry.getValue()) {
					translatedString.append(entry.getKey());
					remaining -= entry.getValue();
					break;
				}
			}
		}
		return translatedString.toString();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
