package net.portalmod.common.sorted.portal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.portalmod.core.math.Vec3;

public class PortalServerProofManager {
   private static PortalServerProofManager instance;
   private final Map<ServerPlayerEntity, BlockInteractionProof> blockInteractionProofs = new HashMap();

   private PortalServerProofManager() {
   }

   public static PortalServerProofManager getInstance() {
      if (instance == null) {
         instance = new PortalServerProofManager();
      }

      return instance;
   }

   public void setProof(ServerPlayerEntity player, int tick, int[] portalChain) {
      this.blockInteractionProofs.put(player, new BlockInteractionProof(tick, portalChain));
   }

   public boolean hasBelievableProof(ServerPlayerEntity player, BlockPos pos, boolean breakBlock) {
      return !this.blockInteractionProofs.containsKey(player) ? false : ((BlockInteractionProof)this.blockInteractionProofs.get(player)).isValid(player, pos, breakBlock);
   }

   private static class BlockInteractionProof {
      private final int tick;
      private final int[] portalChain;

      public BlockInteractionProof(int tick, int[] portalChain) {
         this.tick = tick;
         this.portalChain = portalChain;
      }

      public boolean isValid(ServerPlayerEntity player, BlockPos pos, boolean breakBlock) {
         MinecraftServer server = player.func_184102_h();
         ServerWorld level = player.func_71121_q();
         if (server != null && server.func_71259_af() == this.tick) {
            ModifiableAttributeInstance attribute = player.func_110148_a((Attribute)ForgeMod.REACH_DISTANCE.get());
            if (attribute == null) {
               return false;
            } else {
               IntStream var10000 = Arrays.stream(this.portalChain);
               level.getClass();
               List<Entity> entities = (List)var10000.mapToObj(level::func_73045_a).collect(Collectors.toList());
               double reach = attribute.func_111126_e() + (double)(breakBlock ? 1 : 3);
               reach *= reach;
               Vec3 blockPos = (new Vec3(pos)).add((double)0.5F);
               Vec3 updatingPosition = (new Vec3(player.func_213303_ch())).add((double)0.0F, breakBlock ? (double)1.5F : (double)0.0F, (double)0.0F);

               for(Entity entity : entities) {
                  if (!(entity instanceof PortalEntity)) {
                     return false;
                  }

                  PortalEntity portal = (PortalEntity)entity;
                  if (!portal.getOtherPortal().isPresent()) {
                     return false;
                  }

                  double distance = updatingPosition.clone().sub(portal.func_213303_ch()).magnitudeSqr();
                  if (distance > reach) {
                     return false;
                  }

                  updatingPosition = new Vec3(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch());
               }

               return updatingPosition.clone().sub(blockPos).magnitudeSqr() <= reach;
            }
         } else {
            return false;
         }
      }
   }
}
