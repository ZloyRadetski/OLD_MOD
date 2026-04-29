package net.portalmod.core.packet;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.init.GameRuleInit;

public class SUpdateBooleanGameRulePacket implements AbstractPacket<SUpdateBooleanGameRulePacket> {
   private String name;
   private boolean value;

   public SUpdateBooleanGameRulePacket() {
   }

   public SUpdateBooleanGameRulePacket(String name, boolean value) {
      this.name = name;
      this.value = value;
   }

   public SUpdateBooleanGameRulePacket decode(PacketBuffer buffer) {
      int length = buffer.readInt();
      String rule = buffer.readCharSequence(length, StandardCharsets.UTF_8).toString();
      boolean value = buffer.readBoolean();
      return new SUpdateBooleanGameRulePacket(rule, value);
   }

   public void encode(PacketBuffer buffer) {
      buffer.writeInt(this.name.length());
      buffer.writeCharSequence(this.name, StandardCharsets.UTF_8);
      buffer.writeBoolean(this.value);
   }

   public boolean handle(Supplier<NetworkEvent.Context> context) {
      ((NetworkEvent.Context)context.get()).enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
               ClientWorld level = Minecraft.func_71410_x().field_71441_e;
               if (level != null) {
                  ((GameRules.BooleanValue)level.func_82736_K().func_223585_a(GameRuleInit.getRule(this.name))).func_223570_a(this.value, (MinecraftServer)null);
               }
            }));
      ((NetworkEvent.Context)context.get()).setPacketHandled(true);
      return true;
   }
}
