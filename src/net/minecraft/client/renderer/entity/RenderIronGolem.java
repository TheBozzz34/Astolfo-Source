package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

public class RenderIronGolem extends RenderLiving<EntityIronGolem> {
   private static final ResourceLocation ironGolemTextures = new ResourceLocation("textures/entity/iron_golem.png");
   private static final long d = 139299312565541L;

   public RenderIronGolem(RenderManager var1) {
      super(var1, new ModelIronGolem(), 0.5F);
      this.addLayer(new LayerIronGolemFlower(this));
   }

   public ResourceLocation getEntityTexture(EntityIronGolem var1) {
      return ironGolemTextures;
   }

   public void rotateCorpse(EntityIronGolem var1, float var2, float var3, float var4) {
      super.rotateCorpse(var1, var2, var3, var4);
      if (var1.limbSwingAmount >= 0.01) {
         float var8 = 13.0F;
         float var9 = var1.limbSwing - var1.limbSwingAmount * (1.0F - var4) + 6.0F;
         float var10 = (Math.abs(var9 % var8 - var8 * 0.5F) - var8 * 0.25F) / (var8 * 0.25F);
         GlStateManager.rotate(6.5F * var10, 0.0F, 0.0F, 1.0F);
      }
   }
}
