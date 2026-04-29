package net.portalmod.common.sorted.creer;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CreerModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer head;
   private final ModelRenderer hair;
   private final ModelRenderer leg0;
   private final ModelRenderer leg1;
   private final ModelRenderer leg2;
   private final ModelRenderer leg3;

   public CreerModel() {
      this(0.0F);
   }

   public CreerModel(float p_i46366_1_) {
      this.head = new ModelRenderer(this, 0, 0);
      this.head.func_228301_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i46366_1_);
      this.head.func_78793_a(0.0F, 18.0F, 0.0F);
      this.hair = new ModelRenderer(this, 32, 0);
      this.hair.func_228301_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i46366_1_ + 0.5F);
      this.hair.func_78793_a(0.0F, 18.0F, 0.0F);
      this.leg0 = new ModelRenderer(this, 0, 16);
      this.leg0.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, p_i46366_1_);
      this.leg0.func_78793_a(-2.0F, 18.0F, 4.0F);
      this.leg1 = new ModelRenderer(this, 0, 16);
      this.leg1.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, p_i46366_1_);
      this.leg1.func_78793_a(2.0F, 18.0F, 4.0F);
      this.leg2 = new ModelRenderer(this, 0, 16);
      this.leg2.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, p_i46366_1_);
      this.leg2.func_78793_a(-2.0F, 18.0F, -4.0F);
      this.leg3 = new ModelRenderer(this, 0, 16);
      this.leg3.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, p_i46366_1_);
      this.leg3.func_78793_a(2.0F, 18.0F, -4.0F);
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.head, this.leg0, this.leg1, this.leg2, this.leg3);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.field_78796_g = p_225597_5_ * ((float)Math.PI / 180F);
      this.head.field_78795_f = p_225597_6_ * ((float)Math.PI / 180F);
      this.leg0.field_78795_f = MathHelper.func_76134_b(p_225597_2_ * 0.6662F) * 1.4F * p_225597_3_;
      this.leg1.field_78795_f = MathHelper.func_76134_b(p_225597_2_ * 0.6662F + (float)Math.PI) * 1.4F * p_225597_3_;
      this.leg2.field_78795_f = MathHelper.func_76134_b(p_225597_2_ * 0.6662F + (float)Math.PI) * 1.4F * p_225597_3_;
      this.leg3.field_78795_f = MathHelper.func_76134_b(p_225597_2_ * 0.6662F) * 1.4F * p_225597_3_;
   }
}
