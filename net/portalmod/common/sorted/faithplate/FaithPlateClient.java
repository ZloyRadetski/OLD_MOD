package net.portalmod.common.sorted.faithplate;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.util.ModUtil;

public class FaithPlateClient {
   protected static void handleLaunch(SFaithPlateLaunchPacket packet, Supplier<NetworkEvent.Context> context) {
      World level = Minecraft.func_71410_x().field_71441_e;
      FaithPlateTileEntity be = (FaithPlateTileEntity)level.func_175625_s(packet.pos);
      if (be != null) {
         FaithPlateTER ter = (FaithPlateTER)TileEntityRendererDispatcher.field_147556_a.func_147547_b(be);
         ((FaithPlatePlateModel)ter.getPlateModel()).startAnimation(be, "launch");
         level.func_184148_a(Minecraft.func_71410_x().field_71439_g, (double)packet.pos.func_177958_n() + (double)0.5F, (double)packet.pos.func_177956_o() + (double)0.5F, (double)packet.pos.func_177952_p() + (double)0.5F, (SoundEvent)SoundInit.FAITHPLATE_LAUNCH.get(), SoundCategory.BLOCKS, 1.0F, ModUtil.randomSoundPitch());
      }
   }

   protected static void setScreen(BlockPos pos) {
      Minecraft.func_71410_x().func_147108_a(new FaithPlateConfigScreen(pos));
   }
}
