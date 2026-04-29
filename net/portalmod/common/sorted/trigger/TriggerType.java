package net.portalmod.common.sorted.trigger;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IStringSerializable;

public enum TriggerType implements IStringSerializable {
   PLAYER("player", (entity) -> entity instanceof PlayerEntity),
   MOB("mob", (entity) -> !(entity instanceof PlayerEntity));

   private final String name;
   private final Predicate<LivingEntity> predicate;

   private TriggerType(String name, Predicate<LivingEntity> predicate) {
      this.name = name;
      this.predicate = predicate;
   }

   public String func_176610_l() {
      return this.name;
   }

   public Predicate<LivingEntity> getPredicate() {
      return this.predicate;
   }
}
