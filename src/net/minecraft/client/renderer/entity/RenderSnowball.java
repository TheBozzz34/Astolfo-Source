package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderSnowball<T extends Entity> extends Render<T> {
   public final Item field_177084_a;
   private final RenderItem field_177083_e;

   public RenderSnowball(RenderManager var1, Item var2, RenderItem var3) {
      super(var1);
      this.field_177084_a = var2;
      this.field_177083_e = var3;
   }

   @Override
   public void doRender(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      this.bindTexture(TextureMap.locationBlocksTexture);
      this.field_177083_e.renderItem(this.func_177082_d((T)var1), ItemCameraTransforms.TransformType.GROUND);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender((T)var1, var2, var4, var6, var8, var9);
   }

   public ItemStack func_177082_d(T var1) {
      return new ItemStack(this.field_177084_a, 1, 0);
   }

   @Override
   public ResourceLocation getEntityTexture(Entity var1) {
      return TextureMap.locationBlocksTexture;
   }
}
