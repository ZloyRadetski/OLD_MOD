package net.portalmod.mixins.level;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.TrackedEntity;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.mixins.accessors.ChunkManagerAccessor;
import net.portalmod.mixins.accessors.ChunkManagerAccessor2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"net.minecraft.world.server.ChunkManager$EntityTracker"}
)
public abstract class EntityTrackerMixin {
   @Shadow
   @Final
   private Entity field_219403_c;
   @Shadow
   @Final
   private TrackedEntity field_219402_b;
   @Shadow
   @Final
   private Set<ServerPlayerEntity> field_219406_f;

   @Shadow
   protected abstract int func_229843_b_();

   @Inject(
      method = {"updatePlayer"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pmLoadEntitiesThroughPortal(ServerPlayerEntity player, CallbackInfo info) {
      info.cancel();
      ChunkManager chunkManagerSuper = ((ServerChunkProvider)this.field_219403_c.field_70170_p.func_72863_F()).field_217237_a;
      int viewDistance = ((ChunkManagerAccessor)chunkManagerSuper).pmGetViewDistance();
      Int2ObjectMap<EntityTrackerMixin> entityMap = ((ChunkManagerAccessor)chunkManagerSuper).pmGetEntityMap();
      if (player != this.field_219403_c) {
         Vector3d distance = player.func_213303_ch().func_178788_d(this.field_219402_b.func_219456_b());
         int radius = Math.min(this.func_229843_b_(), (viewDistance - 1) * 16);
         boolean isInRadius = this.pmIsInRadius(distance, radius);
         if (this.field_219403_c.func_174827_a(player)) {
            ObjectIterator var9 = entityMap.values().iterator();

            while(var9.hasNext()) {
               EntityTrackerMixin portalTracker = (EntityTrackerMixin)var9.next();
               if (portalTracker.field_219403_c instanceof PortalEntity && ((PortalEntity)portalTracker.field_219403_c).isOpen()) {
                  PortalEntity portal = (PortalEntity)portalTracker.field_219403_c;
                  Vector3d distanceToPortal = player.func_213303_ch().func_178788_d(portalTracker.field_219402_b.func_219456_b());
                  int playerRadius = (viewDistance - 1) * 16;
                  if (this.pmIsInRadius(distanceToPortal, playerRadius)) {
                     Optional<PortalEntity> otherPortalOptional = portal.getOtherPortal();
                     if (otherPortalOptional.isPresent()) {
                        PortalEntity otherPortal = (PortalEntity)otherPortalOptional.get();
                        Vector3d teleportedPos = player.func_213303_ch().func_178788_d(portal.func_213303_ch()).func_178787_e(otherPortal.func_213303_ch());
                        Vector3d teleportedDistance = teleportedPos.func_178788_d(this.field_219402_b.func_219456_b());
                        if (this.pmIsInRadius(teleportedDistance, radius)) {
                           isInRadius = true;
                           break;
                        }
                     }
                  }
               }
            }
         } else {
            isInRadius = false;
         }

         if (isInRadius) {
            boolean isInCheckerboard = this.field_219403_c.field_98038_p;
            if (!isInCheckerboard) {
               ChunkPos chunkpos = new ChunkPos(this.field_219403_c.field_70176_ah, this.field_219403_c.field_70164_aj);
               ChunkHolder chunkholder = ((ChunkManagerAccessor)chunkManagerSuper).pmGetVisibleChunkIfPresent(chunkpos.func_201841_a());
               if (chunkholder != null && chunkholder.func_219298_c() != null) {
                  isInCheckerboard = ChunkManagerAccessor.pmCheckerboardDistance(chunkpos, player, false) <= viewDistance;
                  if (!isInCheckerboard) {
                     ObjectIterator var25 = entityMap.values().iterator();

                     while(var25.hasNext()) {
                        EntityTrackerMixin portalTracker = (EntityTrackerMixin)var25.next();
                        if (portalTracker.field_219403_c instanceof PortalEntity && ((PortalEntity)portalTracker.field_219403_c).isOpen()) {
                           PortalEntity portal = (PortalEntity)portalTracker.field_219403_c;
                           Vector3d distanceToPortal = player.func_213303_ch().func_178788_d(portalTracker.field_219402_b.func_219456_b());
                           int playerRadius = (viewDistance - 1) * 16;
                           if (this.pmIsInRadius(distanceToPortal, playerRadius)) {
                              Optional<PortalEntity> otherPortalOptional = portal.getOtherPortal();
                              if (otherPortalOptional.isPresent()) {
                                 PortalEntity otherPortal = (PortalEntity)otherPortalOptional.get();
                                 Vector3d teleportedPos = player.func_213303_ch().func_178788_d(portal.func_213303_ch()).func_178787_e(otherPortal.func_213303_ch());
                                 int x = MathHelper.func_76128_c(teleportedPos.field_72450_a / (double)16.0F);
                                 int z = MathHelper.func_76128_c(teleportedPos.field_72449_c / (double)16.0F);
                                 if (ChunkManagerAccessor2.pmCheckerboardDistance(chunkpos, x, z) <= viewDistance) {
                                    isInCheckerboard = true;
                                    break;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (isInCheckerboard && this.field_219406_f.add(player)) {
               this.field_219402_b.func_219455_b(player);
            }
         } else if (this.field_219406_f.remove(player)) {
            this.field_219402_b.func_219454_a(player);
         }

      }
   }

   private boolean pmIsInRadius(Vector3d vector, int radius) {
      return vector.field_72450_a >= (double)(-radius) && vector.field_72450_a <= (double)radius && vector.field_72449_c >= (double)(-radius) && vector.field_72449_c <= (double)radius;
   }
}
