package name.modid;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NewDayPayload() implements CustomPacketPayload {
    public static final Type<NewDayPayload> TYPE =
            new Type<>(KingdomDayCounter.id("kingdom_day"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NewDayPayload> CODEC =
            StreamCodec.unit(new NewDayPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}