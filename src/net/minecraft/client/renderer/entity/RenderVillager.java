package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderVillager extends RenderLiving<EntityVillager> {
   private static final ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
   private static final ResourceLocation farmerVillagerTextures = new ResourceLocation("textures/entity/villager/farmer.png");
   private static final ResourceLocation librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png");
   private static final ResourceLocation priestVillagerTextures = new ResourceLocation("textures/entity/villager/priest.png");
   private static final ResourceLocation smithVillagerTextures = new ResourceLocation("textures/entity/villager/smith.png");
   private static final ResourceLocation butcherVillagerTextures = new ResourceLocation("textures/entity/villager/butcher.png");
   private static final long d = 64161802497687L;

   public RenderVillager(RenderManager var1) {
      super(var1, new ModelVillager(0.0F), 0.5F);
      this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
   }

   public ModelVillager getMainModel() {
      return (ModelVillager)super.getMainModel();
   }

   public ResourceLocation getEntityTexture(EntityVillager var1) {
      switch (var1.getProfession()) {
         case 0:
            return farmerVillagerTextures;
         case 1:
            return librarianVillagerTextures;
         case 2:
            return priestVillagerTextures;
         case 3:
            return smithVillagerTextures;
         case 4:
            return butcherVillagerTextures;
         default:
            return villagerTextures;
      }
   }

   public void preRenderCallback(EntityVillager var1, float var2) {
      float var6 = 0.9375F;
      if (var1.getGrowingAge() < 0) {
         var6 = (float)(var6 * 0.5);
         this.shadowSize = 0.25F;
      } else {
         this.shadowSize = 0.5F;
      }

      GlStateManager.scale(var6, var6, var6);
   }
}
