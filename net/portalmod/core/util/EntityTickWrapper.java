package net.portalmod.core.util;

import java.util.Deque;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3d;
import net.portalmod.core.interfaces.ITeleportLerpable;

public class EntityTickWrapper {
   public static void wrapTick(Entity entity, Consumer<Entity> action) {
      action.accept(entity);
      if (!entity.field_70170_p.field_72995_K && !(entity instanceof PlayerEntity)) {
         Deque<Tuple<Vector3d, Vector3d>> lerpPositions = ((ITeleportLerpable)entity).getLerpPositions();
         lerpPositions.add(new Tuple(ModUtil.getOldPos(entity), entity.func_213303_ch()));

         while(lerpPositions.size() > 10) {
            lerpPositions.removeFirst();
         }
      }

   }
}
