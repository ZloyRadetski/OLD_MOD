package net.portalmod.common.sorted.portalgun;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.portalmod.common.sorted.autoportal.AutoPortalBlock;
import net.portalmod.common.sorted.panel.PortalHelper;
import net.portalmod.common.sorted.portal.AbstractPortalHelper;
import net.portalmod.common.sorted.portal.PortalEnd;
import net.portalmod.common.sorted.portal.PortalHelperRepresentation;
import net.portalmod.common.sorted.portal.VolatilePortalHelper;
import net.portalmod.core.math.Vec3;

public class PortalHelperServerManager {
   private static PortalHelperServerManager instance;
   private static final int HELPER_DELAY = 15;
   private Map<ServerPlayerEntity, PortalHelperState> helpStates = new HashMap();

   private PortalHelperServerManager() {
   }

   public static PortalHelperServerManager getInstance() {
      if (instance == null) {
         instance = new PortalHelperServerManager();
      }

      return instance;
   }

   public void tick() {
      this.helpStates.forEach((player, state) -> state.tick());
   }

   public boolean willBeHelped(ServerPlayerEntity player, UUID gun, PortalEnd end, BlockPos pos, Direction face, Direction horizontalDirection, World level) {
      PortalHelperState helperState = (PortalHelperState)this.helpStates.get(player);
      BlockState state = level.func_180495_p(pos);
      Block block = state.func_177230_c();
      Block frontBlock = level.func_180495_p(pos.func_177972_a(face)).func_177230_c();
      if (helperState != null && helperState.lastHelpedHelper instanceof PortalHelperRepresentation && helperState.nextHelp > 0L && !(frontBlock instanceof AutoPortalBlock) && helperState.isBasicallySame(gun, end, level, pos, face) && block instanceof PortalHelper && ((PortalHelper)block).willHelpPortal(face, horizontalDirection, state, level)) {
         return false;
      } else {
         return block instanceof PortalHelper && ((PortalHelper)block).willHelpPortal(face, horizontalDirection, state, level);
      }
   }

   public boolean willBeHelped(ServerPlayerEntity player, UUID gun, PortalEnd end, Vec3 position, Direction face, VolatilePortalHelper helper) {
      PortalHelperState helperState = (PortalHelperState)this.helpStates.get(player);
      return helperState != null && helperState.lastHelpedHelper instanceof VolatilePortalHelper && helperState.nextHelp > 0L && helperState.isBasicallySame(gun, end, helper) ? false : helper.willHelpPortal(position, face);
   }

   public void setHelped(ServerPlayerEntity player, UUID gun, PortalEnd end, BlockPos pos, Direction face) {
      this.helpStates.put(player, new PortalHelperState(gun, end, new PortalHelperRepresentation(pos, face), 15L));
   }

   public void setHelped(ServerPlayerEntity player, UUID gun, PortalEnd end, VolatilePortalHelper helper) {
      this.helpStates.put(player, new PortalHelperState(gun, end, helper, 15L));
   }

   private static class PortalHelperState {
      private final UUID lastHelpedGun;
      private final PortalEnd lastHelpedEnd;
      private final AbstractPortalHelper lastHelpedHelper;
      private long nextHelp;

      private PortalHelperState(UUID gun, PortalEnd end, AbstractPortalHelper helper, long nextHelp) {
         this.lastHelpedGun = gun;
         this.lastHelpedEnd = end;
         this.lastHelpedHelper = helper;
         this.nextHelp = nextHelp;
      }

      private void tick() {
         if (this.nextHelp > 0L) {
            --this.nextHelp;
         }

      }

      private boolean isBasicallySame(UUID gun, PortalEnd end, World level, BlockPos pos, Direction face) {
         if (!(this.lastHelpedHelper instanceof PortalHelperRepresentation)) {
            return false;
         } else {
            boolean sameGun = gun.equals(this.lastHelpedGun);
            boolean sameEnd = end.equals(this.lastHelpedEnd);
            boolean sameHelper = ((PortalHelperRepresentation)this.lastHelpedHelper).isInside(level, pos, face);
            return sameGun && sameEnd && sameHelper;
         }
      }

      private boolean isBasicallySame(UUID gun, PortalEnd end, VolatilePortalHelper helper) {
         if (!(this.lastHelpedHelper instanceof VolatilePortalHelper)) {
            return false;
         } else {
            boolean sameGun = gun.equals(this.lastHelpedGun);
            boolean sameEnd = end.equals(this.lastHelpedEnd);
            boolean sameHelper = this.lastHelpedHelper.equals(helper);
            return sameGun && sameEnd && sameHelper;
         }
      }
   }
}
