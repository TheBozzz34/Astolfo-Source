package net.minecraft.src;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class PlayerItemRenderer {
   private int attachTo = 0;
   private float scaleFactor = 0.0F;
   private ModelRenderer modelRenderer = null;

   public PlayerItemRenderer(int i, float f, ModelRenderer modelRenderer) {
      this.attachTo = i;
      this.scaleFactor = f;
      this.modelRenderer = modelRenderer;
   }

   public ModelRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public void render(ModelBiped modelBiped, float scale) {
      ModelRenderer modelRenderer2 = PlayerItemModel.getAttachModel(modelBiped, this.attachTo);
      if (modelRenderer2 != null) {
         modelRenderer2.postRender(scale);
      }

      this.modelRenderer.render(scale * this.scaleFactor);
   }
}
