package net.portalmod.core.util;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.PortalMod;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.core.config.PortalModConfigManager;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;

public class ModUtil {
   public static final Style TOOLTIP_STYLE;
   private static int lastChatNumber;

   public static VoxelShape moveVoxelShape(VoxelShape shape, Direction direction, int multiplier) {
      Vector3i normal = direction.func_176730_m();
      return shape.func_197751_a((double)(normal.func_177958_n() * multiplier), (double)(normal.func_177956_o() * multiplier), (double)(normal.func_177952_p() * multiplier));
   }

   public static VoxelShape moveVoxelShape(VoxelShape shape, Direction direction) {
      return moveVoxelShape(shape, direction, 1);
   }

   public static BlockRayTraceResult rayTraceBlock(PlayerEntity player, World level, int length) {
      Vector3d rayPath = player.func_70676_i(0.0F).func_186678_a((double)length);
      Vector3d from = player.func_174824_e(0.0F);
      Vector3d to = from.func_178787_e(rayPath);
      RayTraceContext rayCtx = new RayTraceContext(from, to, BlockMode.OUTLINE, FluidMode.ANY, (Entity)null);
      return level.func_217299_a(rayCtx);
   }

   public static BlockRayTraceResult customClip(World level, RayTraceContext context, Function<BlockPos, Optional<VoxelShape>> shapeOverride) {
      return (BlockRayTraceResult)IBlockReader.func_217300_a(context, (ctx, pos) -> {
         BlockState blockstate = level.func_180495_p(pos);
         FluidState fluidstate = level.func_204610_c(pos);
         Vector3d vector3d = ctx.func_222253_b();
         Vector3d vector3d1 = ctx.func_222250_a();
         VoxelShape voxelshape = (VoxelShape)((Optional)shapeOverride.apply(pos)).orElse(ctx.func_222251_a(blockstate, level, pos));
         BlockRayTraceResult blockraytraceresult = level.func_217296_a(vector3d, vector3d1, pos, voxelshape, blockstate);
         VoxelShape voxelshape1 = ctx.func_222252_a(fluidstate, level, pos);
         BlockRayTraceResult blockraytraceresult1 = voxelshape1.func_212433_a(vector3d, vector3d1, pos);
         double d0 = blockraytraceresult == null ? Double.MAX_VALUE : ctx.func_222253_b().func_72436_e(blockraytraceresult.func_216347_e());
         double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : ctx.func_222253_b().func_72436_e(blockraytraceresult1.func_216347_e());
         return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
      }, (ctx) -> {
         Vector3d vector3d = ctx.func_222253_b().func_178788_d(ctx.func_222250_a());
         return BlockRayTraceResult.func_216352_a(ctx.func_222250_a(), Direction.func_210769_a(vector3d.field_72450_a, vector3d.field_72448_b, vector3d.field_72449_c), new BlockPos(ctx.func_222250_a()));
      });
   }

   public static List<PortalEntity> getPortalsAlongRay(World level, Vec3 from, Vec3 to, Predicate<PortalEntity> filter) {
      List<PortalEntity> portalChain = new ArrayList();
      from = from.clone();
      to = to.clone();

      PortalEntity portal;
      Mat4 matrix;
      for(int limit = 100; limit-- > 0; to = to.sub(portal.func_213303_ch()).transform(matrix).add(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch())) {
         AxisAlignedBB rayAABB = new AxisAlignedBB(from.to3d(), to.to3d());
         Vec3 finalFrom = from.clone();
         Optional<PortalEntity> optionalPortal = PortalEntity.getPortals(level, rayAABB, filter).stream().reduce((o, n) -> n.func_213303_ch().func_72438_d(finalFrom.to3d()) < o.func_213303_ch().func_72438_d(finalFrom.to3d()) ? n : o);
         if (!optionalPortal.isPresent()) {
            break;
         }

         portal = (PortalEntity)optionalPortal.get();
         AxisAlignedBB clipAABB = portal.func_174813_aQ().func_191194_a((new Vec3(portal.getNormal())).mul((double)-0.0625F).to3d());
         boolean traversesPortal = clipAABB.func_216365_b(from.to3d(), to.to3d()).isPresent();
         if (!traversesPortal) {
            break;
         }

         boolean rightDirection = from.clone().sub(portal.func_213303_ch()).dot(portal.getNormal()) > (double)0.0F && to.clone().sub(portal.func_213303_ch()).dot(portal.getNormal()) < (double)0.0F;
         if (!rightDirection || !portal.getOtherPortal().isPresent()) {
            break;
         }

         portalChain.add(portal);
         matrix = portal.getSourceBasis().getChangeOfBasisMatrix(((PortalEntity)portal.getOtherPortal().get()).getDestinationBasis());
         from = from.sub(portal.func_213303_ch()).transform(matrix).add(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch());
      }

      return portalChain;
   }

   public static BlockRayTraceResult clipThroughPortals(World level, RayTraceContext context) {
      List<PortalEntity> portalChain = getPortalsAlongRay(level, new Vec3(context.func_222253_b()), new Vec3(context.func_222250_a()), (portal) -> true);
      if (portalChain.isEmpty()) {
         return level.func_217299_a(context);
      } else {
         BlockRayTraceResult normalRay = level.func_217299_a(context);
         Vector3d positionBeforePortal = normalRay.func_216347_e();
         Optional<Vector3d> optionalPositionOnPortal = ((PortalEntity)portalChain.get(0)).func_174813_aQ().func_216365_b(context.func_222253_b(), context.func_222250_a());
         if (optionalPositionOnPortal.isPresent()) {
            double distanceBeforePortal = positionBeforePortal.func_178788_d(context.func_222253_b()).func_72433_c();
            double distanceOnPortal = ((Vector3d)optionalPositionOnPortal.get()).func_178788_d(context.func_222253_b()).func_72433_c();
            if (distanceBeforePortal < distanceOnPortal) {
               return normalRay;
            }
         }

         Mat4 portalMatrix = getMatrixFromPortalChain(portalChain);
         Vector3d to = (new Vec3(context.func_222250_a())).transform(portalMatrix).to3d();
         Vector3d from = (new Vec3(context.func_222253_b())).transform(portalMatrix).to3d();
         PortalEntity last = (PortalEntity)portalChain.get(portalChain.size() - 1);
         if (!last.getOtherPortal().isPresent()) {
            return level.func_217299_a(context);
         } else {
            Optional<Vector3d> intersection = ((PortalEntity)last.getOtherPortal().get()).func_174813_aQ().func_216365_b(from, to);
            if (intersection.isPresent()) {
               from = (Vector3d)intersection.get();
            }

            RayTraceContext transformed = new RayTraceContextWrapper(context);
            ((RayTraceContextWrapper)transformed).setFrom(from);
            ((RayTraceContextWrapper)transformed).setTo(to);
            return level.func_217299_a(transformed);
         }
      }
   }

   public static Mat4 getMatrixFromPortalChain(List<PortalEntity> portalChain) {
      Mat4 portalMatrix = Mat4.identity();

      for(PortalEntity portal : portalChain) {
         if (!portal.getOtherPortal().isPresent()) {
            break;
         }

         Mat4 matrix = portal.getSourceBasis().getChangeOfBasisMatrix(((PortalEntity)portal.getOtherPortal().get()).getDestinationBasis());
         portalMatrix = Mat4.identity().translate(new Vec3(((PortalEntity)portal.getOtherPortal().get()).func_213303_ch())).mul(matrix).translate((new Vec3(portal.func_213303_ch())).negate()).mul(portalMatrix);
      }

      return portalMatrix;
   }

   public static Mat4 getRotationMatrixFromPortalChain(List<PortalEntity> portalChain) {
      Mat4 portalMatrix = Mat4.identity();

      for(PortalEntity portal : portalChain) {
         if (!portal.getOtherPortal().isPresent()) {
            break;
         }

         Mat4 matrix = portal.getSourceBasis().getChangeOfBasisMatrix(((PortalEntity)portal.getOtherPortal().get()).getDestinationBasis());
         portalMatrix = Mat4.identity().mul(matrix).mul(portalMatrix);
      }

      return portalMatrix;
   }

   public static Pair<Vector3d, Vector3d> teleportRay(List<PortalEntity> portalChain, Vector3d from, Vector3d to) {
      if (portalChain.isEmpty()) {
         return new Pair(from, to);
      } else {
         Mat4 portalMatrix = getMatrixFromPortalChain(portalChain);
         from = (new Vec3(from)).transform(portalMatrix).to3d();
         to = (new Vec3(to)).transform(portalMatrix).to3d();
         PortalEntity last = (PortalEntity)portalChain.get(portalChain.size() - 1);
         if (!last.getOtherPortal().isPresent()) {
            return new Pair(from, to);
         } else {
            Optional<Vector3d> intersection = ((PortalEntity)last.getOtherPortal().get()).func_174813_aQ().func_216365_b(from, to);
            if (intersection.isPresent()) {
               from = (Vector3d)intersection.get();
            }

            return new Pair(from, to);
         }
      }
   }

   public static Vector3d getOldPos(Entity entity) {
      return new Vector3d(entity.field_70169_q, entity.field_70167_r, entity.field_70166_s);
   }

   public static void addTooltip(String name, List<ITextComponent> list) {
      if ((Boolean)PortalModConfigManager.TOOLTIPS.get()) {
         if (!Screen.func_231172_r_()) {
            list.add(tooltipComponent("tooltip.portalmod.hold_control", getModifierKeyName()));
         } else if (I18n.func_188566_a("tooltip.portalmod." + name)) {
            list.add(tooltipComponent("tooltip.portalmod." + name));
         } else {
            int i = 1;

            while(true) {
               String key = "tooltip.portalmod." + name + "_" + i;
               if (!I18n.func_188566_a(key)) {
                  return;
               }

               list.add(tooltipComponent(key));
               ++i;
            }
         }
      }
   }

   public static IFormattableTextComponent tooltipComponent(String key, Object... args) {
      return (new TranslationTextComponent(key, args)).func_230530_a_(TOOLTIP_STYLE);
   }

   private static String getModifierKeyName() {
      return Util.func_110647_a() == OS.OSX ? "Command" : "Ctrl";
   }

   public static float symmetricRandom(float width) {
      return (new Random()).nextFloat() * width * 2.0F - width;
   }

   public static float symmetricRandom() {
      return symmetricRandom(1.0F);
   }

   public static float randomSoundPitch(float width) {
      return 1.0F + symmetricRandom(width);
   }

   public static float randomSoundPitch() {
      return randomSoundPitch(0.15F);
   }

   public static float randomSlightSoundPitch() {
      return randomSoundPitch(0.075F);
   }

   public static void sendChat(World level, Object... text) {
      sendChat(level, level.field_72995_K, text);
   }

   private static void sendChat(World level, boolean isClientSide, Object... text) {
      String formatted = (String)Arrays.stream(text).map((t) -> t == null ? "null" : t.toString()).collect(Collectors.joining(" "));

      try {
         level.func_217369_A().forEach((player) -> player.func_146105_b(new StringTextComponent((isClientSide ? "§3§l[Client " : "§7§l[Server ") + String.format("%03d", lastChatNumber) + "]: §r" + formatted), false));
      } catch (Exception e) {
         PortalMod.LOGGER.error("Could not send debug chat message", e);
      }

      lastChatNumber = (lastChatNumber + 1) % 1000;
   }

   public static boolean canPlaceAt(BlockItemUseContext context, BlockPos pos) {
      return context.func_195991_k().func_180495_p(pos).func_196953_a(context) && pos.func_177956_o() < context.func_195991_k().func_217301_I() && pos.func_177956_o() >= 0;
   }

   public static int getRotationAmount(Rotation rotation) {
      switch (rotation) {
         case CLOCKWISE_90:
            return 1;
         case CLOCKWISE_180:
            return 2;
         case COUNTERCLOCKWISE_90:
            return 3;
         default:
            return 0;
      }
   }

   static {
      TOOLTIP_STYLE = Style.field_240709_b_.func_240712_a_(TextFormatting.GRAY);
      lastChatNumber = 0;
   }
}
