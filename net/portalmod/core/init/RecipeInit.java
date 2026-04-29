package net.portalmod.core.init;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.portalmod.common.sorted.portalgun.PortalGunRecipe;

public class RecipeInit {
   public static final DeferredRegister<IRecipeSerializer<?>> RECIPES;
   public static final RegistryObject<SpecialRecipeSerializer<PortalGunRecipe>> PORTAL_GUN;

   static {
      RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "portalmod");
      PORTAL_GUN = RECIPES.register("portalgun_modifying", () -> new SpecialRecipeSerializer(PortalGunRecipe::new));
   }
}
