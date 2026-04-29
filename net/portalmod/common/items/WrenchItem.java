package net.portalmod.common.items;

import java.util.List;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.portalmod.common.sorted.faithplate.CFaithPlateEndConfigPacket;
import net.portalmod.common.sorted.faithplate.CFaithPlateUpdatedPacket;
import net.portalmod.common.sorted.faithplate.FaithPlateTER;
import net.portalmod.common.sorted.faithplate.FaithPlateTileEntity;
import net.portalmod.common.sorted.trigger.TriggerSelectionClient;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class WrenchItem extends Item {
   public WrenchItem(Item.Properties properties) {
      super(properties);
   }

   public static void playUseSound(PlayerEntity player, World world, Vector3d location) {
      world.func_184148_a(player, location.field_72450_a, location.field_72448_b, location.field_72449_c, (SoundEvent)SoundInit.WRENCH_USE.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSoundPitch());
   }

   public static void playFailSound(PlayerEntity player, World world, Vector3d location) {
      world.func_184148_a(player, location.field_72450_a, location.field_72448_b, location.field_72449_c, (SoundEvent)SoundInit.WRENCH_FAIL.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSoundPitch());
   }

   public static void playUseSound(World world, Vector3d location) {
      playUseSound((PlayerEntity)null, world, location);
   }

   public static void playFailSound(World world, Vector3d location) {
      playFailSound((PlayerEntity)null, world, location);
   }

   public static boolean holdingWrench(Entity entity) {
      return StreamSupport.stream(entity.func_184214_aD().spliterator(), false).anyMatch((itemStack) -> itemStack.func_77973_b() instanceof WrenchItem);
   }

   public static boolean usedWrench(LivingEntity entity, Hand hand) {
      return entity.func_184586_b(hand).func_77973_b() instanceof WrenchItem;
   }

   public static boolean hitWithWrench(LivingEntity entity) {
      return entity.func_184614_ca().func_77973_b() instanceof WrenchItem;
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      BlockRayTraceResult rayHit = ModUtil.rayTraceBlock(player, world, 64);
      Direction clickFace = rayHit.func_216354_b();
      Vector3d clickPos = rayHit.func_216347_e();
      ItemStack itemStack = player.func_184586_b(hand);
      if (!world.field_72995_K) {
         return super.func_77659_a(world, player, hand);
      } else if (FaithPlateTER.selected == null) {
         if (TriggerSelectionClient.isSelecting()) {
            playUseSound(player, world, player.func_213303_ch());
            TriggerSelectionClient.confirmSelection();
            return ActionResult.func_226248_a_(itemStack);
         } else {
            return ActionResult.func_226251_d_(itemStack);
         }
      } else {
         BlockPos selected = FaithPlateTER.selected;
         TileEntity blockEntity = world.func_175625_s(selected);
         if (!(blockEntity instanceof FaithPlateTileEntity)) {
            return ActionResult.func_226251_d_(itemStack);
         } else {
            FaithPlateTileEntity be = (FaithPlateTileEntity)blockEntity;
            clickPos = getTargetPos(clickFace, clickPos);
            boolean enabled = false;
            if (be.getTargetPos() == null) {
               be.setHeight(be.getPredictedHeight(clickPos));
               enabled = true;
            }

            CompoundNBT nbt = new CompoundNBT();
            CompoundNBT target = new CompoundNBT();
            target.func_74776_a("height", be.getHeight());
            nbt.func_74757_a("enabled", enabled || be.isEnabled());
            target.func_74774_a("side", (byte)clickFace.func_176745_a());
            target.func_74780_a("x", clickPos.func_82615_a() - (double)selected.func_177958_n());
            target.func_74780_a("y", clickPos.func_82617_b() - (double)selected.func_177956_o());
            target.func_74780_a("z", clickPos.func_82616_c() - (double)selected.func_177952_p());
            nbt.func_218657_a("target", target);
            be.load(nbt);
            PacketInit.INSTANCE.sendToServer(new CFaithPlateUpdatedPacket(selected, nbt));
            PacketInit.INSTANCE.sendToServer(new CFaithPlateEndConfigPacket(selected));
            FaithPlateTER.selected = null;
            playUseSound(world, player.func_213303_ch());
            return ActionResult.func_226248_a_(itemStack);
         }
      }
   }

   @Nonnull
   public static Vector3d getTargetPos(Direction clickFace, Vector3d clickPos) {
      clickPos = (new Vector3d((double)Math.round(clickPos.func_82615_a() * (double)2.0F) / (double)2.0F, (double)Math.round(clickPos.func_82617_b() * (double)2.0F) / (double)2.0F, (double)Math.round(clickPos.func_82616_c() * (double)2.0F) / (double)2.0F)).func_178786_a((double)0.5F + (double)clickFace.func_82601_c() * (double)0.5F, (double)0.5F + (double)clickFace.func_96559_d() * (double)0.5F, (double)0.5F + (double)clickFace.func_82599_e() * (double)0.5F);
      return clickPos;
   }

   public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
      return true;
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("wrench", list);
   }
}
