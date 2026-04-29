package net.portalmod.core.init;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.Category;
import net.minecraftforge.fml.network.PacketDistributor;
import net.portalmod.core.packet.SUpdateBooleanGameRulePacket;
import net.portalmod.mixins.accessors.BooleanValueAccessor;

public class GameRuleInit {
   public static final Map<String, GameRules.RuleKey<?>> REGISTRY = new HashMap();
   public static GameRules.RuleKey<GameRules.BooleanValue> PORTAL_SLOWSHOT;
   public static GameRules.RuleKey<GameRules.BooleanValue> USE_PORTALABLE_BLACKLIST;
   public static GameRules.RuleKey<GameRules.BooleanValue> ALLOW_PORTAL_OVERWRITE;

   private GameRuleInit() {
   }

   public static void registerAll() {
      PORTAL_SLOWSHOT = registerBoolean("portalSlowShot", Category.PLAYER, false);
      USE_PORTALABLE_BLACKLIST = registerBoolean("usePortalableBlacklist", Category.PLAYER, false);
      ALLOW_PORTAL_OVERWRITE = registerServerBoolean("allowPortalOverwrite", Category.PLAYER, true);
   }

   private static <T extends GameRules.RuleValue<T>> GameRules.RuleKey<T> register(String name, GameRules.Category category, GameRules.RuleType<T> rule) {
      GameRules.RuleKey<T> key = GameRules.func_234903_a_(name, category, rule);
      REGISTRY.put(name, key);
      return key;
   }

   private static GameRules.RuleKey<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean defaultValue) {
      return register(name, category, BooleanValueAccessor.pmCreate(defaultValue, (server, value) -> PacketInit.INSTANCE.send(PacketDistributor.ALL.noArg(), new SUpdateBooleanGameRulePacket(name, value.func_223572_a()))));
   }

   private static GameRules.RuleKey<GameRules.BooleanValue> registerServerBoolean(String name, GameRules.Category category, boolean defaultValue) {
      return register(name, category, BooleanValueAccessor.pmCreate(defaultValue, (server, value) -> {
      }));
   }

   public static <T extends GameRules.RuleValue<T>> GameRules.RuleKey<T> getRule(String name) {
      return (GameRules.RuleKey)REGISTRY.get(name);
   }

   public static void sendBooleanRule(ServerPlayerEntity player, GameRules.RuleKey<GameRules.BooleanValue> key) {
      PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new SUpdateBooleanGameRulePacket(key.func_223576_a(), player.field_70170_p.func_82736_K().func_223586_b(key)));
   }
}
