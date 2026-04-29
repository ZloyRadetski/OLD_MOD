package net.portalmod.core.init;

import java.util.function.Supplier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public enum ArmorMaterialInit implements IArmorMaterial {
   LONGFALL_BOOTS("portalmod:longfall_boots", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.field_187725_r, 3.0F, 0.1F, () -> Ingredient.func_199804_a(new IItemProvider[]{Items.field_234760_kn_}));

   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durabilityMultiplier;
   private final int[] slotProtections;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final float knockbackResistance;
   private final LazyValue<Ingredient> repairIngredient;

   private ArmorMaterialInit(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
      this.name = name;
      this.durabilityMultiplier = durabilityMultiplier;
      this.slotProtections = slotProtections;
      this.enchantmentValue = enchantmentValue;
      this.sound = sound;
      this.toughness = toughness;
      this.knockbackResistance = knockbackResistance;
      this.repairIngredient = new LazyValue(repairIngredient);
   }

   public int func_200896_a(EquipmentSlotType p_200896_1_) {
      return HEALTH_PER_SLOT[p_200896_1_.func_188454_b()] * this.durabilityMultiplier;
   }

   public int func_200902_b(EquipmentSlotType p_200902_1_) {
      return this.slotProtections[p_200902_1_.func_188454_b()];
   }

   public int func_200900_a() {
      return this.enchantmentValue;
   }

   public SoundEvent func_200899_b() {
      return this.sound;
   }

   public Ingredient func_200898_c() {
      return (Ingredient)this.repairIngredient.func_179281_c();
   }

   public String func_200897_d() {
      return this.name;
   }

   public float func_200901_e() {
      return this.toughness;
   }

   public float func_230304_f_() {
      return this.knockbackResistance;
   }
}
