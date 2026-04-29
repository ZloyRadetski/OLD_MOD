package net.portalmod.common.sorted.portal;

import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.common.blocks.PortalableBlock;
import net.portalmod.common.sorted.gel.AbstractGelBlock;
import net.portalmod.common.sorted.panel.PortalHelper;
import net.portalmod.common.sorted.portalgun.PortalHelperServerManager;
import net.portalmod.core.init.BlockTagInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.math.AABBUtil;
import net.portalmod.core.math.AABBVertex;
import net.portalmod.core.math.Collider;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class PortalPlacer {
   private static final BinaryOperator<Vec3> selectLeast = (o, n) -> n.magnitude() < o.magnitude() ? n : o;

   public static PortalEntity placePortal(World level, PortalEnd end, String hue, UUID gunUUID, Vec3 position, Direction face, Direction upDirection, boolean override, @Nullable Direction[] lookingDirections, @Nullable ServerPlayerEntity player) {
      return placePortal(level, end, hue, gunUUID, position, face, upDirection, override, false, lookingDirections, player);
   }

   public static PortalEntity placePortal(World level, PortalEnd end, String hue, UUID gunUUID, Vec3 position, Direction face, Direction upDirection, boolean override, boolean overwriteForeignPortals, @Nullable Direction[] lookingDirections, @Nullable ServerPlayerEntity player) {
      Vec3 forward = new Vec3(face);
      Vec3 up = new Vec3(upDirection);
      Vec3 right = up.clone().cross(forward);
      Mat4 toAbsolute = (new OrthonormalBasis(right, up)).getChangeOfBasisFromCanonicalMatrix();
      BlockPos shotBlockPos = position.clone().add((new Vec3(face.func_176734_d())).mul(0.001)).toBlockPos();
      BlockState shotBlockState = level.func_180495_p(shotBlockPos);
      BlockState behindBlockState = level.func_180495_p(shotBlockPos.func_177972_a(face.func_176734_d()));
      Block shotBlock = shotBlockState.func_177230_c();
      if (PortalableBlock.isPortalable(shotBlockState, face, level) || shotBlock.func_203417_a(BlockTagInit.PORTAL_INHERITING) && PortalableBlock.isPortalable(behindBlockState, face, level)) {
         if (player != null) {
            Optional<VolatilePortalHelper> optionalHelper = VolatilePortalHelperManager.getInstance().findHelperThatWillHelp(player, gunUUID, end, level, position, face);
            if (optionalHelper.isPresent()) {
               VolatilePortalHelper helper = (VolatilePortalHelper)optionalHelper.get();
               PortalHelperServerManager.getInstance().setHelped(player, gunUUID, end, helper);
               position = helper.helpPortal(position, face);
            } else if (PortalHelperServerManager.getInstance().willBeHelped(player, gunUUID, end, shotBlockPos, face, upDirection, level) && lookingDirections != null) {
               PortalHelperServerManager.getInstance().setHelped(player, gunUUID, end, shotBlockPos, face);
               Pair<Vec3, Direction> helpment = ((PortalHelper)shotBlock).helpPortal(position, face, upDirection, lookingDirections, shotBlockState, level);
               position = (Vec3)helpment.getFirst();
               up = new Vec3((Direction)helpment.getSecond());
            }
         }

         AxisAlignedBB portalAABB = new AxisAlignedBB((double)-0.5F, (double)-1.0F, (double)0.0F, (double)0.5F, (double)1.0F, (double)0.0625F);
         portalAABB = AABBUtil.transform(portalAABB, toAbsolute).func_191194_a(position.to3d());
         VoxelShape collision = getCollision(level, face, position, toAbsolute, true);
         List<PortalEntity> portalsInTheWay = PortalEntity.getPortals(level, portalAABB.func_186662_g((double)2.0F), (portalx) -> (new Vec3(portalx.getNormal())).dot(new Vec3(face)) > 0.99 && (new Vec3(portalx.func_213303_ch())).choose(face.func_176740_k()) - position.choose(face.func_176740_k()) < 0.01 && (!portalx.getGunUUID().equals(gunUUID) || portalx.getEnd() != end));
         List<AxisAlignedBB> bumpingPortals = (List)portalsInTheWay.stream().filter((portalx) -> !override && (!overwriteForeignPortals || portalx.getGunUUID().equals(gunUUID))).map(Entity::func_174813_aQ).collect(Collectors.toList());
         collision = AABBUtil.addBoxesToVoxelShape(collision, bumpingPortals).func_197753_c();
         List<AABBVertex> vertices = Collider.getFaceCorners(face.func_176734_d(), portalAABB);

         for(AxisAlignedBB box : collision.func_197756_d()) {
            for(int i = vertices.size() - 1; i >= 0; --i) {
               if (Collider.pointInBoxExceptAxis(face.func_176740_k(), box, ((AABBVertex)vertices.get(i)).getPosition())) {
                  vertices.remove(i);
               }
            }
         }

         List<List<Direction>> allNormals = (List)vertices.stream().map((corner) -> corner.getCorner().getNormals()).collect(Collectors.toList());
         allNormals.forEach((normals) -> normals.remove(face.func_176734_d()));
         Vec3 finalReaction = null;
         switch (vertices.size()) {
            case 1:
               Map<Direction, Double> largestReactionByDirection = getLargestReactionByShortestDirection(face, portalAABB, collision, allNormals);
               finalReaction = (Vec3)((List)allNormals.get(0)).stream().map((direction) -> (new Vec3(direction)).mul((Double)largestReactionByDirection.getOrDefault(direction, (double)0.0F))).reduce(Vec3.origin(), Vec3::add);
               break;
            case 2:
               Stream var47 = ((List)allNormals.get(0)).stream();
               List var50 = (List)allNormals.get(1);
               var50.getClass();
               List<Direction> reactionDirections = (List)var47.filter(var50::contains).collect(Collectors.toList());
               if (reactionDirections.isEmpty()) {
                  return null;
               }

               Map<Direction, Double> largestReactionByDirection = getLargestReactionByDirection(face, portalAABB, collision);
               Direction reactionDirection = (Direction)reactionDirections.get(0);
               finalReaction = (new Vec3(reactionDirection)).mul((Double)largestReactionByDirection.get(reactionDirection));
               break;
            case 3:
               Stream var10000 = ((List)allNormals.get(0)).stream();
               List var10001 = (List)allNormals.get(1);
               var10001.getClass();
               Stream<Direction> normals12 = var10000.filter(var10001::contains);
               var10000 = ((List)allNormals.get(1)).stream();
               var10001 = (List)allNormals.get(2);
               var10001.getClass();
               Stream<Direction> normals23 = var10000.filter(var10001::contains);
               var10000 = ((List)allNormals.get(0)).stream();
               var10001 = (List)allNormals.get(2);
               var10001.getClass();
               Stream<Direction> normals13 = var10000.filter(var10001::contains);
               Map<Direction, Double> largestReactionByDirection = getLargestReactionByDirection(face, portalAABB, collision);
               finalReaction = (Vec3)Stream.concat(Stream.concat(normals12, normals23), normals13).map((direction) -> (new Vec3(direction)).mul((Double)largestReactionByDirection.get(direction))).reduce(new Vec3(1.0E128), selectLeast);
            case 4:
               break;
            default:
               return null;
         }

         if (finalReaction != null) {
            position.add(finalReaction);
            portalAABB = AABBUtil.translate(portalAABB, finalReaction);
         }

         position.mul((double)16.0F).round().div((double)16.0F);
         portalAABB = portalAABB.func_186664_h(0.001);
         if (portalCollides(face, portalAABB, collision)) {
            return null;
         } else {
            if (override || overwriteForeignPortals) {
               PortalEntity.getPortals(level, portalAABB, (portalx) -> (new Vec3(portalx.getNormal())).dot(new Vec3(face)) > 0.99 && (!portalx.getGunUUID().equals(gunUUID) || portalx.getEnd() != end) && (override || !portalx.getGunUUID().equals(gunUUID)) && portalx.func_174813_aQ().func_72326_a(portalAABB)).forEach((portalx) -> PortalManager.getInstance().scheduleRemoval(portalx));
            }

            PortalEntity portal = new PortalEntity(level);
            position.add((new Vec3(face.func_176730_m())).mul(0.001));
            portal.func_70107_b(position.x, position.y, position.z);
            portal.setDirection(face);
            portal.setUpVector(up.toDirection());
            portal.setEnd(end);
            portal.setHue(hue);
            portal.setGunUUID(gunUUID);
            portal.recalculateBoundingBox();
            PortalManager.getInstance().put(gunUUID, end, portal);
            level.func_217376_c(portal);
            PacketInit.INSTANCE.send(PacketDistributor.ALL.noArg(), new SPortalShotPacket(portal.func_145782_y()));
            level.func_184148_a((PlayerEntity)null, portal.func_226277_ct_(), portal.func_226278_cu_(), portal.func_226281_cx_(), (SoundEvent)SoundInit.PORTAL_OPEN.get(), SoundCategory.NEUTRAL, 1.0F, ModUtil.randomSlightSoundPitch());
            return portal;
         }
      } else {
         return null;
      }
   }

   public static VoxelShape getCollision(World level, Direction face, Vec3 position, Mat4 toAbsolute, boolean large) {
      AxisAlignedBB portalWideAABB = (new AxisAlignedBB((double)-0.5F, (double)-1.0F, (double)0.0F, (double)0.5F, (double)1.0F, (double)0.0625F)).func_186664_h(0.002);
      if (large) {
         portalWideAABB = portalWideAABB.func_72321_a((double)-2.0F, (double)-2.0F, (double)0.0F).func_72321_a((double)2.0F, (double)2.0F, (double)0.0F);
      }

      AxisAlignedBB behind = portalWideAABB.func_72317_d((double)0.0F, (double)0.0F, (double)-0.0625F);
      behind = AABBUtil.transform(behind, toAbsolute).func_191194_a(position.to3d());
      AxisAlignedBB front = AABBUtil.transform(portalWideAABB, toAbsolute).func_191194_a(position.to3d());
      List<BlockPos> backBlocks = AABBUtil.getBlocksWithin(behind);
      List<BlockPos> frontBlocks = AABBUtil.getBlocksWithin(front);
      VoxelShape backCollision = VoxelShapes.func_197880_a();
      VoxelShape frontCollision = VoxelShapes.func_197880_a();

      for(BlockPos block : backBlocks) {
         BlockState attachedBlock = level.func_180495_p(block);
         BlockState behindBlock = level.func_180495_p(block.func_177972_a(face.func_176734_d()));
         boolean portalable = PortalableBlock.isPortalable(attachedBlock, face, level);
         boolean inheriting = attachedBlock.func_235714_a_(BlockTagInit.PORTAL_INHERITING);
         boolean behindPortalable = PortalableBlock.isPortalable(behindBlock, face, level);
         boolean valid = portalable || inheriting && behindPortalable;
         VoxelShape blockShape = level.func_180495_p(block).func_196954_c(level, block).func_197751_a((double)block.func_177958_n(), (double)block.func_177956_o(), (double)block.func_177952_p());
         VoxelShape fullBlockShape = VoxelShapes.func_197868_b().func_197751_a((double)block.func_177958_n(), (double)block.func_177956_o(), (double)block.func_177952_p());
         VoxelShape airAround = VoxelShapes.func_197882_b(fullBlockShape, blockShape, IBooleanFunction.field_223234_e_);
         backCollision = VoxelShapes.func_197882_b(backCollision, valid ? airAround : fullBlockShape, IBooleanFunction.field_223244_o_);
      }

      for(BlockPos block : frontBlocks) {
         BlockState frontBlock = level.func_180495_p(block);
         boolean frontNonBlocking = frontBlock.func_235714_a_(BlockTagInit.PORTAL_NONBLOCKING);
         VoxelShape blockShape = level.func_180495_p(block).func_196954_c(level, block).func_197751_a((double)block.func_177958_n(), (double)block.func_177956_o(), (double)block.func_177952_p());
         if (frontBlock.func_177230_c() instanceof AbstractGelBlock) {
            if ((Boolean)frontBlock.func_177229_b((Property)AbstractGelBlock.STATES.get(face.func_176734_d()))) {
               blockShape = ((VoxelShapeGroup)AbstractGelBlock.SHAPES.get(face.func_176734_d())).getShape().func_197751_a((double)block.func_177958_n(), (double)block.func_177956_o(), (double)block.func_177952_p());
            } else {
               blockShape = VoxelShapes.func_197880_a();
            }
         }

         if (!frontNonBlocking) {
            frontCollision = VoxelShapes.func_197882_b(frontCollision, blockShape, IBooleanFunction.field_223244_o_);
         }
      }

      backCollision = VoxelShapes.func_197882_b(backCollision, VoxelShapes.func_197881_a(behind), IBooleanFunction.field_223238_i_);
      frontCollision = VoxelShapes.func_197882_b(frontCollision, VoxelShapes.func_197881_a(front), IBooleanFunction.field_223238_i_);
      return VoxelShapes.func_197872_a(backCollision, frontCollision);
   }

   private static Map<Direction, Double> getLargestReactionByDirection(Direction face, AxisAlignedBB portalAABB, VoxelShape collision) {
      Map<Direction, Double> largestReactionByDirection = new HashMap();

      for(AxisAlignedBB box : collision.func_197756_d()) {
         if (Collider.intersectExceptAxis(face.func_176740_k(), box, portalAABB)) {
            Optional<List<Vec3>> reactionsOptional = Collider.collideExceptAxis(face.func_176740_k(), box, portalAABB);
            if (reactionsOptional.isPresent()) {
               for(Vec3 reaction : (List)reactionsOptional.get()) {
                  Direction reactionDirection = reaction.clone().normalize().round().toDirection();
                  double oldMax = (Double)largestReactionByDirection.getOrDefault(reactionDirection, reaction.magnitude());
                  largestReactionByDirection.put(reactionDirection, Math.max(oldMax, reaction.magnitude()));
               }
            }
         }
      }

      return largestReactionByDirection;
   }

   private static Map<Direction, Double> getLargestReactionByShortestDirection(Direction face, AxisAlignedBB portalAABB, VoxelShape collision, List<List<Direction>> allNormals) {
      Map<Direction, Double> largestReactionByDirection = new HashMap();

      for(AxisAlignedBB box : collision.func_197756_d()) {
         Optional<List<Vec3>> reactionsOptional = Collider.collideExceptAxis(face.func_176740_k(), box, portalAABB);
         if (reactionsOptional.isPresent()) {
            Optional<Vec3> reactionOptional = ((List)reactionsOptional.get()).stream().filter((x) -> ((List)allNormals.get(0)).contains(x.clone().normalize().round().toDirection())).reduce(selectLeast);
            if (reactionOptional.isPresent()) {
               Vec3 reaction = (Vec3)reactionOptional.get();
               Direction reactionDirection = reaction.clone().normalize().round().toDirection();
               double oldMax = (Double)largestReactionByDirection.getOrDefault(reactionDirection, reaction.magnitude());
               largestReactionByDirection.put(reactionDirection, Math.max(oldMax, reaction.magnitude()));
            }
         }
      }

      return largestReactionByDirection;
   }

   public static boolean portalCollides(Direction face, AxisAlignedBB portalAABB, VoxelShape collision) {
      return collision.func_197756_d().stream().anyMatch((box) -> Collider.collideExceptAxis(face.func_176740_k(), box, portalAABB).isPresent());
   }
}
