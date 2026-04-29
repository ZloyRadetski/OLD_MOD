package net.portalmod.common.sorted.trigger;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.portalmod.core.init.TileEntityTypeInit;

public class TriggerTileEntity extends TileEntity implements ITickableTileEntity {
   private BlockPos fieldStart;
   private BlockPos fieldEnd;
   private TriggerType type;
   private int entityCount;
   private PlayerEntity configuringPlayer;

   public TriggerTileEntity(TileEntityType<?> type) {
      super(type);
      this.type = TriggerType.PLAYER;
      this.entityCount = 0;
   }

   public TriggerTileEntity() {
      this((TileEntityType)TileEntityTypeInit.TRIGGER.get());
   }

   public void func_73660_a() {
      if (this.field_145850_b != null) {
         BlockState state = this.field_145850_b.func_180495_p(this.field_174879_c);
         if (!this.hasField()) {
            if (state.func_177229_b(TriggerBlock.STATE) != TriggerState.NULL) {
               this.field_145850_b.func_175656_a(this.field_174879_c, (BlockState)state.func_206870_a(TriggerBlock.STATE, TriggerState.NULL));
            }

            this.entityCount = 0;
         } else {
            AxisAlignedBB aabb = this.getField();
            aabb = aabb.func_186670_a(this.field_174879_c);
            List<LivingEntity> entities = this.field_145850_b.func_175647_a(LivingEntity.class, aabb, ((TriggerType)state.func_177229_b(TriggerBlock.TYPE)).getPredicate().and((entity) -> !entity.func_175149_v()));
            if (this.entityCount != entities.size()) {
               this.entityCount = entities.size();
               this.field_145850_b.func_195593_d(this.field_174879_c, state.func_177230_c());
            }

            boolean shouldActivate = !this.isBeingConfigured() && !entities.isEmpty();
            if (!((TriggerState)state.func_177229_b(TriggerBlock.STATE)).isActive(shouldActivate)) {
               this.field_145850_b.func_175656_a(this.field_174879_c, (BlockState)state.func_206870_a(TriggerBlock.STATE, TriggerState.fromActive(shouldActivate)));
            }

         }
      }
   }

   public void startConfiguration(ServerPlayerEntity player) {
      this.configuringPlayer = player;
   }

   public void endConfiguration() {
      this.configuringPlayer = null;
   }

   public boolean isBeingConfigured() {
      return this.configuringPlayer != null;
   }

   public PlayerEntity getConfiguringPlayer() {
      return this.configuringPlayer;
   }

   public void setField(BlockPos start, BlockPos end) {
      this.fieldStart = start;
      this.fieldEnd = end;
   }

   public boolean hasField() {
      return this.fieldStart != null && this.fieldEnd != null;
   }

   public AxisAlignedBB getField() {
      return !this.hasField() ? null : (new AxisAlignedBB(this.fieldStart, this.fieldEnd)).func_72321_a((double)1.0F, (double)1.0F, (double)1.0F);
   }

   public TriggerType getTriggerType() {
      return this.type;
   }

   public void updateTriggerType() {
      if (this.field_145850_b != null) {
         this.type = (TriggerType)this.field_145850_b.func_180495_p(this.func_174877_v()).func_177229_b(TriggerBlock.TYPE);
      }
   }

   public CompoundNBT func_189515_b(CompoundNBT nbt) {
      if (this.fieldStart != null && this.fieldEnd != null) {
         CompoundNBT start = new CompoundNBT();
         start.func_74768_a("x", this.fieldStart.func_177958_n());
         start.func_74768_a("y", this.fieldStart.func_177956_o());
         start.func_74768_a("z", this.fieldStart.func_177952_p());
         nbt.func_218657_a("start", start);
         CompoundNBT end = new CompoundNBT();
         end.func_74768_a("x", this.fieldEnd.func_177958_n());
         end.func_74768_a("y", this.fieldEnd.func_177956_o());
         end.func_74768_a("z", this.fieldEnd.func_177952_p());
         nbt.func_218657_a("end", end);
      }

      return super.func_189515_b(nbt);
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      this.load(nbt);
      this.updateTriggerType();
   }

   public void load(CompoundNBT nbt) {
      if (nbt.func_74764_b("start") && nbt.func_74764_b("end")) {
         CompoundNBT start = nbt.func_74775_l("start");
         CompoundNBT end = nbt.func_74775_l("end");
         if (!start.func_74764_b("x") || !start.func_74764_b("y") || !start.func_74764_b("z")) {
            return;
         }

         if (!end.func_74764_b("x") || !end.func_74764_b("y") || !end.func_74764_b("z")) {
            return;
         }

         this.fieldStart = new BlockPos(start.func_74762_e("x"), start.func_74762_e("y"), start.func_74762_e("z"));
         this.fieldEnd = new BlockPos(end.func_74762_e("x"), end.func_74762_e("y"), end.func_74762_e("z"));
      }

   }

   public void func_189667_a(Rotation rotation) {
      this.fieldStart = this.fieldStart.func_190942_a(rotation);
      this.fieldEnd = this.fieldEnd.func_190942_a(rotation);
   }

   public void func_189668_a(Mirror mirror) {
      int flipX = mirror == Mirror.FRONT_BACK ? -1 : 1;
      int flipZ = mirror == Mirror.LEFT_RIGHT ? -1 : 1;
      this.fieldStart = new BlockPos(this.fieldStart.func_177958_n() * flipX, this.fieldStart.func_177956_o(), this.fieldStart.func_177952_p() * flipZ);
      this.fieldEnd = new BlockPos(this.fieldEnd.func_177958_n() * flipX, this.fieldEnd.func_177956_o(), this.fieldEnd.func_177952_p() * fl