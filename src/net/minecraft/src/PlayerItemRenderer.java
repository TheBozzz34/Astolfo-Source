package net.minecraft.src;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class PlayerItemRenderer {
   private int attachTo = 0;
   private float q = 0.0F;
   private ModelRenderer modelRenderer = null;
   private static final long a = 75541727112086L;

   public PlayerItemRenderer(int var1, float var2, ModelRenderer var3) {
      this.attachTo = var1;
      this.q = var2;
      this.modelRenderer = var3;
   }

   public ModelRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public void render(ModelBiped var1, float var2) {
      ModelRenderer var6 = PlayerItemModel.getAttachModel(var1, this.attachTo);
      if (var6 != null) {
         var6.postRender(var2);
      }

      this.modelRenderer.render(var2 * this.q);
   }
}
