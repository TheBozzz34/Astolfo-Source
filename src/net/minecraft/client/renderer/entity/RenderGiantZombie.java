package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;

public class RenderGiantZombie extends RenderLiving<EntityGiantZombie> {
   private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
   private float scale;

   public RenderGiantZombie(RenderManager var1, ModelBase var2, float var3, float var4) {
      super(var1, var2, var3 * var4);
      this.scale = var4;
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerBipedArmor(this) {
         @Override
         public void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      });
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   public void preRenderCallback(EntityGiantZombie var1, float var2) {
      GlStateManager.scale(this.scale, this.scale, this.scale);
   }

   public ResourceLocation getEntityTexture(EntityGiantZombie var1) {
      return zombieTextures;
   }
}
