package net.portalmod.common.sorted.portalgun;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.portalmod.core.init.RecipeInit;

public class PortalGunRecipe extends SpecialRecipe {
   public static final Ingredient CHAIN_INGREDIENT;

   public PortalGunRecipe(ResourceLocation p_i48169_1_) {
      super(p_i48169_1_);
   }

   public boolean matches(CraftingInventory inventory, World world) {
      ItemStack gunItem = inventory.func_70301_a(4);
      if (!gunItem.func_190926_b() && gunItem.func_77973_b() instanceof PortalGun) {
         boolean hasModifier = false;
         boolean hasChain = false;
         int i = 0;

         while(true) {
            if (i >= inventory.func_70302_i_()) {
               return hasModifier;
            }

            ItemStack item = inventory.func_70301_a(i);
            if (!item.func_190926_b() && i != 4) {
               hasModifier = true;
               if (i != 1 && i != 3 && i != 5) {
                  break;
               }

               if (!(item.func_77973_b() instanceof DyeItem)) {
                  if (!CHAIN_INGREDIENT.test(item) || hasChain) {
                     break;
                  }

                  hasChain = true;
               }
            }

            ++i;
         }

         return false;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInventory inventory) {
      ItemStack newGun = inventory.func_70301_a(4).func_77946_l();
      CompoundNBT newNBT = newGun.func_196082_o();
      ItemStack accentDye = inventory.func_70301_a(1);
      ItemStack leftDye = inventory.func_70301_a(3);
      ItemStack rightDye = inventory.func_70301_a(5);
      if (!accentDye.func_190926_b()) {
         newNBT.func_74778_a("AccentColor", ((DyeItem)accentDye.func_77973_b()).func_195962_g().func_176762_d());
      }

      if (!leftDye.func_190926_b() && leftDye.func_77973_b() instanceof DyeItem) {
         newNBT.func_74778_a("LeftColor", ((DyeItem)leftDye.func_77973_b()).func_195962_g().func_176762_d());
      }

      if (!rightDye.func_190926_b() && rightDye.func_77973_b() instanceof DyeItem) {
         newNBT.func_74778_a("RightColor", ((DyeItem)rightDye.func_77973_b()).func_195962_g().func_176762_d());
      }

      String lock = "None";
      if (!leftDye.func_190926_b() && CHAIN_INGREDIENT.test(leftDye)) {
         lock = "Left";
      }

      if (!rightDye.func_190926_b() && CHAIN_INGREDIENT.test(rightDye)) {
         lock = "Right";
      }

      newNBT.func_74778_a("Locked", lock);
      newNBT.func_74768_a("LastPortal", 0);
      return newGun;
   }

   public boolean func_194133_a(int width, int height) {
      return width == 3 && height == 3;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return (IRecipeSerializer)RecipeInit.PORTAL_GUN.get();
   }

   static {
      CHAIN_INGREDIENT = Ingredient.func_199804_a(new IItemProvider[]{Items.field_234729_dO_});
   }
}
