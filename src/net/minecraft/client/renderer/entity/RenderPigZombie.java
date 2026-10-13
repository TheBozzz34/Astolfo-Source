package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.util.ResourceLocation;

public class RenderPigZombie extends RenderBiped<EntityPigZombie> {
   private static final ResourceLocation ZOMBIE_PIGMAN_TEXTURE = new ResourceLocation("textures/entity/zombie_pigman.png");

   public RenderPigZombie(RenderManager var1) {
      super(var1, new ModelZombie(), 0.5F, 1.0F);
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerBipedArmor(this) {
         @Override
         public void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      });
   }

   public ResourceLocation getEntityTexture(EntityPigZombie var1) {
      return ZOMBIE_PIGMAN_TEXTURE;
   }
}
