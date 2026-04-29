package net.portalmod.common.sorted.portal;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.faithplate.Flingable;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.init.EntityInit;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.interfaces.IDragCancelable;
import net.portalmod.core.interfaces.ITeleportLerpable;
import net.portalmod.core.math.AABBUtil;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.packet.CPlayerPortalTeleportPacket;
import net.portalmod.core.util.ModUtil;
import net.portalmod.mixins.accessors.EntityAccessor;

public class PortalEntity extends Entity implements IEntityAdditionalSpawnData {
   private final float WIDTH = 1.0F;
   private final float HEIGHT = 2.0F;
   private final float DEPTH = 0.0625F;
   private PortalEnd end;
   private Direction direction;
   private Direction up;
   private UUID gunUUID;
   private String hue;
   private int age;

   public PortalEntity(EntityType<? extends PortalEntity> type, World level) {
      super(type, level);
      this.end = PortalEnd.NONE;
      this.direction = Direction.SOUTH;
      this.up = Direction.UP;
      this.hue = "blue";
      this.age = 0;
   }

   public PortalEntity(World level) {
      super((EntityType)EntityInit.PORTAL.get(), level);
      this.end = PortalEnd.NONE;
      this.direction = Direction.SOUTH;
      this.up = Direction.UP;
      this.hue = "blue";
      this.age = 0;
   }

   public void func_70071_h_() {
      if (this.func_226278_cu_() < (double)-64.0F) {
         this.func_70076_C();
      }

      if (!this.field_70170_p.field_72995_K && this.func_70089_S() && (!this.survives() || this.hasMoved())) {
         this.func_70106_y();
      }

      if (this.field_70170_p.field_72995_K) {
         PortalPhotonParticle.createLivingParticles(this);
      }

      ++this.age;
   }

   public boolean func_70039_c(CompoundNBT nbt) {
      return false;
   }

   public boolean saveGlobal(CompoundNBT nbt) {
      this.field_70128_L = false;
      return super.func_70039_c(nbt);
   }

   private boolean hasMoved() {
      return this.func_213303_ch().func_72436_e(new Vector3d(this.field_70169_q, this.field_70167_r, this.field_70166_s)) > (double)0.0F;
   }

   public int getAge() {
      return this.age;
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      this.recalculateBoundingBox();
      this.field_70128_L = false;
      this.age = 0;
   }

   public Vec3 teleportPoint(Vec3 point) {
      if (!this.isOpen()) {
         return point;
      } else {
         Optional<PortalEntity> targetPortalOptional = this.getOtherPortal();
         if (!targetPortalOptional.isPresent()) {
            PartialPortal targetPartialPortal = ClientPortalManager.getInstance().getPartial(this.gunUUID, this.end.other());
            if (targetPartialPortal == null) {
               return point;
            } else {
               Mat4 portalToPortalMatrix = getPortalToPortalMatrix(this, targetPartialPortal);
               return point.clone().transform(portalToPortalMatrix);
            }
         } else {
            PortalEntity targetPortal = (PortalEntity)targetPortalOptional.get();
            Mat4 portalToPortalMatrix = getPortalToPortalMatrix(this, targetPortal);
            return point.clone().transform(portalToPortalMatrix);
         }
      }
   }

   public Vec3 teleportVector(Vec3 vector) {
      if (!this.isOpen()) {
         return vector;
      } else {
         Optional<PortalEntity> targetPortalOptional = this.getOtherPortal();
         if (!targetPortalOptional.isPresent()) {
            PartialPortal targetPartialPortal = ClientPortalManager.getInstance().getPartial(this.gunUUID, this.end.other());
            if (targetPartialPortal == null) {
               return vector;
            } else {
               Mat4 portalToPortalMatrix = getPortalToPortalRotationMatrix(this, targetPartialPortal);
               return vector.clone().transform(portalToPortalMatrix);
            }
         } else {
            PortalEntity targetPortal = (PortalEntity)targetPortalOptional.get();
            Mat4 portalToPortalMatrix = getPortalToPortalRotationMatrix(this, targetPortal);
            return vector.clone().transform(portalToPortalMatrix);
         }
      }
   }

   public boolean canPointEnter(Vec3 point, boolean lenient) {
      Optional<PortalEntity> targetPortalOptional = this.getOtherPortal();
      if (this.isOpen() && (targetPortalOptional.isPresent() || ClientPortalManager.getInstance().hasPartial(this.gunUUID, this.end.other()))) {
         Vec3 portalPos = new Vec3(this.func_213303_ch());
         if (lenient) {
            portalPos.add((new Vec3(this.getNormal())).negate().mul(0.3));
         }

         Vec3 distance = point.clone().sub(portalPos);
         return distance.dot(this.getNormal()) < (double)0.0F;
      } else {
         return false;
      }
   }

   private static Vector3d recursivelyTeleportEntity(Entity entity, Vector3d delta, PortalEntity justExited, int depth) {
      if (depth <= 100 && (entity.field_70170_p.field_72995_K || !(entity instanceof PlayerEntity))) {
         if (isFiniteVec(delta) && isFiniteVec(entity.func_213303_ch()) && isFiniteAABB(entity.func_174813_aQ())) {
            double MAX_TELEPORT_DELTA = (double)64.0F;
            if (delta.func_189985_c() > (double)4096.0F) {
               return delta;
            } else {
               boolean inFluid = entity.func_70090_H() || entity.func_180799_ab();
               boolean flying = entity instanceof PlayerEntity && ((PlayerEntity)entity).field_71075_bZ.field_75100_b;
               if (!inFluid && !entity.func_233570_aj_() && !flying && delta.field_72448_b > (double)0.0F && delta.field_72448_b * 0.98 - 0.08 < (double)0.0F) {
                  delta = new Vector3d(delta.field_72450_a, (double)0.0F, delta.field_72449_c);
                  Vector3d dm = entity.func_213322_ci();
                  entity.func_213293_j(dm.field_72450_a, (double)0.0F, dm.field_72449_c);
               }

               Vec3 positionToCenterVector = (new Vec3(entity.func_174813_aQ().func_189972_c())).sub(entity.func_213303_ch());
               World level = entity.field_70170_p;
               AxisAlignedBB conservativeTravelAABB = entity.func_174813_aQ().func_216361_a(delta);
               Vec3 entityOldCenter = new Vec3(entity.func_174813_aQ().func_189972_c());
               List<PortalEntity> candidatePortals = getOpenPortals(level, conservativeTravelAABB, (portalx) -> {
                  boolean correctDirection = entity.func_213322_ci().func_72430_b((new Vec3(portalx.getNormal())).to3d()) < (double)0.0F;
                  if (!correctDirection) {
                     return false;
                  } else if (portalx.canPointEnter(entityOldCenter, true)) {
                     return false;
                  } else if (!portalx.isEntityAlignedToPortal(entity)) {
                     return false;
                  } else {
                     return justExited == null || (new Vec3(portalx.func_213303_ch())).sub(justExited.func_213303_ch()).dot(justExited.getNormal()) > (double)0.0F || portalx == justExited;
                  }
               });
               if (candidatePortals.isEmpty()) {
                  return delta;
               } else {
                  Vector3d tmpDelta = ((EntityAccessor)entity).pmCollide(delta);
                  AxisAlignedBB travelAABB = entity.func_174813_aQ().func_216361_a(tmpDelta);
                  List<PortalEntity> portals = new ArrayList();

                  for(PortalEntity portal : candidatePortals) {
                     if (portal.func_174813_aQ().func_72326_a(travelAABB)) {
                        Vec3 entityPos = entityOldCenter.clone().add(tmpDelta);
                        if (portal.canPointEnter(entityPos, false)) {
                           portals.add(portal);
                        }
                     }
                  }

                  if (portals.isEmpty()) {
                     return delta;
                  } else {
                     PortalEntity portal = (PortalEntity)portals.get(0);
                     Vec3 teleportedCenter = portal.teleportPoint(new Vec3(entity.func_174813_aQ().func_189972_c()));
                     Vec3 oldTeleportedCenter = teleportedCenter.clone();
                     if (portal.getOtherPortal().isPresent() && portal.func_174811_aO().func_176740_k().func_200128_b() && ((PortalEntity)portal.getOtherPortal().get()).func_174811_aO().func_176740_k().func_176722_c()) {
                        teleportedCenter.y = ((PortalEntity)portal.getOtherPortal().get()).func_213303_ch().field_72448_b;
                     }

                     Vec3 teleportedPos = teleportedCenter.clone().sub(positionToCenterVector);
                     entity.func_226286_f_(teleportedPos.x, teleportedPos.y, teleportedPos.z);
                     entity.func_174826_a(entity.func_174813_aQ().func_191194_a((new Vec3(entity.func_174813_aQ().func_189972_c())).negate().to3d()).func_191194_a(teleportedCenter.to3d()));
                     Optional<PortalEntity> targetPortalOptional = portal.getOtherPortal();
                     if (portal.isOpen() && targetPortalOptional.isPresent()) {
                        PortalEntity targetPortal = (PortalEntity)targetPortalOptional.get();
                        double gravity = 0.08;
                        if (entity instanceof LivingEntity) {
                           gravity = ((ModifiableAttributeInstance)Objects.requireNonNull(((LivingEntity)entity).func_110148_a((Attribute)ForgeMod.ENTITY_GRAVITY.get()))).func_111126_e();
                        }

                        Vector3d dm = entity.func_213322_ci();
                        if (entity instanceof Flingable) {
                           boolean portalFling = portal.func_174811_aO().func_176740_k().func_200128_b() && targetPortal.func_174811_aO().func_176740_k().func_176722_c();
                           if (portalFling) {
                              ((Flingable)entity).setFlinging(true);
                           } else if (targetPortal.func_174811_aO().func_176740_k().func_200128_b()) {
                              ((Flingable)entity).setFlinging(false);
                           }
                        }

                        boolean doMathTrickery = portal.func_174811_aO() == Direction.UP && targetPortal.func_174811_aO() == Direction.UP && (!(entity instanceof Flingable) || !((Flingable)entity).isFlinging());
                        if (entity instanceof IDragCancelable) {
                           if (doMathTrickery) {
                              if (!((IDragCancelable)entity).pmIsCancelDrag()) {
                                 double yDelta = -Math.log((double)1.0F - -delta.field_72448_b / (gravity / 0.02)) / 0.02;
                                 delta = new Vector3d(delta.field_72450_a, (double)((int)yDelta) * -gravity, delta.field_72449_c);
                              }

                              double deltay = Math.signum(delta.field_72448_b) * Math.max(Math.abs(delta.field_72448_b), (double)0.5F);
                              double deltayResidual = Math.abs(deltay) % gravity;
                              deltay = Math.signum(deltay) * (Math.abs(deltay) - deltayResidual + (deltayResidual > gravity / (double)2.0F ? gravity : (double)0.0F));
                              delta = new Vector3d(delta.field_72450_a, deltay, delta.field_72449_c);
                              if (!((IDragCancelable)entity).pmIsCancelDrag()) {
                                 double ydm = -Math.log((double)1.0F - -dm.field_72448_b / (gravity / 0.02)) / 0.02;
                                 dm = new Vector3d(dm.field_72450_a, (double)((int)ydm) * -gravity, dm.field_72449_c);
                              }

                              double dmy = Math.signum(dm.field_72448_b) * Math.max(Math.abs(dm.field_72448_b), (double)0.5F);
                              double dmyResidual = Math.abs(dmy) % gravity;
                              dmy = Math.signum(deltay) * (Math.abs(dmy) - dmyResidual + (deltayResidual > gravity / (double)2.0F ? gravity : (double)0.0F));
                              dm = new Vector3d(dm.field_72450_a, dmy, dm.field_72449_c);
                              ((IDragCancelable)entity).pmSetCancelDrag(true);
                           } else {
                              ((IDragCancelable)entity).pmSetCancelDrag(false);
                           }
                        }

                        delta = portal.teleportVector(new Vec3(delta)).to3d();
                        entity.func_213317_d(portal.teleportVector(new Vec3(dm)).to3d());
                        if (entity instanceof DamagingProjectileEntity) {
                           DamagingProjectileEntity damagingProjectile = (DamagingProjectileEntity)entity;
                           Vec3 power = new Vec3(damagingProjectile.field_70232_b, damagingProjectile.field_70233_c, damagingProjectile.field_70230_d);
                           Vec3 teleportedPower = portal.teleportVector(power);
                           damagingProjectile.field_70232_b = teleportedPower.x;
                           damagingProjectile.field_70233_c = teleportedPower.y;
                           damagingProjectile.field_70230_d = teleportedPower.z;
                        }

                        OrthonormalBasis portalBasis = portal.getSourceBasis();
                        OrthonormalBasis targetPortalBasis = targetPortal.getDestinationBasis();
                        Mat4 changeOfBasisMatrix = portalBasis.getChangeOfBasisMatrix(targetPortalBasis);
                        Vec3 center = new Vec3(entity.func_174813_aQ().func_189972_c());
                        Vec3 eyeVec = (new Vec3(entity.func_174824_e(1.0F))).sub(center);
                        eyeVec.transform(changeOfBasisMatrix);
                        if (entity instanceof PlayerEntity && ((PlayerEntity)entity).func_175144_cb()) {
                           if (portal.func_174811_aO().func_176740_k().func_176716_d() != targetPortal.func_174811_aO().func_176740_k().func_176716_d()) {
                              CameraAnimator.getInstance().startPosAnimation(oldTeleportedCenter.clone().add(eyeVec), new Vec3(entity.func_174824_e(1.0F)), 500);
                           }

                           CameraRotator.rotate(entity, portal, targetPortal);
                        }

                        boolean shouldDisableFlying = portal.func_174811_aO().func_176740_k() == Axis.Y && targetPortal.func_174811_aO().func_176740_k() == Axis.Y && portal.func_174811_aO() == targetPortal.func_174811_aO();
                        boolean shouldStopEntity = entity instanceof PlayerEntity && ((PlayerEntity)entity).field_71075_bZ.field_75100_b && portal.func_174811_aO().func_176740_k().func_176716_d() != targetPortal.func_174811_aO().func_176740_k().func_176716_d();
                        if (entity instanceof PlayerEntity && shouldDisableFlying) {
                           ((PlayerEntity)entity).field_71075_bZ.field_75100_b = false;
                        }

                        if (targetPortal.func_174811_aO() == Direction.UP) {
                           float amount = (float)(new Vec3(targetPortal.direction)).dot(entity.func_213322_ci());
                           float target = 0.7F;
                           if (amount < target) {
                              entity.func_213317_d((new Vec3(entity.func_213322_ci())).add((new Vec3(targetPortal.direction)).mul((double)(target - amount))).to3d());
                           }
                        }

                        if (shouldStopEntity) {
                           entity.func_213317_d(new Vector3d((double)0.0F, (double)0.0F, (double)0.0F));
                        }

                        ((ITeleportable)entity).setLastUsedPortal(portal.func_145782_y());
                        ((ITeleportable2)entity).setJustUsedPortal(targetPortal.func_145782_y());
                        if (entity instanceof PortalHandler) {
                           ((PortalHandler)entity).onTeleport(portal, targetPortal);
                        }

                        if (entity instanceof PlayerEntity && entity.field_70170_p.field_72995_K) {
                           float pitch = 0.9F + (float)entity.func_213322_ci().func_72433_c() / 4.0F * 0.4F;
                           level.func_184148_a((PlayerEntity)entity, targetPortal.func_213303_ch().field_72450_a, targetPortal.func_213303_ch().field_72448_b, targetPortal.func_213303_ch().field_72449_c, (SoundEvent)SoundInit.PORTAL_TELEPORT.get(), SoundCategory.PLAYERS, 0.5F, pitch * ModUtil.randomSlightSoundPitch());
                        }

                        if (justExited == null && entity instanceof PlayerEntity && entity.field_70170_p.field_72995_K) {
                           PacketInit.INSTANCE.sendToServer(new CPlayerPortalTeleportPacket());
                        }

                        ((ITeleportLerpable)entity).setHasUsedPortal(true);
                        return recursivelyTeleportEntity(entity, delta, targetPortal, depth + 1);
                     } else {
                        return delta;
                     }
                  }
               }
            }
         } else {
            return delta;
         }
      } else {
         return delta;
      }
   }

   public static Vector3d teleportEntity(Entity entity, Vector3d delta) {
      ((ITeleportable2)entity).removeJustUsedPortal();
      return recursivelyTeleportEntity(entity, delta, (PortalEntity)null, 0);
   }

   private static boolean isFiniteVec(Vector3d v) {
      return Double.isFinite(v.field_72450_a) && Double.isFinite(v.field_72448_b) && Double.isFinite(v.field_72449_c);
   }

   private static boolean isFiniteAABB(AxisAlignedBB bb) {
      return Double.isFinite(bb.field_72340_a) && Double.isFinite(bb.field_72338_b) && Double.isFinite(bb.field_72339_c) && Double.isFinite(bb.field_72336_d) && Double.isFinite(bb.field_72337_e) && Double.isFinite(bb.field_72334_f);
   }

   public static Vector3d doFunneling(Entity entity, Vector3d delta) {
      float funnelHeight = 32.0F;
      if (entity.field_70170_p.field_72995_K && !(Boolean)PortalModConfigManager.PORTAL_FUNNELING.get()) {
         return delta;
      } else {
         boolean fastEnough = delta.field_72448_b < (double)-0.5F;
         boolean fallingMore = Math.abs(delta.field_72448_b) > Math.abs(delta.field_72450_a) && Math.abs(delta.field_72448_b) > Math.abs(delta.field_72449_c);
         if (fastEnough && fallingMore) {
            boolean moving = false;
            Supplier<Supplier<Boolean>> localPlayerMovingSupplier = () -> PortalEntityClient::isLocalPlayerMoving;
            if (entity.field_70170_p.field_72995_K) {
               moving = (Boolean)((Supplier)localPlayerMovingSupplier.get()).get();
            }

            float downDot = (float)entity.func_70676_i(1.0F).func_72430_b((new Vec3(Direction.DOWN.func_176730_m())).to3d());
            if (!(entity instanceof PlayerEntity) || !((double)downDot < (double)0.5F) && !moving) {
               Vec3 entityPos = new Vec3(entity.func_213303_ch());
               AxisAlignedBB travelAABB = entity.func_174813_aQ().func_216361_a(delta).func_72321_a((double)0.0F, (double)-32.0F, (double)0.0F).func_72321_a((double)3.0F, (double)0.0F, (double)3.0F).func_72321_a((double)-3.0F, (double)0.0F, (double)-3.0F);
               List<PortalEntity> portals = getOpenPortals(entity.field_70170_p, travelAABB, (portalx) -> portalx.func_174811_aO() == Direction.UP && portalx.func_213303_ch().field_72448_b < entityPos.y);
               if (portals.isEmpty()) {
                  return delta;
               } else {
                  PortalEntity portal = (PortalEntity)portals.stream().reduce((portal1, portal2) -> {
                     Vec3 portal1Pos = new Vec3(portal1.func_213303_ch());
                     Vec3 portal2Pos = new Vec3(portal2.func_213303_ch());
                     Vec3 flatEntityPos = entityPos.clone().mul((double)1.0F, (double)0.0F, (double)1.0F);
                     Vec3 flatPortal1Pos = portal1Pos.clone().mul((double)1.0F, (double)0.0F, (double)1.0F);
                     Vec3 flatPortal2Pos = portal2Pos.clone().mul((double)1.0F, (double)0.0F, (double)1.0F);
                     double hDistance1 = flatPortal1Pos.clone().sub(flatEntityPos).magnitude();
                     double hDistance2 = flatPortal2Pos.clone().sub(flatEntityPos).magnitude();
                     double vDistance1 = portal1Pos.y - entityPos.y;
                     double vDistance2 = portal2Pos.y - entityPos.y;
                     if (hDistance2 == hDistance1) {
                        return vDistance2 < vDistance1 ? portal2 : portal1;
                     } else {
                        return hDistance2 < hDistance1 ? portal2 : portal1;
                     }
                  }).get();
                  RayTraceContext rayContext = new RayTraceContext(entity.func_174824_e(1.0F), portal.func_213303_ch(), BlockMode.COLLIDER, FluidMode.ANY, entity);
                  BlockRayTraceResult rayResult = entity.field_70170_p.func_217299_a(rayContext);
                  if (rayResult.func_216346_c() != Type.MISS) {
                     return delta;
                  } else {
                     Vec3 portalPos = new Vec3(portal.func_213303_ch());
                     Vec3 relativeEntityPos = entityPos.clone().sub(portalPos.clone());
                     Vec3 flatRelativeEntityPos = relativeEntityPos.clone().mul((double)1.0F, (double)0.0F, (double)1.0F);
                     float coneRadius = (float)relativeEntityPos.y * 0.2F;
                     boolean isInCone = relativeEntityPos.y < (double)32.0F && relativeEntityPos.y > (double)0.0F && flatRelativeEntityPos.magnitude() < (double)coneRadius;
                     if (!isInCone) {
                        return delta;
                     } else {
                        float currentHeight = (float)relativeEntityPos.y;
                        float startHeight = Math.min(currentHeight + entity.field_70143_R, 32.0F);
                        float progress = 1.0F - currentHeight / startHeight;
                        float distanceFactor = (float)((double)1.0F - Math.exp((double)(-2.0F * progress)));
                        Vec3 funnelAcceleration = flatRelativeEntityPos.clone().negate().mul((double)distanceFactor);
                        return delta.func_178787_e(funnelAcceleration.to3d());
                     }
                  }
               }
            } else {
               return delta;
            }
         } else {
            return delta;
         }
      }
   }

   public void pushEntities() {
      AxisAlignedBB aabb = this.func_174813_aQ();
      List<Entity> entities = this.field_70170_p.func_175674_a((Entity)null, aabb, (entity) -> !entity.func_175149_v() && !(entity instanceof PortalEntity));
      if (this.field_70170_p.field_72995_K) {
         entities = (List)entities.stream().filter((entity) -> entity instanceof PlayerEntity && ((PlayerEntity)entity).func_175144_cb()).collect(Collectors.toList());
      }

      entities.forEach((entity) -> entity.func_213317_d(entity.func_213322_ci().func_178787_e((new Vec3(this.func_174811_aO())).mul(0.2).to3d())));
   }

   public Vec3 projectPointOnPortalSurface(Vec3 point) {
      Vec3 portalToPoint = point.sub(this.func_174813_aQ().func_189972_c());
      Vec3 n = new Vec3(this.func_174811_aO().func_176730_m());
      Vec3 y = new Vec3(this.getUpVector().func_176730_m());
      Vec3 x = y.clone().cross(n);
      return new Vec3(portalToPoint.dot(x), portalToPoint.dot(y), (double)0.0F);
   }

   public boolean arePointsAlignedToPortal(Vec3... points) {
      AxisAlignedBB bounds = new AxisAlignedBB((double)-0.5F, (double)-1.0F, (double)-1.0F, (double)0.5F, (double)1.0F, (double)1.0F);
      return Arrays.stream(points).allMatch((point) -> bounds.func_72318_a(this.projectPointOnPortalSurface(point).to3d()));
   }

   public boolean isEntityAlignedToPortal(Entity entity) {
      return this.arePointsAlignedToPortal(new Vec3(entity.func_174813_aQ().func_189972_c().func_178787_e(entity.func_213322_ci())));
   }

   public static boolean shouldSkipCollision(IBlockReader blockReader, BlockPos pos, ISelectionContext selectionContext) {
      Entity entity = selectionContext.getEntity();
      if (entity != null && !blockReader.func_180495_p(pos).func_196958_f()) {
         if (((ITeleportable2)entity).hasJustUsedPortal()) {
            PortalEntity portal = (PortalEntity)entity.field_70170_p.func_73045_a(((ITeleportable2)entity).getJustUsedPortal());
            if (portal == null) {
               return false;
            }

            if ((new Vec3(pos)).add((double)0.5F).sub(portal.func_213303_ch()).dot(portal.getNormal()) <= (double)0.0F) {
               return true;
            }
         }

         AxisAlignedBB travelAABB = entity.func_174813_aQ().func_216361_a(entity.func_213322_ci());
         boolean noneOfTheServersBusiness = !entity.field_70170_p.field_72995_K && entity instanceof PlayerEntity;
         AxisAlignedBB actualAABB = noneOfTheServersBusiness ? travelAABB.func_186662_g((double)3.0F) : travelAABB;
         List<PortalEntity> portals = getOpenPortals(entity.field_70170_p, actualAABB, (portalx) -> {
            Vec3 center = (new Vec3(pos)).blockCenter();
            Vec3 portalBlockCenter = (new Vec3(portalx.func_213303_ch())).blockCenter();
            boolean isBlockBehind = (float)center.clone().sub(portalBlockCenter).dot(portalx.getNormal()) < 0.0F;
            boolean isBlockSupportingPortal = portalx.getBlocksBehind().contains(pos);
            boolean entityAligned = portalx.isEntityAlignedToPortal(entity);
            boolean velocityAffine = entity.func_213322_ci().func_72430_b(new Vector3d(portalx.getNormal())) <= (double)0.0F;
            boolean intersect = entity.func_174813_aQ().func_72326_a(portalx.func_174813_aQ());
            boolean hasJustUsedPortal = ((ITeleportable2)entity).hasJustUsedPortal();
            boolean justUsedPortalInFront = false;
            if (hasJustUsedPortal) {
               PortalEntity justUsedPortal = (PortalEntity)entity.field_70170_p.func_73045_a(((ITeleportable2)entity).getJustUsedPortal());
               if (justUsedPortal != null) {
                  justUsedPortalInFront = (new Vec3(portalx.func_213303_ch())).sub(justUsedPortal.func_213303_ch()).dot(justUsedPortal.getNormal()) > (double)0.0F;
               }
            }

            return (isBlockBehind || isBlockSupportingPortal) && entityAligned && (velocityAffine || intersect) && (!hasJustUsedPortal || justUsedPortalInFront);
         });
         return !portals.isEmpty();
      } else {
         return false;
      }
   }

   public static VoxelShape getCollisionShape(Entity entity) {
      Vector3d delta = entity.func_213322_ci();
      if (entity.func_233570_aj_()) {
         delta = delta.func_216372_d((double)1.0F, (double)0.0F, (double)1.0F);
      }

      AxisAlignedBB travelAABB = entity.func_174813_aQ().func_216361_a(delta);
      List<PortalEntity> portals = getOpenPortals(entity.field_70170_p, travelAABB, (portalx) -> true);
      if (portals.isEmpty()) {
         return VoxelShapes.func_197880_a();
      } else {
         Optional<PortalEntity> portalOptional = portals.stream().reduce((o, n) -> n.func_213303_ch().func_178788_d(entity.func_213303_ch()).func_72433_c() < o.func_213303_ch().func_178788_d(entity.func_213303_ch()).func_72433_c() ? n : o);
         PortalEntity portal = (PortalEntity)portalOptional.get();
         Vec3 origin = (new Vec3(portal.func_213303_ch())).add((new Vec3(portal.getNormal())).mul(0.001));
         Vec3 normal = new Vec3(portal.func_174811_aO().func_176730_m());
         Vec3 right = new Vec3(portal.getRightVector().func_176730_m());
         Vec3 up = new Vec3(portal.getUpVector().func_176730_m());
         VoxelShape boundingBox = VoxelShapes.func_197881_a(new AxisAlignedBB(origin.clone().sub(normal).sub(right.clone().mul((double)1.5F)).sub(up.clone().mul((double)2.0F)).to3d(), origin.clone().sub(normal.clone().mul(0.002)).add(right.clone().mul((double)1.5F)).add(up.clone().mul((double)2.0F)).to3d()));
         VoxelShape carving = VoxelShapes.func_197881_a(new AxisAlignedBB(origin.clone().sub(normal).sub(right.clone().mul((double)0.5F)).sub(up.clone().mul((double)1.0F)).to3d(), origin.clone().sub(normal.clone().mul(0.002)).add(right.clone().mul((double)0.5F)).add(up.clone().mul((double)1.0F)).to3d()));
         VoxelShape shape = VoxelShapes.func_197878_a(boundingBox, carving, IBooleanFunction.field_223234_e_);
         return shape;
      }
   }

   public List<BlockPos> getBlocksBehind() {
      AxisAlignedBB aabb = this.func_174813_aQ().func_186664_h(0.001).func_191194_a((new Vec3(this.func_174811_aO().func_176734_d().func_176730_m())).mul((double)0.0625F).to3d());
      return AABBUtil.getBlocksWithin(aabb);
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null && this.up != null) {
         Vec3 baseVertex = new Vec3((double)0.0F);
         Vec3 endVertex = new Vec3((double)1.0F, (double)2.0F, (double)0.0625F);
         Mat4 matrix = setupMatrix(this.direction, this.up, this.getPivotPoint());
         baseVertex.transform(matrix);
         endVertex.transform(matrix);
         this.func_174826_a(new AxisAlignedBB(baseVertex.to3d(), endVertex.to3d()));
      }
   }

   public Vector3d getPivotPoint() {
      return this.func_213303_ch().func_178787_e((new Vec3(this.direction.func_176734_d().func_176730_m())).mul(0.501).to3d());
   }

   public static void setupMatrix(MatrixStack matrix, Direction direction, Direction upVector, Vector3d center) {
      int i = direction.func_96559_d() * -1;
      float yRot;
      if (direction.func_176740_k().func_176722_c()) {
         yRot = -direction.func_185119_l();
      } else {
         yRot = -upVector.func_176734_d().func_185119_l();
         if (direction.func_176743_c() == AxisDirection.NEGATIVE) {
            yRot += 180.0F;
         }
      }

      matrix.func_227861_a_(center.field_72450_a, center.field_72448_b, center.field_72449_c);
      matrix.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(yRot));
      matrix.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_((float)(i * 90)));
      matrix.func_227861_a_((double)-0.5F, (double)-1.0F, (double)0.5F);
   }

   public static Mat4 setupMatrix(Direction direction, Direction upVector, Vector3d center) {
      Mat4 matrix = Mat4.identity();
      int i = direction.func_96559_d() * -1;
      float yRot;
      if (direction.func_176740_k().func_176722_c()) {
         yRot = -direction.func_185119_l();
      } else {
         yRot = -upVector.func_176734_d().func_185119_l();
         if (direction.func_176743_c() == AxisDirection.NEGATIVE) {
            yRot += 180.0F;
         }
      }

      matrix.translate(center.field_72450_a, center.field_72448_b, center.field_72449_c);
      matrix.rotateDeg(Vector3f.field_229181_d_, yRot);
      matrix.rotateDeg(Vector3f.field_229179_b_, (float)(i * 90));
      matrix.translate((double)-0.5F, (double)-1.0F, (double)0.5F);
      return matrix;
   }

   public Vector3f getNormal() {
      return (new Vec3(this.direction.func_176730_m())).to3f();
   }

   public float getWallAttachmentDistance(ActiveRenderInfo camera) {
      double distance = camera.func_216785_c().func_178788_d(this.func_213303_ch()).func_72433_c();
      return (float)Math.min(1.0E-4 + Math.max(0.0010204081632653062 * (distance - (double)2.0F), (double)0.0F), 0.1);
   }

   public OrthonormalBasis getSourceBasis() {
      return new OrthonormalBasis((new Vec3(this.getUpVector())).cross(new Vec3(this.func_174811_aO())), new Vec3(this.getUpVector()));
   }

   public OrthonormalBasis getDestinationBasis() {
      return new OrthonormalBasis((new Vec3(this.getUpVector())).cross(new Vec3(this.func_174811_aO().func_176734_d())), new Vec3(this.getUpVector()));
   }

   public static Mat4 getPortalToPortalRotationMatrix(PortalEntity portal, PortalEntity otherPortal) {
      OrthonormalBasis srcBasis = portal.getSourceBasis();
      OrthonormalBasis dstBasis = otherPortal.getDestinationBasis();
      return srcBasis.getChangeOfBasisMatrix(dstBasis);
   }

   public static Mat4 getPortalToPortalRotationMatrix(PortalEntity portal, PartialPortal otherPortal) {
      OrthonormalBasis srcBasis = portal.getSourceBasis();
      OrthonormalBasis dstBasis = otherPortal.getDestinationBasis();
      return srcBasis.getChangeOfBasisMatrix(dstBasis);
   }

   public static Mat4 getPortalToPortalMatrix(PortalEntity portal, PortalEntity otherPortal) {
      Vec3 thisPos = new Vec3(portal.func_213303_ch());
      Vec3 otherPos = new Vec3(otherPortal.func_213303_ch());
      return Mat4.identity().mul(Mat4.createTranslation(otherPos.x, otherPos.y, otherPos.z)).mul(getPortalToPortalRotationMatrix(portal, otherPortal)).mul(Mat4.createTranslation(-thisPos.x, -thisPos.y, -thisPos.z));
   }

   public static Mat4 getPortalToPortalMatrix(PortalEntity portal, PartialPortal otherPortal) {
      Vec3 thisPos = new Vec3(portal.func_213303_ch());
      Vec3 otherPos = new Vec3(otherPortal.getPosition());
      return Mat4.identity().mul(Mat4.createTranslation(otherPos.x, otherPos.y, otherPos.z)).mul(getPortalToPortalRotationMatrix(portal, otherPortal)).mul(Mat4.createTranslation(-thisPos.x, -thisPos.y, -thisPos.z));
   }

   public void onReplaced() {
      if (!this.field_70170_p.field_72995_K) {
         ((ServerWorld)this.field_70170_p).removeEntity(this, false);
         this.getOtherPortal().ifPresent(PortalEntity::pushEntities);
      }
   }

   public boolean isOpen() {
      return this.field_70170_p.field_72995_K ? ClientPortalManager.getInstance().hasPartial(this.gunUUID, this.end.other()) : PortalManager.getInstance().has(this.gunUUID, this.end.other());
   }

   public void setEnd(PortalEnd end) {
      this.end = end;
   }

   public PortalEnd getEnd() {
      return this.end;
   }

   public void setGunUUID(UUID gunUUID) {
      this.gunUUID = gunUUID;
   }

   public UUID getGunUUID() {
      return this.gunUUID;
   }

   public void setUpVector(Direction upVector) {
      this.up = upVector;
   }

   public Direction getUpVector() {
      return this.up;
   }

   public Direction getRightVector() {
      Vec3 n = new Vec3(this.func_174811_aO().func_176730_m());
      Vec3 y = new Vec3(this.getUpVector().func_176730_m());
      Vec3 x = y.clone().cross(n);
      return Direction.func_218383_a((int)x.x, (int)x.y, (int)x.z);
   }

   public void setDirection(@Nonnull Direction direction) {
      this.direction = direction;
      if (direction.func_176740_k().func_176722_c()) {
         this.field_70125_A = 0.0F;
         this.field_70177_z = (float)(this.direction.func_176736_b() * 90);
      } else {
         this.field_70125_A = (float)(-90 * direction.func_176743_c().func_179524_a());
         this.field_70177_z = 0.0F;
      }

      this.field_70127_C = this.field_70125_A;
      this.field_70126_B = this.field_70177_z;
   }

   public boolean survives() {
      if (this.field_70170_p.field_72995_K) {
         return true;
      } else {
         Mat4 toAbsolute = this.getSourceBasis().getChangeOfBasisFromCanonicalMatrix();
         VoxelShape collision = PortalPlacer.getCollision(this.field_70170_p, this.direction, new Vec3(this.func_213303_ch()), toAbsolute, false);
         return !PortalPlacer.portalCollides(this.direction, this.func_174813_aQ().func_186664_h(0.001), collision);
      }
   }

   public void func_70107_b(double x, double y, double z) {
      this.func_226288_n_(x, y, z);
   }

   private boolean equals(PortalEntity portal) {
      return portal.gunUUID.equals(this.gunUUID) && portal.end == this.end;
   }

   public static List<PortalEntity> getPortals(World level, Vector3d pos, float size, Predicate<PortalEntity> predicate) {
      return getPortals(level, (new AxisAlignedBB(pos, pos)).func_186662_g((double)size), predicate);
   }

   public static List<PortalEntity> getPortals(World level, AxisAlignedBB bb, Predicate<PortalEntity> predicate) {
      return level.func_175647_a(PortalEntity.class, bb, predicate);
   }

   public static List<PortalEntity> getOpenPortals(World level, AxisAlignedBB bb, Predicate<PortalEntity> predicate) {
      List<PortalEntity> portals = new ArrayList();
      Map<UUID, PortalPair> portalPairs = PortalManager.getInstance().getPortalMap();
      if (level.field_72995_K) {
         portalPairs = ClientPortalManager.getInstance().getPortalMap();
      }

      for(PortalEntity portal : (List)portalPairs.values().stream().map((pair) -> Lists.newArrayList(new PortalEntity[]{pair.get(PortalEnd.PRIMARY), pair.get(PortalEnd.SECONDARY)})).flatMap(Collection::stream).collect(Collectors.toList())) {
         if (portal != null && portal.func_174813_aQ().func_72326_a(bb) && portal.isOpen() && predicate.test(portal)) {
            portals.add(portal);
         }
      }

      return portals;
   }

   public Optional<PortalEntity> getOtherPortal() {
      return this.field_70170_p.field_72995_K ? Optional.ofNullable(ClientPortalManager.getInstance().get(this.gunUUID, this.end.other())) : Optional.ofNullable(PortalManager.getInstance().get(this.gunUUID, this.end.other()));
   }

   public void onRemovedFromWorld() {
      super.onRemovedFromWorld();
      if (!this.field_70170_p.field_72995_K) {
         boolean isPortalStillUsed = PortalManager.getInstance().get(this.gunUUID, this.end) == this;
         if (!PortalManager.getInstance().unloadingChunk && isPortalStillUsed) {
            PortalManager.getInstance().remove(this.gunUUID, this);
            PacketInit.INSTANCE.send(PacketDistributor.ALL.noArg(), new SForgetPortalPacket(this.gunUUID, this.end));
            this.field_70170_p.func_184148_a((PlayerEntity)null, this.func_213303_ch().field_72450_a, this.func_213303_ch().field_72448_b, this.func_213303_ch().field_72449_c, (SoundEvent)SoundInit.PORTAL_CLOSE.get(), SoundCategory.NEUTRAL, 0.8F, ModUtil.randomSlightSoundPitch());
         }

      }
   }

   public boolean func_241849_j(Entity entity) {
      return false;
   }

   public void func_70024_g(double x, double y, double z) {
   }

   public boolean func_70097_a(DamageSource source, float amount) {
      if (!this.field_70170_p.field_72995_K && this.func_70089_S()) {
         Entity attacker = source.func_76346_g();
         if (source instanceof EntityDamageSource && attacker instanceof LivingEntity) {
            boolean hitWithWrench = WrenchItem.hitWithWrench((LivingEntity)attacker);
            boolean isCreative = attacker instanceof PlayerEntity && ((PlayerEntity)attacker).func_184812_l_();
            if (hitWithWrench || isCreative && !this.isOpen()) {
               this.func_70106_y();
               return true;
            }
         }

         return source == DamageSource.field_76380_i && super.func_70097_a(source, amount);
      } else {
         return false;
      }
   }

   public Direction func_174811_aO() {
      return this.direction;
   }

   public Vec3 getPlanePos() {
      Vec3 posToPlane = (new Vec3(this.direction.func_176730_m())).mul((double)9.999999E-4F);
      return (new Vec3(this.func_213303_ch())).add(posToPlane);
   }

   public void func_213315_a(MoverType moverType, Vector3d delta) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L && delta.func_189985_c() > (double)0.0F) {
         this.func_70106_y();
      }

   }

   public float func_184229_a(Rotation rotation) {
      return 0.0F;
   }

   public float func_184217_a(Mirror mirror) {
      return 0.0F;
   }

   public void func_241841_a(ServerWorld level, LightningBoltEntity lightning) {
   }

   public void func_213323_x_() {
   }

   public boolean func_70112_a(double d) {
      return true;
   }

   public static boolean shouldRenderBlockOverlay(World level, BlockPos pos) {
      return getOpenPortals(level, (new AxisAlignedBB(pos)).func_186662_g(0.1), (portal) -> portal.getBlocksBehind().contains(pos)).isEmpty();
   }

   protected void func_70088_a() {
   }

   protected float func_213316_a(Pose p_213316_1_, EntitySize p_213316_2_) {
      return 0.0F;
   }

   public ActionResultType func_184230_a(PlayerEntity player, Hand hand) {
      ItemStack handItem = player.func_184586_b(hand);
      if (handItem.func_77973_b() == Items.field_151057_cb) {
         if (handItem.func_82837_s()) {
            if (!player.field_70170_p.field_72995_K && this.func_70089_S()) {
               this.func_200203_b(handItem.func_200301_q());
               handItem.func_190918_g(1);
            }

            return ActionResultType.func_233537_a_(player.field_70170_p.field_72995_K);
         } else {
            return ActionResultType.PASS;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   public void setHue(String hue) {
      this.hue = hue;
   }

   public String getColor() {
      return this.hue;
   }

   public ItemStack getPickedResult(RayTraceResult target) {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_186854_a("gunUUID", this.gunUUID);
      ItemStack item = new ItemStack((IItemProvider)ItemInit.PORTALGUN.get());
      item.func_77982_d(nbt);
      return item;
   }

   public IPacket<?> func_213297_N() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void writeSpawnData(PacketBuffer buffer) {
      buffer.func_179252_a(this.gunUUID).func_179249_a(this.end).writeByte((byte)this.up.func_176745_a()).writeByte((byte)this.direction.func_176745_a()).writeByte(DyeColor.valueOf(this.hue.toUpperCase()).func_196059_a());
   }

   public void readSpawnData(PacketBuffer buffer) {
      this.gunUUID = buffer.func_179253_g();
      this.end = (PortalEnd)buffer.func_179257_a(PortalEnd.class);
      this.up = Direction.func_82600_a(buffer.readByte());
      this.setDirection(Direction.func_82600_a(buffer.readByte()));
      this.hue = DyeColor.func_196056_a(buffer.readByte()).func_176762_d();
      this.recalculateBoundingBox();
      ClientPortalManager.getInstance().put(this.gunUUID, this.end, this);
   }

   public void func_213281_b(CompoundNBT nbt) {
      nbt.func_74778_a("end", this.end.func_176610_l());
      nbt.func_186854_a("gunUUID", this.gunUUID);
      nbt.func_74778_a("up", this.up.func_176610_l().toUpperCase());
      nbt.func_74778_a("facing", this.direction.func_176610_l().toUpperCase());
      nbt.func_74778_a("hue", this.hue);
   }

   public void func_70037_a(CompoundNBT nbt) {
      this.end = PortalEnd.valueOf(nbt.func_74779_i("end").toUpperCase());
      this.gunUUID = nbt.func_186857_a("gunUUID");
      this.up = Direction.valueOf(nbt.func_74779_i("up"));
      this.setDirection(Direction.valueOf(nbt.func_74779_i("facing")));
      this.hue = nbt.func_74779_i("hue");
   }
}
