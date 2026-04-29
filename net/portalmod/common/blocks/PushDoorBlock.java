package net.portalmod.common.blocks;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.portalmod.common.sorted.portalgun.CPortalGunInteractionPacket;
import net.portalmod.common.sorted.portalgun.PortalGunInteraction;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class PushDoorBlock extends DoorBlock implements InteractKeyInteractable {
   private void playCloseSound(World world, BlockPos pos) {
      world.func_184133_a((PlayerEntity)null, pos, (SoundEvent)SoundInit.PUSH_DOOR_CLOSE.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSoundPitch());
   }

   private void playOpenSound(World world, BlockPos pos) {
      world.func_184133_a((PlayerEntity)null, pos, (SoundEvent)SoundInit.PUSH_DOOR_OPEN.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSoundPitch());
   }

   public ActionResultType func_225533_a_(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
      return this.interact(blockState, world, blockPos, blockRayTraceResult) ? ActionResultType.func_233537_a_(world.field_72995_K) : ActionResultType.PASS;
   }

   public boolean interact(BlockState blockState, World world, BlockPos blockPos, BlockRayTraceResult blockRayTraceResult) {
      Direction clickedFace = blockRayTraceResult.func_216354_b();
      Direction facing = (Direction)blockState.func_177229_b(field_176520_a);
      Direction doorFront = !(Boolean)blockState.func_177229_b(field_176519_b) ? facing.func_176734_d() : (blockState.func_177229_b(field_176521_M) == DoorHingeSide.LEFT ? facing.func_176746_e() : facing.func_176735_f());
      if (clickedFace == doorFront) {
         if (!(Boolean)blockState.func_177229_b(field_176519_b)) {
            this.open(blockState, world, blockPos);
         }

         if ((Boolean)blockState.func_177229_b(field_176519_b)) {
            this.close(blockState, world, blockPos);
         }

         world.func_205220_G_().func_205360_a(blockPos, this, 20);
         return true;
      } else {
         return false;
      }
   }

   public boolean interactKeyInteract(PlayerEntity player, BlockRayTraceResult rayHit) {
      if (this.withinInteractRange(player, rayHit)) {
         PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.OPEN_DOOR)).blockHit(rayHit).build());
         return true;
      } else {
         return false;
      }
   }

   public PushDoorBlock(AbstractBlock.Properties p_i48413_1_) {
      super(p_i48413_1_);
   }

   public void func_180658_a(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      super.func_180658_a(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_);
   }

   public void func_225534_a_(BlockState blockState, ServerWorld world, BlockPos blockPos, Random random) {
      boolean playerNearby = !world.func_175647_a(PlayerEntity.class, (new AxisAlignedBB(blockPos)).func_186662_g((double)1.0F), (player) -> !player.func_175149_v()).isEmpty();
      if (playerNearby) {
         world.func_205220_G_().func_205360_a(blockPos, this, 5);
      } else {
         if ((Boolean)blockState.func_177229_b(field_176519_b)) {
            this.close(blockState, world, blockPos);
         }

      }
   }

   public void func_220069_a(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      boolean powered = world.func_175640_z(pos) || world.func_175640_z(pos.func_177972_a(state.func_177229_b(field_176523_O) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
      if (powered != (Boolean)state.func_177229_b(field_176522_N)) {
         if (powered != (Boolean)state.func_177229_b(field_176519_b)) {
            if (powered) {
               this.playOpenSound(world, pos);
            } else {
               this.playCloseSound(world, pos);
            }
         }

         world.func_180501_a(pos, (BlockState)((BlockState)state.func_206870_a(field_176522_N, powered)).func_206870_a(field_176519_b, powered), 2);
      }

   }

   private void open(BlockState blockState, World world, BlockPos blockPos) {
      blockState = (BlockState)blockState.func_235896_a_(field_176519_b);
      world.func_180501_a(blockPos, blockState, 10);
      this.playOpenSound(world, blockPos);
   }

   private void close(BlockState blockState, World world, BlockPos blockPos) {
      if (!(Boolean)blockState.func_177229_b(field_176522_N)) {
         blockState = (BlockState)blockState.func_235896_a_(field_176519_b);
         world.func_180501_a(blockPos, blockState, 10);
         this.playCloseSound(world, blockPos);
      }
   }

   public void func_190948_a(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("push_door", list);
   }
}
