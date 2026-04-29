package net.portalmod.common.sorted.panel;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.portalmod.common.blocks.MultiBlock;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;

public class PanelBlock extends Block implements PortalHelper {
   public static final EnumProperty<PanelState> STATE = EnumProperty.func_177709_a("state", PanelState.class);
   public static final EnumProperty<Direction.Axis> AXIS;

   public PanelBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(STATE, PanelState.SINGLE)).func_206870_a(AXIS, Axis.X));
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{STATE, AXIS});
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      World world = context.func_195991_k();
      Direction clickedFace = context.func_196000_l();
      BlockPos clickedOnPos = context.func_195995_a().func_177972_a(clickedFace.func_176734_d());
      BlockState clickedBlock = world.func_180495_p(clickedOnPos);
      PlayerEntity player = context.func_195999_j();
      if (clickedBlock.func_177230_c().func_235332_a_(this) && player != null && player.func_225608_bj_()) {
         PanelState clickedPanelState = (PanelState)clickedBlock.func_177229_b(STATE);
         boolean clickedOnSide = clickedFace.func_176740_k() != Axis.Y;
         if (clickedPanelState.isSingle() && !clickedOnSide) {
            return (BlockState)this.func_176223_P().func_206870_a(STATE, PanelState.doubleState(clickedFace == Direction.DOWN));
         } else {
            if (clickedOnSide && areTwoBlocksInInventory(player, this)) {
               if (clickedPanelState.isDouble() && world.func_180495_p(context.func_195995_a().func_177972_a(clickedPanelState.getVerticalFacing())).func_196953_a(context)) {
                  removeBlockFromInventory(player, this);
                  return (BlockState)((BlockState)this.func_176223_P().func_206870_a(STATE, wallStateFromDirection(clickedFace, clickedPanelState))).func_206870_a(AXIS, clickedFace.func_176740_k());
               }

               if (clickedPanelState.isSingle()) {
                  Direction left = clickedFace.func_176746_e();
                  Direction right = clickedFace.func_176735_f();
                  BlockState leftBlockState = world.func_180495_p(clickedOnPos.func_177972_a(left));
                  BlockState rightBlockState = world.func_180495_p(clickedOnPos.func_177972_a(right));
                  boolean canPlaceOnRight = leftBlockState.func_203425_a(this) && ((PanelState)leftBlockState.func_177229_b(STATE)).isSingle() && world.func_180495_p(context.func_195995_a().func_177972_a(left)).func_196953_a(context);
                  boolean canPlaceOnLeft = rightBlockState.func_203425_a(this) && ((PanelState)rightBlockState.func_177229_b(STATE)).isSingle() && world.func_180495_p(context.func_195995_a().func_177972_a(right)).func_196953_a(context);
                  if (!canPlaceOnRight && !canPlaceOnLeft) {
                     return this.func_176223_P();
                  }

                  boolean prefersLeft = MultiBlock.clickedOnPositiveHalf(context, right);
                  boolean willPlaceLeft = prefersLeft && canPlaceOnLeft || !canPlaceOnRight;
                  Direction.Axis axis = clickedFace.func_176740_k();
                  boolean getsBottomState = axis == Axis.Z ? clickedFace == Direction.SOUTH : willPlaceLeft != (clickedFace == Direction.WEST);
                  boolean getsLeftState = axis == Axis.X ? clickedFace == Direction.WEST : willPlaceLeft != (clickedFace == Direction.NORTH);
                  removeBlockFromInventory(player, this);
                  return (BlockState)((BlockState)this.func_176223_P().func_206870_a(AXIS, left.func_176740_k())).func_206870_a(STATE, PanelState.floorState(getsBottomState, getsLeftState));
               }
            }

            return this.func_176223_P();
         }
      } else {
         return this.func_176223_P();
      }
   }

   public void func_180633_a(World world, BlockPos pos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
      PanelState panelState = (PanelState)blockState.func_177229_b(STATE);
      if (panelState.isDouble()) {
         world.func_175656_a(pos.func_177972_a(panelState.getVerticalFacing()), (BlockState)blockState.func_206870_a(STATE, panelState.mirrorUpDown()));
      } else {
         Direction.Axis axis = (Direction.Axis)blockState.func_177229_b(AXIS);
         if (panelState.isWall()) {
            Direction direction = axis == Axis.X ? (panelState.isLeft() ? Direction.EAST : Direction.WEST) : (panelState.isLeft() ? Direction.NORTH : Direction.SOUTH);
            this.setBlock(world, pos.func_177972_a(panelState.getVerticalFacing()), wallStateFromDirection(direction.func_176734_d(), panelState.mirrorUpDown()), axis);
            this.setBlock(world, pos.func_177972_a(direction), wallStateFromDirection(direction, panelState), axis);
            this.setBlock(world, pos.func_177972_a(direction).func_177972_a(panelState.getVerticalFacing()), wallStateFromDirection(direction, panelState.mirrorUpDown()), axis);
         }

         if (panelState.isFloor()) {
            Direction leftRightDir = getLeftRightFloorDir(panelState);
            Direction upDownDir = getUpDownFloorDir(panelState);
            this.setBlock(world, pos.func_177972_a(leftRightDir), panelState.mirrorLeftRight(), axis);
            this.setBlock(world, pos.func_177972_a(upDownDir), panelState.mirrorUpDown(), axis);
            this.setBlock(world, pos.func_177972_a(leftRightDir).func_177972_a(upDownDir), panelState.mirrorUpDown().mirrorLeftRight(), axis);
         }

      }
   }

   public static Direction getUpDownFloorDir(PanelState panelState) {
      return panelState.isBottom() ? Direction.NORTH : Direction.SOUTH;
   }

   public static Direction getLeftRightFloorDir(PanelState panelState) {
      return panelState.isLeft() ? Direction.EAST : Direction.WEST;
   }

   public void setBlock(World world, BlockPos pos, PanelState state, Direction.Axis axis) {
      world.func_175656_a(pos, (BlockState)((BlockState)this.func_176223_P().func_206870_a(STATE, state)).func_206870_a(AXIS, axis));
   }

   public static PanelState wallStateFromDirection(Direction direction, PanelState placedState) {
      return direction.func_176740_k() == Axis.Y ? PanelState.SINGLE : PanelState.wallState(placedState.isBottom(), direction == Direction.WEST || direction == Direction.SOUTH);
   }

   public BlockState func_196271_a(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
      PanelState panelState = (PanelState)state.func_177229_b(STATE);
      Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
      if (!panelState.isSingle() && this.getConnectedPositions(state, pos).contains(neighborPos)) {
         if (!panelState.isFloor() || neighborState.func_203425_a(this) && axis == neighborState.func_177229_b(AXIS) && (getLeftRightFloorDir(panelState) != direction || neighborState.func_177229_b(STATE) == panelState.mirrorLeftRight()) && (getUpDownFloorDir(panelState) != direction || neighborState.func_177229_b(STATE) == panelState.mirrorUpDown())) {
            if (!panelState.isWall() || direction.func_176740_k() == Axis.Y || neighborState.func_203425_a(this) && axis == neighborState.func_177229_b(AXIS) && panelState.mirrorLeftRight() == neighborState.func_177229_b(STATE)) {
               return direction.func_176740_k() != Axis.Y || neighborState.func_203425_a(this) && panelState.isBottom() != ((PanelState)neighborState.func_177229_b(STATE)).isBottom() ? state : (BlockState)state.func_206870_a(STATE, PanelState.SINGLE);
            } else {
               return (BlockState)state.func_206870_a(STATE, PanelState.doubleState(panelState.isBottom()));
            }
         } else {
            return this.func_176223_P();
         }
      } else {
         return state;
      }
   }

   public static boolean areTwoBlocksInInventory(PlayerEntity player, Block block) {
      if (player.func_184812_l_()) {
         return true;
      } else {
         int total = 0;

         for(int i = 0; i < player.field_71071_by.func_70302_i_(); ++i) {
            ItemStack itemStack = player.field_71071_by.func_70301_a(i);
            Item item = itemStack.func_77973_b();
            if (item instanceof BlockItem && ((BlockItem)item).func_179223_d().func_235332_a_(block)) {
               total += itemStack.func_190916_E();
               if (total >= 2) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static void removeBlockFromInventory(PlayerEntity player, Block block) {
      PlayerInventory inventory = player.field_71071_by;

      for(int i = 0; i < inventory.func_70302_i_(); ++i) {
         ItemStack itemStack = inventory.func_70301_a(i);
         Item item = itemStack.func_77973_b();
         if (item instanceof BlockItem && ((BlockItem)item).func_179223_d().func_235332_a_(block) && (i != inventory.field_70461_c || itemStack.func_190916_E() != 1)) {
            itemStack.func_190918_g(1);
            return;
         }
      }

   }

   public Set<BlockPos> getConnectedPositions(BlockState state, BlockPos pos) {
      Set<BlockPos> set = new HashSet();
      PanelState panelState = (PanelState)state.func_177229_b(STATE);
      Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
      if (panelState.isDouble()) {
         set.add(pos.func_177972_a(panelState.getVerticalFacing()));
      }

      if (panelState.isWall()) {
         Direction direction = axis == Axis.X ? (panelState.isLeft() ? Direction.EAST : Direction.WEST) : (panelState.isLeft() ? Direction.NORTH : Direction.SOUTH);
         set.add(pos.func_177972_a(panelState.getVerticalFacing()));
         set.add(pos.func_177972_a(panelState.getVerticalFacing()).func_177972_a(direction));
         set.add(pos.func_177972_a(direction));
      }

      if (panelState.isFloor()) {
         Direction leftRightDir = getLeftRightFloorDir(panelState);
         Direction upDownDir = getUpDownFloorDir(panelState);
         set.add(pos.func_177972_a(leftRightDir));
         set.add(pos.func_177972_a(upDownDir));
         set.add(pos.func_177972_a(upDownDir).func_177972_a(leftRightDir));
      }

      return set;
   }

   public BlockState func_185499_a(BlockState state, Rotation rotation) {
      PanelState panelState = (PanelState)state.func_177229_b(STATE);
      if (panelState.isFloor()) {
         if (ModUtil.getRotationAmount(rotation) % 2 == 1) {
            state = (BlockState)state.func_235896_a_(AXIS);
         }

         for(int i = 0; i < ModUtil.getRotationAmount(rotation); ++i) {
            state = (BlockState)state.func_206870_a(STATE, ((PanelState)state.func_177229_b(STATE)).rotate());
         }

         return state;
      } else {
         if (panelState.isWall()) {
            if (ModUtil.getRotationAmount(rotation) % 2 == 1) {
               state = (BlockState)state.func_235896_a_(AXIS);
            }

            Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
            if (axis == Axis.Z && rotation == Rotation.CLOCKWISE_90 || axis == Axis.X && rotation == Rotation.COUNTERCLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180) {
               return (BlockState)state.func_206870_a(STATE, panelState.mirrorLeftRight());
            }
         }

         return state;
      }
   }

   public BlockState func_185471_a(BlockState state, Mirror mirror) {
      if (mirror == Mirror.NONE) {
         return state;
      } else {
         PanelState panelState = (PanelState)state.func_177229_b(STATE);
         if (panelState.isFloor()) {
            return (BlockState)state.func_206870_a(STATE, mirror == Mirror.FRONT_BACK ? panelState.mirrorLeftRight() : panelState.mirrorUpDown());
         } else {
            if (panelState.isWall()) {
               Direction.Axis axis = (Direction.Axis)state.func_177229_b(AXIS);
               if (axis == Axis.X && mirror == Mirror.FRONT_BACK || axis == Axis.Z && mirror == Mirror.LEFT_RIGHT) {
                  return (BlockState)state.func_206870_a(STATE, panelState.mirrorLeftRight());
               }
            }

            return state;
         }
      }
   }

   public boolean containsBlock(BlockState state, BlockPos panelPos, BlockPos pos, World world) {
      return panelPos.equals(pos) || this.getConnectedPositions(state, panelPos).contains(pos);
   }

   public boolean willHelpPortal(Direction face, Direction horizontalDirection, BlockState state, World world) {
      PanelState panelState = (PanelState)state.func_177229_b(STATE);
      if (panelState.isFloor()) {
         return face.func_176740_k().func_200128_b();
      } else if (!panelState.isWall()) {
         return false;
      } else {
         return !face.func_176740_k().func_200128_b() && face.func_176746_e().func_176740_k() == state.func_177229_b(AXIS);
      }
   }

   public Pair<Vec3, Direction> helpPortal(Vec3 hitPos, Direction face, Direction horizontalDirection, Direction[] lookingDirections, BlockState state, World world) {
      if (!this.willHelpPortal(face, horizontalDirection, state, world)) {
         return new Pair(hitPos, horizontalDirection);
      } else {
         PanelState panelState = (PanelState)state.func_177229_b(STATE);
         double yPos = panelState.isBottom() ? Math.ceil(hitPos.y) : Math.floor(hitPos.y);
         Optional<Direction> upDirection = Arrays.stream(lookingDirections).filter((direction) -> direction.func_176740_k() == state.func_177229_b(AXIS)).findFirst();
         if (!upDirection.isPresent()) {
            return new Pair(hitPos, lookingDirections[0]);
         } else if (face.func_176740_k().func_200128_b()) {
            return state.func_177229_b(AXIS) == Axis.X ? new Pair(new Vec3(!panelState.isLeft() ? Math.floor(hitPos.x) : Math.ceil(hitPos.x), yPos, panelState.isBottom() ? Math.floor(hitPos.z) : Math.ceil(hitPos.z)), upDirection.get()) : new Pair(new Vec3(!panelState.isLeft() ? Math.floor(hitPos.x) : Math.ceil(hitPos.x), yPos, panelState.isBottom() ? Math.floor(hitPos.z) : Math.ceil(hitPos.z)), upDirection.get());
         } else {
            return face.func_176740_k() == Axis.X ? new Pair(new Vec3(panelState.isBottom() ? Math.floor(hitPos.x) : Math.ceil(hitPos.x), yPos, panelState.isLeft() ? Math.floor(hitPos.z) : Math.ceil(hitPos.z)), horizontalDirection) : new Pair(new Vec3(!panelState.isLeft() ? Math.floor(hitPos.x) : Math.ceil(hitPos.x), yPos, !panelState.isBottom() ? Math.floor(hitPos.z) : Math.ceil(hitPos.z)), horizontalDirection);
         }
      }
   }

   static {
      AXIS = BlockStateProperties.field_208199_z;
   }
}
