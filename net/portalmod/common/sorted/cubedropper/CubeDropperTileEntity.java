package net.portalmod.common.sorted.cubedropper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.sorted.antline.indicator.IndicatorActivated;
import net.portalmod.common.sorted.antline.indicator.IndicatorInfo;
import net.portalmod.core.init.TileEntityTypeInit;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.ModUtil;

public class CubeDropperTileEntity extends TileEntity implements ITickableTileEntity, IndicatorActivated {
   public List<UUID> entityUUIDs = new ArrayList();
   public int openTicks = 0;
   public boolean wasActive = false;
   public CompoundNBT entityNBT = null;

   public CubeDropperTileEntity() {
      super((TileEntityType)TileEntityTypeInit.CUBE_DROPPER.get());
   }

   public void func_73660_a() {
      BlockState blockState = this.func_195044_w();
      BlockPos pos = this.func_174877_v();
      CubeDropperBlock dropperBlock = (CubeDropperBlock)blockState.func_177230_c();
      IndicatorInfo indicatorInfo = this.checkIndicators(blockState, this.field_145850_b, pos);
      boolean active = !indicatorInfo.hasIndicators || indicatorInfo.allIndicatorsActivated;
      if (this.openTicks > 0) {
         ++this.openTicks;
      }

      if (blockState.func_177230_c() instanceof CubeDropperBlock && this.entityNBT != null) {
         this.updateEntities();
         if (active && this.openTicks == 0 && (this.entityUUIDs.size() == 1 || !this.wasActive)) {
            this.openDropper(dropperBlock);
         }
      }

      this.wasActive = active;
      if (this.openTicks > 20) {
         this.closeDropper(dropperBlock);
      }

   }

   public void openDropper(CubeDropperBlock dropperBlock) {
      if (this.field_145850_b instanceof ServerWorld) {
         dropperBlock.setOpen(true, this.func_195044_w(), this.field_145850_b, this.func_174877_v());
         this.openTicks = 1;
         if (this.entityUUIDs.size() == 2) {
            this.fizzleFirstCube();
         }
      }

   }

   public void fizzleFirstCube() {
      if (this.field_145850_b instanceof ServerWorld) {
         Entity entity = ((ServerWorld)this.field_145850_b).func_217461_a((UUID)this.entityUUIDs.get(0));
         if (entity instanceof TestElementEntity) {
            ((TestElementEntity)entity).startFizzling();
         } else if (entity != null && entity.func_70089_S()) {
            entity.func_70106_y();
         }

         this.entityUUIDs.remove(0);
      }
   }

   public void closeDropper(CubeDropperBlock dropperBlock) {
      if (this.field_145850_b instanceof ServerWorld) {
         dropperBlock.setOpen(false, this.func_195044_w(), this.field_145850_b, this.func_174877_v());
         this.openTicks = 0;
         this.addEntity();
      }

   }

   public void resetDropper() {
      if (this.entityUUIDs.size() == 2) {
         this.fizzleFirstCube();
      }

   }

   public void updateEntities() {
      if (this.entityUUIDs.isEmpty() && this.openTicks == 0) {
         this.addEntity();
      }

      if (this.field_145850_b instanceof ServerWorld) {
         this.entityUUIDs.removeIf((uuid) -> ((ServerWorld)this.field_145850_b).func_217461_a(uuid) == null);
      }

   }

   public void addEntity() {
      if (this.entityNBT != null && this.field_145850_b instanceof ServerWorld) {
         Entity entity = EntityType.func_220335_a(this.entityNBT, this.field_145850_b, (e) -> {
            e.func_233576_c_((new Vec3(this.func_174877_v().func_177968_d().func_177974_f())).to3d());
            e.field_70177_z = ModUtil.symmetricRandom(15.0F) + e.func_184229_a(Rotation.func_222466_a(new Random()));
            e.func_181013_g(e.field_70177_z);
            e.func_70034_d(e.field_70177_z);
            return e;
         });
         if (entity != null) {
            if (entity instanceof TestElementEntity) {
               ((TestElementEntity)entity).setFromDropper(true);
            }

            boolean successful = ((ServerWorld)this.field_145850_b).func_242106_g(entity);
            if (successful) {
               this.entityUUIDs.add(entity.func_110124_au());
            }

         }
      }
   }

   public void removeAllEntities() {
      if (this.field_145850_b instanceof ServerWorld) {
         for(UUID uuid : this.entityUUIDs) {
            Entity entity = ((ServerWorld)this.field_145850_b).func_217461_a(uuid);
            if (entity != null) {
               if (entity instanceof TestElementEntity) {
                  ((TestElementEntity)entity).startFizzling();
               } else if (entity.func_70089_S()) {
                  entity.func_70106_y();
               }
            }
         }

         this.entityUUIDs.clear();
      }

   }

   public void onEggClick(ItemStack egg, PlayerEntity player) {
      EntityType<?> spawnEggType = ((SpawnEggItem)egg.func_77973_b()).func_208076_b(egg.func_77978_p());
      if (!player.func_184812_l_()) {
         egg.func_190918_g(1);
         this.getEntityType().ifPresent((type) -> player.func_191521_c(new ItemStack(ForgeSpawnEggItem.fromEntityType(type))));
      }

      this.removeAllEntities();
      this.setEntityNBT(spawnEggType);
   }

   public void onWrenchClick(PlayerEntity player) {
      if (this.hasEntityNBT()) {
         if (this.entityUUIDs.size() > 1) {
            this.resetDropper();
         } else {
            this.removeAllEntities();
            if (!player.func_184812_l_()) {
               this.dropEgg();
            }

            this.removeEntityNBT();
         }

      }
   }

   public void onRemove() {
      this.removeAllEntities();
      this.dropEgg();
   }

   public void dropEgg() {
      if (this.field_145850_b != null) {
         this.getEntityType().ifPresent((type) -> {
            BlockPos blockPos = this.func_174877_v();
            ItemEntity entity = new ItemEntity(this.field_145850_b, (double)(blockPos.func_177958_n() + 1), (double)blockPos.func_177956_o() - 1.4, (double)(blockPos.func_177952_p() + 1), new ItemStack(ForgeSpawnEggItem.fromEntityType(type)));
            entity.func_213317_d(entity.func_213322_ci().func_216372_d((double)1.0F, (double)0.5F, (double)1.0F));
            this.field_145850_b.func_217376_c(entity);
         });
      }
   }

   public List<BlockPos> getIndicatorPositions(BlockState blockState, World world, BlockPos pos) {
      List<BlockPos> possibleIndicatorPositions = new ArrayList();
      possibleIndicatorPositions.addAll(getSurroundingPositions(pos));
      possibleIndicatorPositions.addAll(getSurroundingPositions(pos.func_177977_b()));
      return possibleIndicatorPositions;
   }

   public static List<BlockPos> getSurroundingPositions(BlockPos pos) {
      return new ArrayList(Arrays.asList(pos.func_177970_e(2), pos.func_177970_e(2).func_177976_e(), pos.func_177968_d().func_177976_e(), pos.func_177976_e(), pos.func_177978_c().func_177976_e(), pos.func_177978_c(), pos.func_177978_c().func_177974_f(), pos.func_177978_c().func_177965_g(2), pos.func_177965_g(2), pos.func_177968_d().func_177965_g(2), pos.func_177970_e(2).func_177965_g(2), pos.func_177970_e(2).func_177974_f()));
   }

   public void setEntityNBT(EntityType<?> entityType) {
      this.entityNBT = new CompoundNBT();
      this.entityNBT.func_74778_a("id", Registry.field_212629_r.func_177774_c(entityType).toString());
   }

   public boolean hasEntityNBT() {
      return this.entityNBT != null;
   }

   public void removeEntityNBT() {
      this.entityNBT = null;
   }

   public Optional<EntityType<?>> getEntityType() {
      return this.entityNBT == null ? Optional.empty() : EntityType.func_220347_a(this.entityNBT);
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      nbt = super.func_189515_b(nbt);
      if (!this.entityUUIDs.isEmpty()) {
         nbt.func_186854_a("UUID1", (UUID)this.entityUUIDs.get(0));
      }

      if (this.entityUUIDs.size() >= 2) {
         nbt.func_186854_a("UUID2", (UUID)this.entityUUIDs.get(1));
      }

      nbt.func_74757_a("Powered", this.wasActive);
      nbt.func_74768_a("OpenTicks", this.openTicks);
      if (this.entityNBT != null) {
         nbt.func_218657_a("Entity", this.entityNBT);
      }

      return nbt;
   }

   public void func_230337_a_(BlockState blockState, CompoundNBT nbt) {
      super.func_230337_a_(blockState, nbt);
      this.entityUUIDs.clear();
      if (nbt.func_186855_b("UUID1")) {
         this.entityUUIDs.add(nbt.func_186857_a("UUID1"));
      }

      if (nbt.func_186855_b("UUID2")) {
         this.entityUUIDs.add(nbt.func_186857_a("UUID2"));
      }

      this.wasActive = nbt.func_74767_n("Powered");
      this.openTicks = nbt.func_74762_e("OpenTicks");
      this.entityNBT = null;
      if (nbt.func_74764_b("Entity")) {
         this.entityNBT = nbt.func_74775_l("Entity");
      }

   }
}
