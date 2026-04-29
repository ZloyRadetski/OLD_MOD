package net.portalmod.mixins.accessors;

import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractMinecartEntity.class})
public interface AbstractMinecartAccessor {
   @Accessor("lSteps")
   void pmSetLSteps(int var1);

   @Accessor("lx")
   void pmSetLX(double var1);

   @Accessor("ly")
   void pmSetLY(double var1);

   @Accessor("lz")
   void pmSetLZ(double var1);
}
