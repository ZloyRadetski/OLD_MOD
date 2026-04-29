package net.portalmod.common.sorted.trigger;

import java.util.HashMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.core.init.PacketInit;

public class TriggerSelectionServer {
   public static final HashMap<ServerPlayerEntity, TriggerTileEntity> TRIGGER_PER_PLAYER = new HashMap();

   public static void startConfiguration(ServerPlayerEntity player, TriggerTileEntity trigger) {
      trigger.startConfiguration(player);
      TRIGGER_PER_PLAYER.put(player, trigger);
      PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new STriggerStartConfigPacket(trigger.func_174877_v()));
   }

   public static void endConfiguration(ServerPlayerEntity player) {
      if (TRIGGER_PER_PLAYER.containsKey(player)) {
         ((TriggerTileEntity)TRIGGER_PER_PLAYER.get(player)).endConfiguration();
      }

      TRIGGER_PER_PLAYER.remove(player);
   }
}
