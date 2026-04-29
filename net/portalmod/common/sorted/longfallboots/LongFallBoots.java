package net.portalmod.common.sorted.longfallboots;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.portalmod.core.util.ModUtil;

public class LongFallBoots extends ArmorItem {
   private static final ResourceLocation TEXTURE = new ResourceLocation("portalmod", "textures/models/armor/longfall_boots.png");

   public LongFallBoots(IArmorMaterial armorMaterial, EquipmentSlotType slotType, Item.Properties properties) {
      super(armorMaterial, slotType, properties);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public <A extends BipedModel<?>> A getArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlotType armorSlot, A originalModel) {
      return (A)(new LongFallBootsModel(entity));
   }

   @Nullable
   public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
      return TEXTURE.toString();
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
      ModUtil.addTooltip("longfall_boots", list);
   }
}
