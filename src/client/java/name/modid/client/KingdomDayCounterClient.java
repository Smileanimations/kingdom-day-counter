package name.modid.client;

import name.modid.KingdomDayCounter;
import name.modid.NewDayPayload;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KingdomDayCounterClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(KingdomDayCounterClient.class);

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		SoundEvent sound = SoundEvent.createVariableRangeEvent(KingdomDayCounter.id("kingdom_day"));
		ClientPlayNetworking.registerGlobalReceiver(NewDayPayload.TYPE, (client, packet) -> {
			packet.client().execute(() -> {
				LOGGER.info("Receiving Package... Playing Sound...");
				packet.client().getSoundManager().play(
						SimpleSoundInstance.forUI(sound, 1.0f, 1.0f)
				);
			});
		});
	}
}