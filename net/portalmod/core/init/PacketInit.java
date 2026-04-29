package net.portalmod.core.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.portalmod.common.entities.CTestElementHoldingPacket;
import net.portalmod.common.sorted.antline.SAntlineUpdatePacket;
import net.portalmod.common.sorted.faithplate.CFaithPlateEndConfigPacket;
import net.portalmod.common.sorted.faithplate.CFaithPlateLaunchPacket;
import net.portalmod.common.sorted.faithplate.CFaithPlateUpdatedPacket;
import net.portalmod.common.sorted.faithplate.SFaithPlateLaunchPacket;
import net.portalmod.common.sorted.faithplate.SFaithPlateStartConfigPacket;
import net.portalmod.common.sorted.gel.CPropulsionGelBoostTickPacket;
import net.portalmod.common.sorted.gel.CRepulsionGelBouncePacket;
import net.portalmod.common.sorted.portal.CThroughPortalProofPacket;
import net.portalmod.common.sorted.portal.SForgetPortalPacket;
import net.portalmod.common.sorted.portal.SPortalPairPacket;
import net.portalmod.common.sorted.portal.SPortalShotPacket;
import net.portalmod.common.sorted.portalgun.CPortalGunInteractionPacket;
import net.portalmod.common.sorted.portalgun.SPortalGunAnimationPacket;
import net.portalmod.common.sorted.portalgun.SPortalGunFailShotPacket;
import net.portalmod.common.sorted.portalgun.skins.CSetPlayerSkinPacket;
import net.portalmod.common.sorted.portalgun.skins.SSetPlayerSkinPacket;
import net.portalmod.common.sorted.radio.SRadioUpdatePacket;
import net.portalmod.common.sorted.trigger.CTriggerAbortConfigPacket;
import net.portalmod.common.sorted.trigger.CTriggerEndConfigPacket;
import net.portalmod.common.sorted.trigger.STriggerStartConfigPacket;
import net.portalmod.core.packet.AbstractPacket;
import net.portalmod.core.packet.CPlayerPortalTeleportLerpPacket;
import net.portalmod.core.packet.CPlayerPortalTeleportPacket;
import net.portalmod.core.packet.SEntityPortalTeleportLerpPacket;
import net.portalmod.core.packet.SSpawnChamberSignPacket;
import net.portalmod.core.packet.SUpdateBooleanGameRulePacket;

public class PacketInit {
   private static int id = 0;
   private static final String PROTOCOL_VERSION = "1";
   public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("portalmod", "main"), () -> "1", "1"::equals, "1"::equals);

   private PacketInit() {
   }

   public static void init() {
      register(new SRadioUpdatePacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SAntlineUpdatePacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SFaithPlateLaunchPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SEntityPortalTeleportLerpPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SPortalGunAnimationPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SPortalPairPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SForgetPortalPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SPortalShotPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SSetPlayerSkinPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SPortalGunFailShotPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SSpawnChamberSignPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SFaithPlateStartConfigPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new STriggerStartConfigPacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new SUpdateBooleanGameRulePacket(), NetworkDirection.PLAY_TO_CLIENT);
      register(new CFaithPlateUpdatedPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CFaithPlateLaunchPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CPortalGunInteractionPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CPlayerPortalTeleportPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CSetPlayerSkinPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CTestElementHoldingPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CFaithPlateEndConfigPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CTriggerEndConfigPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CTriggerAbortConfigPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CThroughPortalProofPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CPlayerPortalTeleportLerpPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CPropulsionGelBoostTickPacket(), NetworkDirection.PLAY_TO_SERVER);
      register(new CRepulsionGelBouncePacket(), NetworkDirection.PLAY_TO_SERVER);
   }

   private static <T extends AbstractPacket<T>> void register(T inst, NetworkDirection direction) {
      SimpleChannel.MessageBuilder var10000 = INSTANCE.messageBuilder(inst.getClass(), id++, direction).encoder(AbstractPacket::encode);
      inst.getClass();
      var10000.decoder(inst::decode).consumer(AbstractPacket::handle).add();
   }
}
