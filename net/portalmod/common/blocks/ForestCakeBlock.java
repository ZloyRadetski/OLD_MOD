package net.portalmod.common.blocks;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.portalmod.core.init.ParticleInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;

public class ForestCakeBlock extends CakeBlock {
   public static final VoxelShape CANDLE = Block.func_208617_a((double)7.0F, (double)8.0F, (double)7.0F, (double)9.0F, (double)14.0F, (double)9.0F);
   public static final VoxelShape BASE = Block.func_208617_a((double)2.0F, (double)0.0F, (double)2.0F, (double)14.0F, (double)8.0F, (double)14.0F);
   public static final VoxelShape HALF = Block.func_208617_a((double)2.0F, (double)0.0F, (double)2.0F, (double)10.0F, (double)8.0F, (double)14.0F);
   public static final VoxelShape QUARTER = Block.func_208617_a((double)2.0F, (double)0.0F, (double)2.0F, (double)10.0F, (double)8.0F, (double)8.0F);
   public static final VoxelShape PART_1 = Block.func_208617_a((double)10.0F, (double)0.0F, (double)8.0F, (double)14.0F, (double)8.0F, (double)14.0F);
   public static final VoxelShape PART_2 = Block.func_208617_a((double)2.0F, (double)0.0F, (double)8.0F, (double)6.0F, (double)8.0F, (double)14.0F);
   public static final VoxelShape PART_3 = Block.func_208617_a((double)6.0F, (double)0.0F, (double)2.0F, (double)10.0F, (double)8.0F, (double)8.0F);

   public ForestCakeBlock(AbstractBlock.Properties p_i48434_1_) {
      super(p_i48434_1_);
   }

   public ActionResultType func_225533_a_(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
      ActionResultType used = super.func_225533_a_(blockState, world, pos, player, hand, result);
      if (used == ActionResultType.SUCCESS) {
         boolean ateCandle = (Integer)blockState.func_177229_b(field_176589_a) == 0;
         world.func_184133_a(player, pos, ateCandle ? (SoundEvent)SoundInit.CAKE_EAT_CANDLE.get() : SoundEvents.field_187537_bA, SoundCategory.PLAYERS, 1.0F, ModUtil.randomSoundPitch());
      }

      return used;
   }

   public VoxelShape func_220053_a(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch ((Integer)blockState.func_177229_b(field_176589_a)) {
         case 0:
            return VoxelShapes.func_197872_a(BASE, CANDLE);
         case 1:
            return BASE;
         case 2:
            return VoxelShapes.func_197872_a(HALF, PART_1);
         case 3:
            return HALF;
         case 4:
            return VoxelShapes.func_197872_a(QUARTER, PART_2);
         case 5:
            return QUARTER;
         case 6:
            return PART_3;
         default:
            return super.func_220053_a(blockState, p_220053_2_, p_220053_3_, p_220053_4_);
      }
   }

   public void func_180655_c(BlockState blockState, World level, BlockPos pos, Random randomSource) {
      if ((Integer)blockState.func_177229_b(field_176589_a) <= 0) {
         Vec3 vec3 = (new Vec3((double)0.5F, (double)1.0F, (double)0.5F)).add((Vector3i)pos);
         float f = randomSource.nextFloat();
         if (f < 0.3F) {
            level.func_195594_a(ParticleTypes.field_197601_L, vec3.x, vec3.y, vec3.z, (double)0.0F, (double)0.0F, (double)0.0F);
         }

         level.func_195594_a((IParticleData)ParticleInit.SMALL_FLAME.get(), vec3.x, vec3.y, vec3.z, (double)0.0F, (double)0.0F, (double)0.0F);
      }
   }
}
