package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderSkeleton extends RenderBiped<EntitySkeleton> {
   private static final ResourceLocation skeletonTextures = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation witherSkeletonTextures = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
   private static final long d = 46505082216814L;

   public RenderSkeleton(RenderManager var1) {
      super(var1, new ModelSkeleton(), 0.5F);
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerBipedArmor(this) {
         @Override
         public void initArmor() {
            this.modelLeggings = new ModelSkeleton(0.5F, true);
            this.modelArmor = new ModelSkeleton(1.0F, true);
         }
      });
   }

   public void preRenderCallback(EntitySkeleton var1, float var2) {
      if (var1.getSkeletonType() == 1) {
         GlStateManager.scale(1.2F, 1.2F, 1.2F);
      }
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
   }

   public ResourceLocation getEntityTexture(EntitySkeleton var1) {
      return var1.getSkeletonType() == 1 ? witherSkeletonTextures : skeletonTextures;
   }
}
