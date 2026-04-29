package net.portalmod.common.sorted.faithplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.common.sorted.antline.indicator.IndicatorActivated;
import net.portalmod.common.sorted.antline.indicator.IndicatorInfo;
import net.portalmod.common.sorted.portal.VolatilePortalHelperManager;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;

public class FaithPlateTileEntity extends TileEntity implements ITickableTileEntity, IndicatorActivated {
   private boolean enabled;
   private boolean indicatorControlled;
   private boolean override;
   private Vector3d targetPos;
   private Direction targetFace;
   private Float height;
   private int cooldown;
   public static int COOLDOWN_DURATION = 10;
   private static final HashMap<PlayerEntity, FaithPlateTileEntity> PLATE_PER_PLAYER = new HashMap();
   private PlayerEntity configuringPlayer;
   private static final VoxelShapeGroup TRIGGER = (new VoxelShapeGroup.Builder()).add((double)3.0F, (double)16.0F, (double)3.0F, (double)13.0F, (double)17.0F, (double)13.0F).build();

   public FaithPlateTileEntity(TileEntityType<?> type) {
      super(type);
      this.enabled = false;
      this.indicatorControlled = false;
      this.override = false;
      this.cooldown = 0;
   }

   public FaithPlateTileEntity() {
      this((TileEntityType)TileEntityTypeInit.FAITHPLATE.get());
   }

   public void func_73660_a() {
      if (this.field_145850_b != null) {
         if (!this.field_145850_b.field_72995_K && this.targetPos != null) {
            Vec3 helperPosition = (new Vec3(this.targetPos)).add((Vector3i)this.field_174879_c).add((double)0.5F).add((new Vec3(this.targetFace)).mul((double)0.5F));
            VolatilePortalHelperManager.getInstance().addVolatilePortalHelper(this.field_145850_b, helperPosition, this.targetFace, 1.0F);
         }

         IndicatorInfo indicatorInfo = this.checkIndicators(this.func_195044_w(), this.func_145831_w(), this.func_174877_v());
         this.indicatorControlled = indicatorInfo.hasIndicators;
         if (this.indicatorControlled) {
            this.override = indicatorInfo.allIndicatorsActivated;
         } else {
            this.override = this.enabled;
         }

         if (this.cooldown > 0) {
            --this.cooldown;
         }

         if (this.targetPos != null && this.targetFace != null && this.override) {
            if (this.cooldown <= 0) {
               for(Entity entity : this.field_145850_b.func_217357_a(LivingEntity.class, this.getTrigger())) {
                  if (!entity.func_184218_aH()) {
                     if (entity instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity)entity;
                        if (player.field_71075_bZ.field_75100_b) {
                           continue;
                        }
                     }

                     this.launchEntity(entity);
                  }
               }

            }
         }
      }
   }

   private void launchEntity(Entity entity) {
      if (this.field_145850_b != null) {
         Vector3d targetPos = this.getTargetPos();
         double offset = !this.getTargetFace().func_176740_k().func_176722_c() && this.getTargetFace() != Direction.DOWN ? (double)0.0F : (double)-0.25F - (double)0.5F * (double)entity.func_213302_cg();
         Vec3 target = (new Vec3(targetPos)).add((double)0.5F).add((new Vec3(this.getTargetFace().func_176730_m())).mul((double)0.5F));
         Vec3 relativeTarget = target.clone().add((Vector3i)this.func_174877_v()).sub(entity.func_213303_ch()).add((double)0.0F, offset, (double)0.0F);
         if (targetPos.func_82615_a() == (double)0.0F && targetPos.func_82616_c() == (double)0.0F) {
            relativeTarget = (new Vec3(this.func_174877_v())).add((double)0.5F, (double)1.0F, (double)0.5F).sub(entity.func_213303_ch());
         }

         FaithPlateParabola parabola = new FaithPlateParabola(relativeTarget.to3d(), (double)this.height);
         double angle = parabola.getAngle();
         double velocity = parabola.getVelocity();
         double rotation = parabola.getRotation();
         entity.func_213317_d(new Vector3d(velocity * Math.cos(angle) * Math.cos(rotation), velocity * Math.sin(angle), velocity * Math.cos(angle) * Math.sin(rotation)));
         entity.func_226284_e_(false);
         ((Flingable)entity).setFlinging(true);
         this.cooldown = COOLDOWN_DURATION;
         if (!this.field_145850_b.field_72995_K) {
            if (entity.func_184186_bw() && !(entity instanceof PlayerEntity)) {
               PacketInit.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new SFaithPlateLaunchPacket(this.func_174877_v()));
            }
         } else {
            PacketInit.INSTANCE.sendToServer(new CFaithPlateLaunchPacket(this.func_174877_v()));
         }

      }
   }

   public void startConfiguration(ServerPlayerEntity player) {
      this.configuringPlayer = player;
      PLATE_PER_PLAYER.put(player, this);
      PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SFaithPlateStartConfigPacket(this.func_174877_v()));
   }

   public void endConfiguration() {
      endConfigurationForPlayer(this.configuringPlayer);
   }

   public static void endConfigurationForPlayer(PlayerEntity player) {
      if (PLATE_PER_PLAYER.containsKey(player)) {
         ((FaithPlateTileEntity)PLATE_PER_PLAYER.get(player)).configuringPlayer = null;
      }

      PLATE_PER_PLAYER.remove(player);
   }

   public static boolean isPlayerConfiguring(PlayerEntity player) {
      return PLATE_PER_PLAYER.containsKey(player);
   }

   public boolean isBeingConfigured() {
      return this.configuringPlayer != null;
   }

   public AxisAlignedBB getTrigger() {
      BlockState state = this.func_195044_w();
      VoxelShapeGroup triggerTransformed = TRIGGER.clone();
      if (state.func_177229_b(FaithPlateBlock.FACE) == FaithPlateBlock.Face.WALL) {
         Mat4 matrix = Mat4.identity().translate(new Vec3((double)0.5F)).rotateDeg(Vector3f.field_229181_d_, -((Direction)state.func_177229_b(FaithPlateBlock.FACING)).func_185119_l()).rotateDeg(Vector3f.field_229179_b_, 90.0F).translate(new Vec3((double)-0.5F));
         triggerTransformed.transform(matrix);
      }

      AxisAlignedBB aabb = triggerTransformed.getShape().func_197752_a();
      FaithPlateBlock block = (FaithPlateBlock)state.func_177230_c();
      BlockPos mainPosition = block.getMainPosition(state, this.func_174877_v());
      return VoxelShapes.func_197872_a(VoxelShapes.func_197881_a(aabb.func_186670_a(mainPosition)), VoxelShapes.func_197881_a(aabb.func_186670_a(mainPosition.func_177972_a(block.getUpperDirection(state))))).func_197752_a();
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      if (this.targetPos != null && this.targetFace != null) {
         CompoundNBT target = new CompoundNBT();
         target.func_74780_a("x", this.targetPos.func_82615_a());
         target.func_74780_a("y", this.targetPos.func_82617_b());
         target.func_74780_a("z", this.targetPos.func_82616_c());
         target.func_74774_a("side", (byte)this.targetFace.func_176745_a());
         target.func_74776_a("height", this.height);
         nbt.func_218657_a("target", target);
      }

      nbt.func_74757_a("enabled", this.enabled);
      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      this.load(nbt);
   }

   public void load(CompoundNBT nbt) {
      this.enabled = false;
      if (nbt.func_74764_b("enabled")) {
         this.enabled = nbt.func_74767_n("enabled");
      }

      if (nbt.func_74764_b("target")) {
         CompoundNBT target = nbt.func_74775_l("target");
         if (target.func_74764_b("x") && target.func_74764_b("y") && target.func_74764_b("z") && target.func_74764_b("side") && target.func_74764_b("height")) {
            double x = target.func_74769_h("x");
            double y = target.func_74769_h("y");
            double z = target.func_74769_h("z");
            this.targetPos = new Vector3d(x, y, z);
            this.targetFace = Direction.func_82600_a(target.func_74771_c("side"));
            this.height = target.func_74760_g("height");
         }
      }

   }

   public boolean isEnabled() {
      return this.override;
   }

   public Vector3d getTargetPos() {
      return this.targetPos;
   }

   public Direction getTargetFace() {
      return this.targetFace;
   }

   public float getHeight() {
      return this.height;
   }

   public float getPredictedHeight(Vector3d absoluteTargetPos) {
      return this.height != null ? this.height : Math.max(1.0F, (float)((double)(new BlockPos(absoluteTargetPos)).func_218139_n(this.func_174877_v()) / (double)4.0F));
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void func_189667_a(Rotation rotation) {
      Vector3d newTarget = this.targetPos;
      switch (rotation) {
         case CLOCKWISE_90:
            newTarget = new Vector3d(-this.targetPos.field_72449_c, this.targetPos.field_72448_b, this.targetPos.field_72450_a);
            break;
         case CLOCKWISE_180:
            newTarget = new Vector3d(-this.targetPos.field_72450_a, this.targetPos.field_72448_b, -this.targetPos.field_72449_c);
            break;
         case COUNTERCLOCKWISE_90:
            newTarget = new Vector3d(this.targetPos.field_72449_c, this.targetPos.field_72448_b, -this.targetPos.field_72450_a);
      }

      this.targetPos = newTarget;
      this.targetFace = rotation.func_185831_a(this.targetFace);
   }

   public void func_189668_a(Mirror mirror) {
      this.targetPos = (new Vec3(this.targetPos)).mul(mirror == Mirror.FRONT_BACK ? (double)-1.0F : (double)1.0F, (double)1.0F, mirror == Mirror.LEFT_RIGHT ? (double)-1.0F : (double)1.0F).to3d();
      this.targetFace = mirror.func_185803_b(this.targetFace);
   }

   public CompoundNBT func_189517_E_() {
      return this.func_189515_b(new CompoundNBT());
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.func_174877_v(), -1, this.func_189515_b(new CompoundNBT()));
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
      this.load(packet.func_148857_g());
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return (new AxisAlignedBB(this.func_174877_v())).func_186662_g(1.0E30);
   }

   public double func_145833_n() {
      return (double)256.0F;
   }

   public List<BlockPos> getIndicatorPositions(BlockState blockState, World world, BlockPos pos) {
      Direction facing = (Direction)blockState.func_177229_b(FaithPlateBlock.FACING);
      Direction up = Direction.UP;
      Direction feet = facing;
      Direction side = facing.func_176746_e();
      boolean vertical = blockState.func_177229_b(FaithPlateBlock.FACE) == FaithPlateBlock.Face.WALL;
      boolean topHalf = blockState.func_177229_b(FaithPlateBlock.HALF) == DoubleBlockHalf.UPPER;
      if (vertical) {
         up = facing;
         feet = Direction.DOWN;
      }

      if (!topHalf) {
         feet = feet.func_176734_d();
      }

      BlockPos above = pos.func_177972_a(up);
      return new ArrayList(Arrays.asList(above.func_177967_a(feet, 2).func_177972_a(side.func_176734_d()), above.func_177967_a(feet, 2), above.func_177967_a(feet, 2).func_177972_a(side), above.func_177972_a(feet).func_177972_a(side), above.func_177972_a(side), above.func_177972_a(feet.func_176734_d()).func_177972_a(side), above.func_177972_a(feet.func_176734_d()), above.func_177972_a(feet.func_176734_d()).func_177972_a(side.func_176734_d()), above.func_177972_a(side.func_176734_d()), above.func_177972_a(feet).func_177972_a(side.func_176734_d())));
   }

   public boolean isIndicatorControlled() {
      return this.indicatorControlled;
   }

   public void setIndicatorControlled(boolean indicatorControlled) {
      this.indicatorControlled = indicatorControlled;
   }

   public boolean isOverride() {
      return this.override;
   }

   public void setOverride(boolean override) {
      this.override = override;
   }

   public void setHeight(float height) {
      this.height = height;
   }
}
