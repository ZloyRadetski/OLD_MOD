package net.portalmod.common.sorted.antline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.util.ClientModUtil;

public class AntlineBlock extends Block {
   private static final ThreadLocal<Set<BlockPos>> ACTIVE_SIGNAL_POSITIONS = ThreadLocal.withInitial(HashSet::new);
   private static final int MAX_RECURSION_DEPTH = 256;
   private static final int NO_THRESHOLD = 20;
   private static final VoxelShape DOT = Block.func_208617_a((double)5.0F, (double)0.0F, (double)5.0F, (double)11.0F, (double)2.0F, (double)11.0F);
   private static final VoxelShape WHOLE_SIDE = Block.func_208617_a((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)2.0F, (double)16.0F);
   private static final HashMap<Direction, VoxelShape> dotShapes = new HashMap();
   private static final HashMap<Direction, Function<VoxelShape, VoxelShape>> ROTATE;

   public AntlineBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      TileEntity entity = ((TileEntityType)TileEntityTypeInit.ANTLINE.get()).func_200968_a();
      return entity;
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      return !func_220055_a(context.func_195991_k(), context.func_195995_a().func_177972_a(context.func_196000_l().func_176734_d()), context.func_196000_l()) ? null : super.func_196258_a(context);
   }

   public static ConnectionType getConnectionType(World level, BlockPos pos, Direction sideDir, Direction direction, int threshold) {
      if (!(level.func_180495_p(pos).func_177230_c() instanceof AntlineBlock)) {
         return AntlineBlock.ConnectionType.NONE;
      } else {
         BlockState adjacentState = level.func_180495_p(pos.func_177972_a(direction));
         BlockState cornerState = level.func_180495_p(pos.func_177972_a(direction).func_177972_a(sideDir));
         AntlineTileEntity selfEntity = (AntlineTileEntity)level.func_175625_s(pos);
         boolean cornerIsSolid = adjacentState.func_215686_e(level, pos.func_177972_a(direction));
         if (adjacentState.func_177230_c() instanceof AntlineBlock) {
            AntlineTileEntity adjacentEntity = (AntlineTileEntity)level.func_175625_s(pos.func_177972_a(direction));
            if (adjacentEntity.getSideMap().hasSide(sideDir) && ((AntlineTileEntity.Side)adjacentEntity.getSideMap().get(sideDir)).isConnectableWith(direction.func_176734_d()) && ((AntlineTileEntity.Side)adjacentEntity.getSideMap().get(sideDir)).countConnections() <= threshold) {
               return AntlineBlock.ConnectionType.ADJACENT;
            }
         }

         if (adjacentState.func_177230_c() instanceof AntlineConnector && ((AntlineConnector)adjacentState.func_177230_c()).getHorsedOn(adjacentState) == sideDir) {
            return AntlineBlock.ConnectionType.ELEMENT;
         } else {
            if (!cornerIsSolid && cornerState.func_177230_c() instanceof AntlineBlock) {
               AntlineTileEntity cornerEntity = (AntlineTileEntity)level.func_175625_s(pos.func_177972_a(direction).func_177972_a(sideDir));
               if (cornerEntity.getSideMap().hasSide(direction.func_176734_d()) && ((AntlineTileEntity.Side)cornerEntity.getSideMap().get(direction.func_176734_d())).isConnectableWith(sideDir.func_176734_d()) && ((AntlineTileEntity.Side)cornerEntity.getSideMap().get(direction.func_176734_d())).countConnections() <= threshold) {
                  return AntlineBlock.ConnectionType.CORNER;
               }
            }

            return selfEntity.getSideMap().hasSide(direction) && ((AntlineTileEntity.Side)selfEntity.getSideMap().get(direction)).isConnectableWith(sideDir) && ((AntlineTileEntity.Side)selfEntity.getSideMap().get(direction)).countConnections() <= threshold ? AntlineBlock.ConnectionType.SELF : AntlineBlock.ConnectionType.NONE;
         }
      }
   }

   public void recursiveSignalChain(World level, AntlineTileEntity.Side side, BlockPos pos, Direction originDirection, boolean active, int depth) {
      BlockPos trackedPos = pos.func_185334_h();
      Set<BlockPos> activePositions = (Set)ACTIVE_SIGNAL_POSITIONS.get();
      boolean addedToActive = activePositions.add(trackedPos);

      try {
         if (depth <= 256) {
            Boolean newActive = null;
            if (active == side.isActive() && originDirection != null) {
               return;
            }

            side.setActive(active);
            if (side.countConnections() < 2 && originDirection != null) {
               this.sendUpdatePacket(level, pos, side.toDirection(), (AntlineTileEntity)level.func_175625_s(pos));
               return;
            }

            boolean becameActive = active;

            for(Direction connectDirection : side.absoluteConnections()) {
               if (connectDirection.func_176740_k() != side.toDirection().func_176740_k() && connectDirection != originDirection) {
                  ConnectionType connectionType = getConnectionType(level, pos, side.toDirection(), connectDirection, 20);
                  if (connectionType != AntlineBlock.ConnectionType.NONE) {
                     Friend friend = new Friend(connectionType, pos, side.toDirection(), connectDirection);
                     if (connectionType == AntlineBlock.ConnectionType.ELEMENT) {
                        BlockState neighborState = level.func_180495_p(friend.pos);
                        Block neighborBlock = neighborState.func_177230_c();
                        if (neighborBlock instanceof AntlineActivated) {
                           ((AntlineActivated)neighborBlock).onAntlineActivation(active, neighborState, level, friend.pos);
                        }

                        if (neighborBlock instanceof AntlineActivator) {
                           newActive = ((AntlineActivator)neighborBlock).isAntlineActive(neighborState);
                           if (newActive && !active) {
                              this.recursiveSignalChain(level, side, pos, (Direction)null, newActive, 0);
                           }

                           becameActive = newActive;
                        }
                     } else {
                        AntlineTileEntity entity = (AntlineTileEntity)level.func_175625_s(friend.pos);
                        AntlineTileEntity.SideMap sideMap = entity.getSideMap();
                        this.recursiveSignalChain(level, (AntlineTileEntity.Side)sideMap.get(friend.sideDirection), friend.pos, friend.connectDirection, active || becameActive, depth + 1);
                        if (((AntlineTileEntity.Side)((AntlineTileEntity)level.func_175625_s(friend.pos)).getSideMap().get(friend.sideDirection)).isActive()) {
                           becameActive = true;
                        }
                     }
                  }
               }
            }

            this.sendUpdatePacket(level, pos, side.toDirection(), (AntlineTileEntity)level.func_175625_s(pos));
            return;
         }
      } finally {
         if (addedToActive) {
            activePositions.remove(trackedPos);
         }

      }

   }

   public void sideUpdate(World level, AntlineTileEntity.Side side, BlockPos pos, boolean allowNewConnections, boolean shift, Direction allowedConnectDirection) {
      Boolean active = null;
      Direction originDirection = null;

      for(Direction connectionDirection : Direction.values()) {
         boolean allowConnection = allowedConnectDirection == connectionDirection || allowNewConnections;
         if (side.isConnectableWith(connectionDirection) && connectionDirection.func_176740_k() != side.toDirection().func_176740_k()) {
            ConnectionType connectionType = getConnectionType(level, pos, side.toDirection(), connectionDirection, !shift && allowConnection ? 1 : 20);
            Friend friend = null;
            if (connectionType != AntlineBlock.ConnectionType.NONE) {
               friend = new Friend(connectionType, pos, side.toDirection(), connectionDirection);
            }

            boolean element = connectionType == AntlineBlock.ConnectionType.ELEMENT;
            boolean connect = connectionType != AntlineBlock.ConnectionType.NONE;
            if (side.hasConnection(connectionDirection) != connect) {
               this.connect(level, pos, side.toDirection(), connectionDirection, connect && allowConnection);
            }

            if (!element && connect && allowConnection) {
               this.connect(level, friend.pos, friend.sideDirection, friend.connectDirection, true);
            }

            if (friend != null) {
               BlockState friendState = level.func_180495_p(friend.pos);
               if (element && friendState.func_177230_c() instanceof AntlineActivator) {
                  active = ((AntlineActivator)friendState.func_177230_c()).isAntlineActive(friendState);
                  originDirection = connectionDirection;
               }
            }
         }
      }

      if (active != null) {
         this.recursiveSignalChain(level, side, pos, originDirection, active, 0);
      }

   }

   private void connect(World level, BlockPos pos, Direction side, Direction connectDir, boolean connect) {
      AntlineTileEntity tileEntity = (AntlineTileEntity)level.func_175625_s(pos);
      AntlineTileEntity.Side sideObj = (AntlineTileEntity.Side)tileEntity.getSideMap().get(side);
      if (connect) {
         sideObj.addConnection(connectDir);
      } else {
         sideObj.removeConnection(connectDir);
      }

      this.sendUpdatePacket(level, pos, side, tileEntity);
   }

   public void func_220069_a(BlockState blockState, World level, BlockPos pos, Block block, BlockPos neighborPos, boolean b) {
      super.func_220069_a(blockState, level, pos, block, neighborPos, b);
      if (level.field_72995_K || !((Set)ACTIVE_SIGNAL_POSITIONS.get()).contains(pos)) {
         BlockState neighborState = level.func_180495_p(neighborPos);
         AntlineTileEntity tileEntity = (AntlineTileEntity)level.func_175625_s(pos);
         AntlineTileEntity.SideMap sideMap = tileEntity.getSideMap();
         Direction neighborDir = Direction.func_176737_a((float)(neighborPos.func_177958_n() - pos.func_177958_n()), (float)(neighborPos.func_177956_o() - pos.func_177956_o()), (float)(neighborPos.func_177952_p() - pos.func_177952_p()));
         if (!((AntlineTileEntity.Side)sideMap.get(neighborDir)).isEmpty() && !neighborState.func_224755_d(level, pos, neighborDir.func_176734_d())) {
            this.breakDot(blockState, level, pos, (PlayerEntity)null, neighborDir);
         } else {
            for(AntlineTileEntity.Side side : sideMap.values()) {
               if (!side.isEmpty()) {
                  boolean usedToHaveConnection = side.hasConnection(neighborDir);
                  this.sideUpdate(level, side, pos, false, true, neighborState.func_177230_c() instanceof AntlineConnector ? neighborDir : null);
                  boolean hasNowConnection = side.hasConnection(neighborDir);
                  if (usedToHaveConnection && !hasNowConnection) {
                     this.recursiveSignalChain(level, side, pos, (Direction)null, false, 0);
                  }
               }
            }

         }
      }
   }

   public boolean func_196253_a(BlockState state, BlockItemUseContext context) {
      return context.func_195996_i().func_77973_b() instanceof AntlineBlockItem;
   }

   public void breakDot(BlockState state, World level, BlockPos pos, PlayerEntity player, Direction direction) {
      if (!level.field_72995_K) {
         AntlineTileEntity tileEntity = (AntlineTileEntity)level.func_175625_s(pos);
         if (tileEntity.getSideMap().getSideCount() <= 1) {
            if (player != null) {
               level.func_217378_a((PlayerEntity)null, 2001, pos, func_196246_j(state));
            }

            if (player == null || !player.func_184812_l_()) {
               func_220075_c(state, level, pos);
            }

            level.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
         } else {
            level.func_217378_a((PlayerEntity)null, 2001, pos, func_196246_j(state));
            tileEntity.getSideMap().removeSide(direction);
            if (player == null || !player.func_184812_l_()) {
               func_220075_c(state, level, pos);
            }

            if (level instanceof ServerWorld) {
               for(AntlineTileEntity.Side side : tileEntity.getSideMap().values()) {
                  if (!side.isEmpty() && side.toDirection() != direction) {
                     this.sideUpdate(level, side, pos, false, false, (Direction)null);
                  }
               }

               for(AntlineTileEntity.Side side : tileEntity.getSideMap().values()) {
                  if (!side.isEmpty() && side.toDirection() != direction) {
                     this.recursiveSignalChain(level, side, pos, (Direction)null, false, 0);
                  }
               }
            }
         }

         for(AntlineTileEntity.Side side : tileEntity.getSideMap().values()) {
            level.func_175695_a(pos.func_177972_a(side.toDirection()), state.func_177230_c(), side.toDirection().func_176734_d());
         }

         level.func_195593_d(pos, state.func_177230_c());
         this.sendUpdatePacket(level, pos, direction, tileEntity);
      }
   }

   public void sendUpdatePacket(World level, BlockPos pos, Direction sideDir, AntlineTileEntity tileEntity) {
      if (!level.field_72995_K) {
         CompoundNBT nbtA = new CompoundNBT();
         AntlineTileEntity.SideMap sideMap = tileEntity.getSideMap();
         AntlineTileEntity.Side side = (AntlineTileEntity.Side)sideMap.get(sideDir);
         nbtA.func_74774_a(sideDir.func_176742_j(), side.getActualValue());
         PacketInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.func_175726_f(pos)), new SAntlineUpdatePacket(pos, nbtA));
      }
   }

   public boolean removedByPlayer(BlockState state, World level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
      Vector3d rayPath = player.func_70676_i(0.0F).func_186678_a((double)7.0F);
      Vector3d from = player.func_174824_e(0.0F);
      Vector3d to = from.func_178787_e(rayPath);

      for(Direction direction : Direction.values()) {
         BlockRayTraceResult rayHit = this.getSideShape(level, pos, direction, isHoldingAntline(player)).func_212433_a(from, to, pos);
         if (rayHit != null && rayHit.func_216346_c() == Type.BLOCK) {
            this.breakDot(state, level, pos, player, direction);
            break;
         }
      }

      return false;
   }

   public static boolean isHoldingAntline(PlayerEntity player) {
      return player.func_184614_ca().func_77973_b() instanceof AntlineBlockItem;
   }

   private static void initializeRotateMap() {
      ROTATE.put(Direction.NORTH, (Function)(shape) -> {
         AxisAlignedBB bb = shape.func_197751_a((double)-0.5F, (double)-0.5F, (double)-0.5F).func_197752_a();
         return Block.func_208617_a(bb.field_72340_a * (double)16.0F, -bb.field_72339_c * (double)16.0F, bb.field_72338_b * (double)16.0F, bb.field_72336_d * (double)16.0F, -bb.field_72334_f * (double)16.0F, bb.field_72337_e * (double)16.0F).func_197751_a((double)0.5F, (double)0.5F, (double)0.5F);
      });
      ROTATE.put(Direction.SOUTH, (Function)(shape) -> {
         AxisAlignedBB bb = ((VoxelShape)((Function)ROTATE.get(Direction.NORTH)).apply(shape)).func_197751_a((double)-0.5F, (double)-0.5F, (double)-0.5F).func_197752_a();
         return Block.func_208617_a(-bb.field_72340_a * (double)16.0F, bb.field_72338_b * (double)16.0F, -bb.field_72339_c * (double)16.0F, -bb.field_72336_d * (double)16.0F, bb.field_72337_e * (double)16.0F, -bb.field_72334_f * (double)16.0F).func_197751_a((double)0.5F, (double)0.5F, (double)0.5F);
      });
      ROTATE.put(Direction.EAST, (Function)(shape) -> {
         AxisAlignedBB bb = ((VoxelShape)((Function)ROTATE.get(Direction.NORTH)).apply(shape)).func_197751_a((double)-0.5F, (double)-0.5F, (double)-0.5F).func_197752_a();
         return Block.func_208617_a(-bb.field_72339_c * (double)16.0F, bb.field_72338_b * (double)16.0F, bb.field_72340_a * (double)16.0F, -bb.field_72334_f * (double)16.0F, bb.field_72337_e * (double)16.0F, bb.field_72336_d * (double)16.0F).func_197751_a((double)0.5F, (double)0.5F, (double)0.5F);
      });
      ROTATE.put(Direction.WEST, (Function)(shape) -> {
         AxisAlignedBB bb = ((VoxelShape)((Function)ROTATE.get(Direction.NORTH)).apply(shape)).func_197751_a((double)-0.5F, (double)-0.5F, (double)-0.5F).func_197752_a();
         return Block.func_208617_a(bb.field_72339_c * (double)16.0F, bb.field_72338_b * (double)16.0F, -bb.field_72340_a * (double)16.0F, bb.field_72334_f * (double)16.0F, bb.field_72337_e * (double)16.0F, -bb.field_72336_d * (double)16.0F).func_197751_a((double)0.5F, (double)0.5F, (double)0.5F);
      });
      ROTATE.put(Direction.UP, (Function)(shape) -> {
         AxisAlignedBB bb = shape.func_197751_a((double)-0.5F, (double)-0.5F, (double)-0.5F).func_197752_a();
         return Block.func_208617_a(bb.field_72340_a * (double)16.0F, -bb.field_72338_b * (double)16.0F, bb.field_72339_c * (double)16.0F, bb.field_72336_d * (double)16.0F, -bb.field_72337_e * (double)16.0F, bb.field_72334_f * (double)16.0F).func_197751_a((double)0.5F, (double)0.5F, (double)0.5F);
      });
      ROTATE.put(Direction.DOWN, (Function)(shape) -> shape);
   }

   public VoxelShape getSideShape(IBlockReader level, BlockPos pos, Direction sideDirection, boolean largeShape) {
      TileEntity blockEntity = level.func_175625_s(pos);
      if (blockEntity != null && sideDirection != null) {
         AntlineTileEntity.SideMap sideMap = ((AntlineTileEntity)blockEntity).getSideMap();
         if (sideMap == null) {
            return VoxelShapes.func_197880_a();
         } else {
            AntlineTileEntity.Side side = (AntlineTileEntity.Side)sideMap.get(sideDirection);
            AtomicReference<VoxelShape> RESULT = new AtomicReference(VoxelShapes.func_197880_a());
            if (side.getSideType() != AntlineTileEntity.Side.SideType.NONE && DOT != null) {
               RESULT.set(VoxelShapes.func_197872_a((VoxelShape)RESULT.get(), (VoxelShape)((Function)ROTATE.get(sideDirection)).apply(DOT)));
            }

            if (largeShape) {
               if (!side.isEmpty()) {
                  RESULT.set(((Function)ROTATE.get(sideDirection)).apply(WHOLE_SIDE));
               }
            } else {
               side.getConnections().forEach((direction, b) -> {
                  if (b && dotShapes.get(direction) != null) {
                     RESULT.set(VoxelShapes.func_197872_a((VoxelShape)RESULT.get(), (VoxelShape)((Function)ROTATE.get(sideDirection)).apply(dotShapes.get(direction))));
                  }

               });
            }

            return (VoxelShape)RESULT.get();
         }
      } else {
         return VoxelShapes.func_197880_a();
      }
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
      if (level.func_175625_s(pos) != null && ((AntlineTileEntity)level.func_175625_s(pos)).getSideMap() != null) {
         AtomicReference<Entity> entity = new AtomicReference(context.getEntity());
         if (entity.get() == null) {
            if (!((World)level).field_72995_K) {
               return VoxelShapes.func_197880_a();
            }

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> entity.set(ClientModUtil.getLocalPlayer()));
         }

         Vector3d rayPath = ((Entity)entity.get()).func_70676_i(0.0F).func_186678_a((double)7.0F);
         Vector3d from = ((Entity)entity.get()).func_174824_e(0.0F);
         Vector3d to = from.func_178787_e(rayPath);

         for(Direction direction : Direction.values()) {
            boolean holdingAntline = entity.get() instanceof PlayerEntity && isHoldingAntline((PlayerEntity)entity.get());
            VoxelShape shape = this.getSideShape(level, pos, direction, holdingAntline);
            BlockRayTraceResult rayHit = shape.func_212433_a(from, to, pos);
            if (rayHit != null && rayHit.func_216346_c() == Type.BLOCK) {
               return shape;
            }
         }

         AtomicReference<VoxelShape> RESULT = new AtomicReference(VoxelShapes.func_197880_a());
         AntlineTileEntity.SideMap sideMap = ((AntlineTileEntity)level.func_175625_s(pos)).getSideMap();
         sideMap.forEach((directionx, side) -> {
            if (!side.isEmpty()) {
               RESULT.set(VoxelShapes.func_197872_a((VoxelShape)RESULT.get(), (VoxelShape)((Function)ROTATE.get(directionx)).apply(DOT)));
            }

         });
         return (VoxelShape)RESULT.get();
      } else {
         return VoxelShapes.func_197880_a();
      }
   }

   public boolean func_200123_i(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   static {
      dotShapes.put(Direction.NORTH, DOT.func_197751_a((double)0.0F, (double)0.0F, (double)-0.3125F));
      dotShapes.put(Direction.SOUTH, DOT.func_197751_a((double)0.0F, (double)0.0F, (double)0.3125F));
      dotShapes.put(Direction.WEST, DOT.func_197751_a((double)-0.3125F, (double)0.0F, (double)0.0F));
      dotShapes.put(Direction.EAST, DOT.func_197751_a((double)0.3125F, (double)0.0F, (double)0.0F));
      ROTATE = new HashMap();
      initializeRotateMap();
   }

   public static enum ConnectionType {
      SELF,
      ADJACENT,
      CORNER,
      ELEMENT,
      NONE;
   }

   private static class Friend {
      public BlockPos pos;
      public Direction sideDirection;
      public Direction connectDirection;

      private Friend(ConnectionType type, BlockPos pos, Direction sideDirection, Direction connectionDirection) {
         this.pos = null;
         this.sideDirection = null;
         this.connectDirection = null;
         switch (type) {
            case SELF:
               this.pos = pos;
               this.connectDirection = sideDirection;
               this.sideDirection = connectionDirection;
               break;
            case ADJACENT:
               this.pos = pos.func_177972_a(connectionDirection);
               this.connectDirection = connectionDirection.func_176734_d();
               this.sideDirection = sideDirection;
               break;
            case CORNER:
               this.pos = pos.func_177972_a(connectionDirection).func_177972_a(sideDirection);
               this.connectDirection = sideDirection.func_176734_d();
               this.sideDirection = connectionDirection.func_176734_d();
               break;
            case ELEMENT:
               this.pos = pos.func_177972_a(connectionDirection);
               this.sideDirection = sideDirection;
               this.connectDirection = connectionDirection.func_176734_d();
         }

      }
   }
}
