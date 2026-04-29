package net.portalmod.mixins.accessors;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LivingEntity.class})
public interface LivingEntityAccessor {
   @Accessor("lerpSteps")
   void pmSetLerpSteps(int var1);

   @Accessor("lerpX")
   void pmSetLerpX(double var1);

   @Accessor("lerpY")
   void pmSetLerpY(double var1);

   @Accessor("lerpZ")
   void pmSetLerpZ(double var1);
}
