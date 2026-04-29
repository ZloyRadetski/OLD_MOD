package net.portalmod.mixins.accessors;

import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GameRules.BooleanValue.class})
public interface BooleanValueAccessor {
   @Invoker("create")
   static GameRules.RuleType<GameRules.BooleanValue> pmCreate(boolean value, BiConsumer<MinecraftServer, GameRules.BooleanValue> consumer) {
      throw new AssertionError();
   }
}
