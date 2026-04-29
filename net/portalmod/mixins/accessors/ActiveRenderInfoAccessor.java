package net.portalmod.mixins.accessors;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ActiveRenderInfo.class})
public interface ActiveRenderInfoAccessor {
   @Accessor("position")
   void pmSetPosition(Vector3d var1);

   @Accessor("position")
   Vector3d pmGetPosition();

   @Accessor("blockPosition")
   BlockPos.Mutable pmGetBlockPosition();

   @Accessor("eyeHeight")
   float pmGetEyeHeight();

   @Accessor("eyeHeightOld")
   float pmGetEyeHeightOld();

   @Invoker("setRotation")
   void pmSetRotation(float var1, float var2);
}
