package net.portalmod.core.packet;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface AbstractPacket<T extends AbstractPacket<T>> {
   void encode(PacketBuffer var1);

   T decode(PacketBuffer var1);

   boolean handle(Supplier<NetworkEvent.Context> var1);
}
