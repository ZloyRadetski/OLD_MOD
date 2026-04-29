package net.portalmod.common.sorted.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.portalmod.common.sorted.portalgun.PortalHelperServerManager;
import net.portalmod.core.math.Vec3;

public class VolatilePortalHelperManager {
   private static VolatilePortalHelperManager instance;
   private final Map<RegistryKey<World>, List<VolatilePortalHelper>> volatilePortalHelpers = new HashMap();

   private VolatilePortalHelperManager() {
   }

   public static VolatilePortalHelperManager getInstance() {
      if (instance == null) {
         instance = new VolatilePortalHelperManager();
      }

      return instance;
   }

   public void addVolatilePortalHelper(World level, Vec3 position, Direction normal, float radius) {
      List<VolatilePortalHelper> helpers = (List)this.volatilePortalHelpers.getOrDefault(level.func_234923_W_(), new ArrayList());
      helpers.add(new VolatilePortalHelper(position, normal, radius));
      this.volatilePortalHelpers.put(level.func_234923_W_(), helpers);
   }

   public void clearVolatilePortalHelpers() {
      this.volatilePortalHelpers.forEach((k, v) -> v.clear());
   }

   public Optional<VolatilePortalHelper> findHelperThatWillHelp(ServerPlayerEntity player, UUID gun, PortalEnd end, World level, Vec3 hitPos, Direction face) {
      List<VolatilePortalHelper> helpers = new ArrayList((Collection)this.volatilePortalHelpers.getOrDefault(level.func_234923_W_(), new ArrayList()));

      for(VolatilePortalHelper helper : (List)helpers.stream().sorted((o1, o2) -> (int)Math.signum(o1.position.clone().sub(hitPos).magnitudeSqr() - o2.position.clone().sub(hitPos).magnitudeSqr())).collect(Collectors.toList())) {
         if (PortalHelperServerManager.getInstance().willBeHelped(player, gun, end, hitPos, face, helper)) {
            return Optional.of(helper);
         }
      }

      return Optional.empty();
   }
}
