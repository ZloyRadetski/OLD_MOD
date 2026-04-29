package net.portalmod.common.sorted.sign;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.portalmod.common.items.WrenchItem;
import net.portalmod.common.sorted.cube.Cube;
import net.portalmod.core.init.EntityInit;
import net.portalmod.core.init.ItemInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.packet.SSpawnChamberSignPacket;
import net.portalmod.core.util.ModUtil;

public class ChamberSignEntity extends HangingEntity {
   public static final DataParameter<Integer> DATA_LEFT_DIGIT;
   public static final DataParameter<Integer> DATA_RIGHT_DIGIT;
   public static final DataParameter<Integer> DATA_PROGRESS;
   public static final DataParameter<Boolean> DATA_ICON_1;
   public static final DataParameter<Boolean> DATA_ICON_2;
   public static final DataParameter<Boolean> DATA_ICON_3;
   public static final DataParameter<Boolean> DATA_ICON_4;
   public static final DataParameter<Boolean> DATA_ICON_5;
   public static final DataParameter<Boolean> DATA_ICON_6;
   public static final DataParameter<Boolean> DATA_ICON_7;
   public static final DataParameter<Boolean> DATA_ICON_8;
   public static final DataParameter<Boolean> DATA_ENABLED;
   private boolean verticallyAligned;

   public ChamberSignEntity(EntityType<? extends HangingEntity> p_i48561_1_, World p_i48561_2_) {
      super(p_i48561_1_, p_i48561_2_);
   }

   public ChamberSignEntity(World world, BlockPos pos, Direction direction, boolean verticallyAligned) {
      super((EntityType)EntityInit.CHAMBER_SIGN.get(), world, pos);
      this.func_174859_a(direction);
      this.setVerticallyAligned(verticallyAligned);
      this.func_174856_o();
   }

   public void func_70071_h_() {
      if (this.field_70173_aa == 1) {
         this.func_174856_o();
      }

      super.func_70071_h_();
      boolean powered = this.isPowered();
      if (powered == this.getEnabled()) {
         this.setEnabled(!powered);
      }

   }

   public boolean isPowered() {
      Stream<BlockPos> boundingBoxPositions = BlockPos.func_239581_a_(this.func_174813_aQ().func_186664_h(0.001));
      return boundingBoxPositions.anyMatch((pos) -> this.field_70170_p.func_175640_z(pos));
   }

   public ActionResultType func_184199_a(PlayerEntity player, Vector3d pos, Hand hand) {
      if (!(player.func_184586_b(hand).func_77973_b() instanceof WrenchItem)) {
         return ActionResultType.PASS;
      } else {
         int inverted = this.field_174860_b != Direction.NORTH && this.field_174860_b != Direction.EAST ? 1 : -1;
         double pixelXCoordinate = pos.func_216370_a(this.field_174860_b.func_176746_e().func_176740_k()) * (double)16.0F * (double)inverted + (double)12.0F;
         double pixelYCoordinate = pos.field_72448_b * (double)16.0F + (double)24.0F;
         if (pixelYCoordinate > (double)21.5F) {
            int increment = player.func_225608_bj_() ? -1 : 1;
            if (pixelXCoordinate < (double)11.0F) {
               this.setLeftDigit(Math.floorMod(this.getLeftDigit() + increment, 10));
            } else {
               this.setRightDigit(Math.floorMod(this.getRightDigit() + increment, 10));
            }
         } else if (pixelYCoordinate > (double)15.5F) {
            this.setProgress((int)Math.floor(pixelXCoordinate / (double)2.0F - (double)1.0F));
         } else {
            int x = pixelXCoordinate < (double)7.5F ? 0 : (pixelXCoordinate < (double)12.0F ? 1 : (pixelXCoordinate < (double)16.5F ? 2 : 3));
            int y = pixelYCoordinate > (double)9.5F ? 0 : 1;
            this.toggleIcon(x + y * 4);
         }

         WrenchItem.playUseSound(this.field_70170_p, this.func_213303_ch().func_178787_e(pos));
         return ActionResultType.SUCCESS;
      }
   }

   public void toggleIcon(int index) {
      if (index == 0) {
         this.setIcon1(!this.getIcon1());
      } else if (index == 1) {
         this.setIcon2(!this.getIcon2());
      } else if (index == 2) {
         this.setIcon3(!this.getIcon3());
      } else if (index == 3) {
         this.setIcon4(!this.getIcon4());
      } else if (index == 4) {
         this.setIcon5(!this.getIcon5());
      } else if (index == 5) {
         this.setIcon6(!this.getIcon6());
      } else if (index == 6) {
         this.setIcon7(!this.getIcon7());
      } else if (index == 7) {
         this.setIcon8(!this.getIcon8());
      }

   }

   public ItemStack getPickedResult(RayTraceResult target) {
      return new ItemStack((IItemProvider)ItemInit.CHAMBER_SIGN.get());
   }

   public boolean func_70097_a(DamageSource damageSource, float damage) {
      Entity entity = damageSource.func_76346_g();
      return entity instanceof PlayerEntity && !((PlayerEntity)entity).func_184812_l_() && !(((PlayerEntity)entity).func_184614_ca().func_77973_b() instanceof WrenchItem) ? false : super.func_70097_a(damageSource, damage);
   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(DATA_LEFT_DIGIT, 0);
      this.func_184212_Q().func_187214_a(DATA_RIGHT_DIGIT, 0);
      this.func_184212_Q().func_187214_a(DATA_PROGRESS, 6);
      this.func_184212_Q().func_187214_a(DATA_ICON_1, true);
      this.func_184212_Q().func_187214_a(DATA_ICON_2, false);
      this.func_184212_Q().func_187214_a(DATA_ICON_3, true);
      this.func_184212_Q().func_187214_a(DATA_ICON_4, false);
      this.func_184212_Q().func_187214_a(DATA_ICON_5, false);
      this.func_184212_Q().func_187214_a(DATA_ICON_6, false);
      this.func_184212_Q().func_187214_a(DATA_ICON_7, true);
      this.func_184212_Q().func_187214_a(DATA_ICON_8, true);
      this.func_184212_Q().func_187214_a(DATA_ENABLED, true);
   }

   protected void func_174856_o() {
      if (this.field_174860_b != null) {
         double x = (double)this.field_174861_a.func_177958_n() + (double)0.5F;
         double y = (double)this.field_174861_a.func_177956_o() + (double)0.5F;
         double z = (double)this.field_174861_a.func_177952_p() + (double)0.5F;
         double widthOffset = this.func_190202_a(this.func_82329_d()) + (double)0.5F;
         double heightOffset = this.func_190202_a(this.func_82330_g()) + (this.verticallyAligned ? (double)0.0F : (double)0.5F);
         x -= (double)this.field_174860_b.func_82601_c() * (double)0.4375F;
         z -= (double)this.field_174860_b.func_82599_e() * (double)0.4375F;
         y += heightOffset;
         Direction direction = this.field_174860_b.func_176735_f();
         x += widthOffset * (double)direction.func_82601_c();
         z += widthOffset * (double)direction.func_82599_e();
         this.func_226288_n_(x, y, z);
         double dx = (double)this.func_82329_d();
         double dy = (double)this.func_82330_g();
         double dz = (double)this.func_82329_d();
         if (this.field_174860_b.func_176740_k() == Axis.Z) {
            dz = (double)2.0F;
         } else {
            dx = (double)2.0F;
         }

         dx /= (double)32.0F;
         dy /= (double)32.0F;
         dz /= (double)32.0F;
         this.func_174826_a(new AxisAlignedBB(x - dx, y - dy, z - dz, x + dx, y + dy, z + dz));
      }

   }

   public boolean func_70518_d() {
      boolean survives = super.func_70518_d();
      if (!survives) {
         return false;
      } else {
         Direction sideDirection = this.field_174860_b.func_176735_f();
         BlockPos wallPos = this.field_174861_a.func_177972_a(this.field_174860_b.func_176734_d());
         BlockPos sidePos = wallPos.func_177972_a(sideDirection);

         for(int i = -1; i < 2; ++i) {
            if (!this.isValidWall(sidePos.func_177967_a(Direction.UP, i))) {
               return false;
            }
         }

         if (this.verticallyAligned) {
            return true;
         } else {
            BlockPos above = wallPos.func_177981_b(2);
            return this.isValidWall(above) && this.isValidWall(above.func_177967_a(sideDirection, 1));
         }
      }
   }

   public boolean func_241849_j(Entity entity) {
      return !(entity instanceof Cube) && !(entity instanceof BoatEntity) && super.func_241849_j(entity);
   }

   public boolean isValidWall(BlockPos pos) {
      BlockState blockstate = this.field_70170_p.func_180495_p(pos);
      return Block.func_220055_a(this.field_70170_p, pos, this.field_174860_b) || blockstate.func_185904_a().func_76220_a() || RedstoneDiodeBlock.func_185546_B(blockstate);
   }

   public double func_190202_a(int p_190202_1_) {
      return p_190202_1_ % 32 == 0 ? (double)0.5F : (double)0.0F;
   }

   public int func_82329_d() {
      return 24;
   }

   public int func_82330_g() {
      return 48;
   }

   public void func_110128_b(@Nullable Entity entity) {
      this.func_184185_a((SoundEvent)SoundInit.CHAMBER_SIGN_PLACE.get(), 1.0F, ModUtil.randomSlightSoundPitch() * 0.9F);
      if ((!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).func_184812_l_()) && this.field_70170_p.func_82736_K().func_223586_b(GameRules.field_223604_g)) {
         this.func_199703_a((IItemProvider)ItemInit.CHAMBER_SIGN.get());
      }
   }

   public void func_184523_o() {
      this.func_184185_a((SoundEvent)SoundInit.CHAMBER_SIGN_PLACE.get(), 1.0F, ModUtil.randomSlightSoundPitch());
   }

   public IPacket<?> func_213297_N() {
      return PacketInit.INSTANCE.toVanillaPacket(new SSpawnChamberSignPacket(this.func_145782_y(), this.func_110124_au(), this.func_174857_n(), this.field_174860_b, this.verticallyAligned), NetworkDirection.PLAY_TO_CLIENT);
   }

   public void func_213281_b(CompoundNBT nbt) {
      super.func_213281_b(nbt);
      nbt.func_74774_a("Facing", (byte)this.field_174860_b.func_176736_b());
      nbt.func_74768_a("LeftDigit", this.getLeftDigit());
      nbt.func_74768_a("RightDigit", this.getRightDigit());
      nbt.func_74768_a("Progress", this.getProgress());
      nbt.func_74757_a("Icon1", this.getIcon1());
      nbt.func_74757_a("Icon2", this.getIcon2());
      nbt.func_74757_a("Icon3", this.getIcon3());
      nbt.func_74757_a("Icon4", this.getIcon4());
      nbt.func_74757_a("Icon5", this.getIcon5());
      nbt.func_74757_a("Icon6", this.getIcon6());
      nbt.func_74757_a("Icon7", this.getIcon7());
      nbt.func_74757_a("Icon8", this.getIcon8());
      nbt.func_74757_a("Aligned", this.getVerticallyAligned());
   }

   public void func_70037_a(CompoundNBT nbt) {
      super.func_70037_a(nbt);
      this.func_174859_a(Direction.func_176731_b(nbt.func_74771_c("Facing")));
      this.setLeftDigit(nbt.func_74762_e("LeftDigit"));
      this.setRightDigit(nbt.func_74762_e("RightDigit"));
      this.setProgress(nbt.func_74762_e("Progress"));
      this.setIcon1(nbt.func_74767_n("Icon1"));
      this.setIcon2(nbt.func_74767_n("Icon2"));
      this.setIcon3(nbt.func_74767_n("Icon3"));
      this.setIcon4(nbt.func_74767_n("Icon4"));
      this.setIcon5(nbt.func_74767_n("Icon5"));
      this.setIcon6(nbt.func_74767_n("Icon6"));
      this.setIcon7(nbt.func_74767_n("Icon7"));
      this.setIcon8(nbt.func_74767_n("Icon8"));
      this.setVerticallyAligned(nbt.func_74767_n("Aligned"));
   }

   public int getLeftDigit() {
      return (Integer)this.func_184212_Q().func_187225_a(DATA_LEFT_DIGIT);
   }

   public void setLeftDigit(int leftDigit) {
      this.func_184212_Q().func_187227_b(DATA_LEFT_DIGIT, leftDigit % 10);
   }

   public int getRightDigit() {
      return (Integer)this.func_184212_Q().func_187225_a(DATA_RIGHT_DIGIT);
   }

   public void setRightDigit(int rightDigit) {
      this.func_184212_Q().func_187227_b(DATA_RIGHT_DIGIT, rightDigit % 10);
   }

   public int getProgress() {
      return (Integer)this.func_184212_Q().func_187225_a(DATA_PROGRESS);
   }

   public void setProgress(int progress) {
      this.func_184212_Q().func_187227_b(DATA_PROGRESS, progress);
   }

   public boolean getIcon1() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_1);
   }

   public void setIcon1(boolean icon1) {
      this.func_184212_Q().func_187227_b(DATA_ICON_1, icon1);
   }

   public boolean getIcon2() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_2);
   }

   public void setIcon2(boolean icon2) {
      this.func_184212_Q().func_187227_b(DATA_ICON_2, icon2);
   }

   public boolean getIcon3() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_3);
   }

   public void setIcon3(boolean icon3) {
      this.func_184212_Q().func_187227_b(DATA_ICON_3, icon3);
   }

   public boolean getIcon4() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_4);
   }

   public void setIcon4(boolean icon4) {
      this.func_184212_Q().func_187227_b(DATA_ICON_4, icon4);
   }

   public boolean getIcon5() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_5);
   }

   public void setIcon5(boolean icon5) {
      this.func_184212_Q().func_187227_b(DATA_ICON_5, icon5);
   }

   public boolean getIcon6() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_6);
   }

   public void setIcon6(boolean icon6) {
      this.func_184212_Q().func_187227_b(DATA_ICON_6, icon6);
   }

   public boolean getIcon7() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_7);
   }

   public void setIcon7(boolean icon7) {
      this.func_184212_Q().func_187227_b(DATA_ICON_7, icon7);
   }

   public boolean getIcon8() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ICON_8);
   }

   public void setIcon8(boolean icon8) {
      this.func_184212_Q().func_187227_b(DATA_ICON_8, icon8);
   }

   public boolean getEnabled() {
      return (Boolean)this.func_184212_Q().func_187225_a(DATA_ENABLED);
   }

   public void setEnabled(boolean enabled) {
      this.func_184212_Q().func_187227_b(DATA_ENABLED, enabled);
   }

   public boolean getVerticallyAligned() {
      return this.verticallyAligned;
   }

   public void setVerticallyAligned(boolean verticallyAligned) {
      this.verticallyAligned = verticallyAligned;
   }

   static {
      DATA_LEFT_DIGIT = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187192_b);
      DATA_RIGHT_DIGIT = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187192_b);
      DATA_PROGRESS = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187192_b);
      DATA_ICON_1 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ICON_2 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ICON_3 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ICON_4 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ICON_5 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ICON_6 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ICON_7 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ICON_8 = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
      DATA_ENABLED = EntityDataManager.func_187226_a(ChamberSignEntity.class, DataSerializers.field_187198_h);
   }
}
