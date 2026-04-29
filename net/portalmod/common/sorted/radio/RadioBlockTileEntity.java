package net.portalmod.common.sorted.radio;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.TileEntityTypeInit;

public class RadioBlockTileEntity extends TileEntity implements ITickableTileEntity {
   private int countedTicks = 0;
   private boolean initialized = false;

   public RadioBlockTileEntity() {
      super((TileEntityType)TileEntityTypeInit.RADIO.get());
   }

   public void setInitialized() {
      this.initialized = true;
   }

   public void func_73660_a() {
      if (!this.field_145850_b.func_201670_d()) {
         if (!this.initialized) {
            if (!(Boolean)this.func_195044_w().func_177229_b(RadioBlock.POWERED)) {
               this.setState(RadioState.OFF);
            }

            this.initialized = true;
         }

         if (this.getState() == RadioState.ACTIVE) {
            if (this.countedTicks++ == 900) {
               this.setState(RadioState.INACTIVE);
               this.countedTicks = 0;
            }
         } else {
            this.countedTicks = 0;
         }

      }
   }

   public void switchManual() {
      if (!this.field_145850_b.func_201670_d() && !(Boolean)this.func_195044_w().func_177229_b(RadioBlock.POWERED) && this.getState() != RadioState.ACTIVE) {
         if (this.isPlaying()) {
            this.stop();
         } else {
            this.play();
         }

      }
   }

   public void handlePacket(RadioState state) {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RadioBlockClient.handlePacket(this, state));
   }

   public void play() {
      if (!this.field_145850_b.func_201670_d() && !this.isPlaying()) {
         if (this.field_145850_b.func_234923_W_() != null && this.func_145831_w().func_234923_W_() == World.field_234920_i_) {
            this.setState(RadioState.ACTIVE);
         } else {
            this.setState(RadioState.ON);
         }

         this.sendUpdatePacket();
      }
   }

   public void stop() {
      if (!this.field_145850_b.func_201670_d() && this.isPlaying()) {
         if (this.field_145850_b.func_234923_W_() != null && this.func_145831_w().func_234923_W_() == World.field_234920_i_) {
            this.setState(RadioState.INACTIVE);
         } else {
            this.setState(RadioState.OFF);
         }

         this.sendUpdatePacket();
      }
   }

   public void sendUpdatePacket() {
      PacketInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.field_145850_b.func_175726_f(this.func_174877_v())), new SRadioUpdatePacket(this.func_174877_v(), this.getState()));
   }

   private RadioState getState() {
      return (RadioState)this.func_195044_w().func_177229_b(RadioBlock.STATE);
   }

   private void setState(RadioState state) {
      this.field_145850_b.func_180501_a(this.func_174877_v(), (BlockState)this.func_195044_w().func_206870_a(RadioBlock.STATE, state), 2);
   }

   public boolean isPlaying() {
      return this.getState().isPlaying();
   }

   public void func_145843_s() {
      if (this.field_145850_b.func_201670_d()) {
         this.handlePacket(RadioState.OFF);
      }

      super.func_145843_s();
   }
}
