package net.portalmod.common.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.portalmod.core.util.ModUtil;

public class ModSpawnEggItem extends SpawnEggItem {
   protected static final List<ModSpawnEggItem> UNADDED = new ArrayList();
   private final Lazy<? extends EntityType<?>> supplier;
   public final String tooltipName;

   public ModSpawnEggItem(RegistryObject<? extends EntityType<?>> supplier, Item.Properties properties, String tooltipName) {
      super((EntityType)null, 16777215, 16777215, properties);
      supplier.getClass();
      this.supplier = Lazy.of(supplier::get);
      UNADDED.add(this);
      this.tooltipName = tooltipName;
   }

   public static void register() {
      Map<EntityType<?>, SpawnEggItem> eggs = (Map)ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, (Object)null, "field_195987_b");
      ModDefaultDispenseItemBehavior behaviour = new ModDefaultDispenseItemBehavior();

      for(SpawnEggItem spawnEgg : UNADDED) {
         eggs.put(spawnEgg.func_208076_b((CompoundNBT)null), spawnEgg);
         DispenserBlock.func_199774_a(spawnEgg, behaviour);
      }

      UNADDED.clear();
   }

   public EntityType<?> func_208076_b(CompoundNBT p_208076_1_) {
      return (EntityType)this.supplier.get();
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
      if (this.tooltipName != null) {
         ModUtil.addTooltip(this.tooltipName, list);
      }

   }

   private static class ModDefaultDispenseItemBehavior implements IDispenseItemBehavior {
      private ModDefaultDispenseItemBehavior() {
      }

      public ItemStack dispense(IBlockSource source, ItemStack stack) {
         Direction direction = (Direction)source.func_189992_e().func_177229_b(DispenserBlock.field_176441_a);
         EntityType<?> type = ((SpawnEggItem)stack.func_77973_b()).func_208076_b(stack.func_77978_p());
         type.func_220331_a(source.func_197524_h(), stack, (PlayerEntity)null, source.func_180699_d().func_177972_a(direction), SpawnReason.DISPENSER, direction != Direction.DOWN, false);
         stack.func_190918_g(1);
         return stack;
      }

      protected ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         Direction direction = (Direction)p_82487_1_.func_189992_e().func_177229_b(DispenserBlock.field_176441_a);
         IPosition iposition = DispenserBlock.func_149939_a(p_82487_1_);
         ItemStack itemstack = p_82487_2_.func_77979_a(1);
         spawnItem(p_82487_1_.func_197524_h(), itemstack, 6, direction, iposition);
         return p_82487_2_;
      }

      public static void spawnItem(World p_82486_0_, ItemStack p_82486_1_, int p_82486_2_, Direction p_82486_3_, IPosition p_82486_4_) {
         double d0 = p_82486_4_.func_82615_a();
         double d1 = p_82486_4_.func_82617_b();
         double d2 = p_82486_4_.func_82616_c();
         if (p_82486_3_.func_176740_k() == Axis.Y) {
            d1 -= (double)0.125F;
         } else {
            d1 -= (double)0.15625F;
         }

         ItemEntity itementity = new ItemEntity(p_82486_0_, d0, d1, d2, p_82486_1_);
         double d3 = p_82486_0_.field_73012_v.nextDouble() * 0.1 + 0.2;
         itementity.func_213293_j(p_82486_0_.field_73012_v.nextGaussian() * (double)0.0075F * (double)p_82486_2_ + (double)p_82486_3_.func_82601_c() * d3, p_82486_0_.field_73012_v.nextGaussian() * (double)0.0075F * (double)p_82486_2_ + (double)0.2F, p_82486_0_.field_73012_v.nextGaussian() * (double)0.0075F * (double)p_82486_2_ + (double)p_82486_3_.func_82599_e() * d3);
         p_82486_0_.func_217376_c(itementity);
      }

      protected void playSound(IBlockSource p_82485_1_) {
         p_82485_1_.func_197524_h().func_217379_c(1000, p_82485_1_.func_180699_d(), 0);
      }

      protected void playAnimation(IBlockSource p_82489_1_, Direction p_82489_2_) {
         p_82489_1_.func_197524_h().func_217379_c(2000, p_82489_1_.func_180699_d(), p_82489_2_.func_176745_a());
      }
   }
}
