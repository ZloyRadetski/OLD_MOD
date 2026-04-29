package net.portalmod.common.sorted.autoportal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Tuple;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.common.blocks.OmnidirectionalQuadBlock;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.button.QuadBlockCorner;
import net.portalmod.common.sorted.portal.OrthonormalBasis;
import net.portalmod.common.sorted.portal.PortalColors;
import net.portalmod.common.sorted.portal.PortalEnd;
import net.portalmod.common.sorted.portalgun.PortalGun;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.BiHashMap;
import net.portalmod.core.math.Mat4;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.math.VoxelShapeGroup;
import net.portalmod.core.util.ModUtil;

public class AutoPortalBlock extends OmnidirectionalQuadBlock {
   public static final BooleanProperty POWERED;
   private static final BiHashMap<String, QuadBlockCorner, VoxelShapeGroup> SHAPES;

   public AutoPortalBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.func_180632_j((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.UP)).func_206870_a(CORNER, QuadBlockCorner.UP_LEFT)).func_206870_a(DIRECTION, Direction.NORTH)).func_206870_a(POWERED, false));
      this.initAABBs();
   }

   protected void func_206840_a(StateContainer.Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, CORNER, DIRECTION, POWERED});
   }

   private void initAABBs() {
      VoxelShapeGroup leftShape = (new VoxelShapeGroup.Builder()).add((double)3.0F, (double)0.0F, (double)0.0F, (double)6.0F, (double)16.0F, 1.01).build();
      VoxelShapeGroup rightShape = (new VoxelShapeGroup.Builder()).add((double)10.0F, (double)0.0F, (double)0.0F, (double)13.0F, (double)16.0F, 1.01).build();

      for(Direction direction : Direction.values()) {
         if (direction.func_176740_k() != Axis.Y) {
            for(Direction facing : Direction.values()) {
               for(QuadBlockCorner corner : QuadBlockCorner.values()) {
                  Tuple<Direction, Direction> directions = this.placementDirectionsFromFacingAndDirection(facing, direction);
                  Direction a = (Direction)directions.func_76341_a();
                  Direction b = (Direction)directions.func_76340_b();
                  int x = QuadBlockCorner.DOWN_RIGHT.getX() - QuadBlockCorner.DOWN_LEFT.getX();
                  int y = QuadBlockCorner.UP_LEFT.getY() - QuadBlockCorner.DOWN_LEFT.getY();
                  if (facing.func_176743_c() == AxisDirection.NEGATIVE) {
                     x *= -1;
                  }

                  if (x < 0) {
                     a = a.func_176734_d();
                  }

                  if (y < 0) {
                     b = b.func_176734_d();
                  }

                  Vec3 up = new Vec3(b);
                  Vec3 right = new Vec3(a);
                  Mat4 matrix = (new OrthonormalBasis(right, up)).getChangeOfBasisFromCanonicalMatrix();
                  Mat4 am = Mat4.identity().translate((double)0.5F, (double)0.5F, (double)0.5F).mul(matrix).translate((double)-0.5F, (double)-0.5F, (double)-0.5F);
                  SHAPES.put(facing + " " + direction, corner, (corner.isLeft() ? leftShape : rightShape).clone().transform(am));
               }
            }
         }
      }

   }

   public void setAntlinePowered(boolean powered, BlockState blockState, World world, BlockPos pos) {
      this.setBlockStateValue(POWERED, powered, blockState, world, pos);
      this.updateAllNeighbors(world, pos, blockState);
   }

   private VoxelShapeGroup getShapeGroup(BlockState state) {
      return SHAPES.get(state.func_177229_b(FACING) + " " + state.func_177229_b(DIRECTION), state.func_177229_b(CORNER));
   }

   public ActionResultType func_225533_a_(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
      if (!WrenchItem.usedWrench(player, hand)) {
         return ActionResultType.PASS;
      } else if (level.field_72995_K) {
         return ActionResultType.CONSUME;
      } else {
         Block block = state.func_177230_c();
         if (!(block instanceof AutoPortalBlock)) {
            return ActionResultType.PASS;
         } else {
            BlockPos tePos = ((AutoPortalBlock)block).getOtherBlock(pos, (QuadBlockCorner)state.func_177229_b(CORNER), QuadBlockCorner.DOWN_LEFT, (Direction)state.func_177229_b(FACING), (Direction)state.func_177229_b(DIRECTION));
            TileEntity te = level.func_175625_s(tePos);
            if (!(te instanceof AutoPortalTileEntity)) {
               return ActionResultType.PASS;
            } else {
               AutoPortalTileEntity autoPortal = (AutoPortalTileEntity)te;
               if (!(player.func_184592_cb().func_77973_b() instanceof PortalGun)) {
                  if (autoPortal.end == null) {
                     WrenchItem.playFailSound(level, rayTraceResult.func_216347_e());
                     return ActionResultType.SUCCESS;
                  } else if (autoPortal.lastOpenedUUID != null) {
                     autoPortal.closePortal();
                     WrenchItem.playUseSound(level, rayTraceResult.func_216347_e());
                     return ActionResultType.SUCCESS;
                  } else {
                     autoPortal.swapEnd();
                     WrenchItem.playUseSound(level, rayTraceResult.func_216347_e());
                     player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.autoportal." + (autoPortal.end == PortalEnd.PRIMARY ? "primary" : "secondary")), true);
                     return ActionResultType.SUCCESS;
                  }
               } else {
                  ItemStack itemStack = player.func_184592_cb();
                  Optional<UUID> uuid = PortalGun.getUUID(itemStack);
                  if (uuid.isPresent() && itemStack.func_77942_o()) {
                     CompoundNBT nbt = itemStack.func_77978_p();
                     if (nbt == null) {
                        return ActionResultType.PASS;
                     } else if (nbt.func_74764_b("LeftColor") && nbt.func_74764_b("RightColor")) {
                        int primaryColor = PortalColors.getIndex(nbt.func_74779_i("LeftColor"));
                        int secondaryColor = PortalColors.getIndex(nbt.func_74779_i("RightColor"));
                        PortalEnd end = nbt.func_74764_b("Locked") && nbt.func_74779_i("Locked").equals("Left") ? PortalEnd.PRIMARY : PortalEnd.SECONDARY;
                        autoPortal.link((UUID)uuid.get(), end, primaryColor, secondaryColor);
                        WrenchItem.playUseSound(level, rayTraceResult.func_216347_e());
                        player.func_146105_b(new TranslationTextComponent("actionbar.portalmod.autoportal.set"), true);
                        return ActionResultType.SUCCESS;
                     } else {
                        return ActionResultType.PASS;
                     }
                  } else {
                     return ActionResultType.PASS;
                  }
               }
            }
         }
      }
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      VoxelShape shape = this.getShapeGroup(state).getShape();
      return shape != null ? shape : VoxelShapes.func_197880_a();
   }

   public VoxelShape func_220071_b(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      return this.func_220053_a(state, level, pos, context);
   }

   public boolean isCornerPlaceable(BlockItemUseContext context, QuadBlockCorner corner) {
      Direction direction = context.func_196000_l();
      return this.getAllBlocks(context.func_195995_a(), corner, direction, context.func_195992_f()).stream().allMatch((pos) -> ModUtil.canPlaceAt(context, pos) && context.func_195991_k().func_180495_p(pos.func_177972_a(direction.func_176734_d())).func_224755_d(context.func_195991_k(), pos, direction));
   }

   public void func_220069_a(BlockState state, World level, BlockPos pos, Block block, BlockPos neighborPos, boolean b) {
      if (!level.field_72995_K) {
         Direction facing = (Direction)state.func_177229_b(FACING);
         boolean isPowered = this.getAllPositions(state, pos).stream().filter((blockPos) -> level.func_180495_p(blockPos).func_177230_c() instanceof AutoPortalBlock).anyMatch((checkingPos) -> level.func_175709_b(checkingPos.func_177972_a(facing.func_176734_d()), facing));
         TileEntity te = level.func_175625_s(this.getOtherBlock(pos, (QuadBlockCorner)state.func_177229_b(CORNER), QuadBlockCorner.DOWN_LEFT, facing, (Direction)state.func_177229_b(DIRECTION)));
         if (te instanceof AutoPortalTileEntity) {
            ((AutoPortalTileEntity)te).setPowered(isPowered);
         }

         if (!state.func_196955_c(level, pos)) {
            level.func_241212_a_(pos, true, (Entity)null, 0);
         }

      }
   }

   public boolean hasTileEntity(BlockState state) {
      return state.func_177229_b(CORNER) == QuadBlockCorner.DOWN_LEFT;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ((TileEntityType)TileEntityTypeInit.AUTOPORTAL.get()).func_200968_a();
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("autoportal", list);
   }

   static {
      POWERED = BlockStateProperties.field_208194_u;
      SHAPES = new BiHashMap<String, QuadBlockCorner, VoxelShapeGroup>();
   }
}
