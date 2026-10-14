package net.minecraft.src;

import java.util.Map;
import java.util.Set;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public class PlayerItemsLayer implements LayerRenderer {
   private RenderPlayer renderPlayer = null;
   private static final long a = 42064702067052L;

   public PlayerItemsLayer(RenderPlayer renderPlayer) {
      this.renderPlayer = renderPlayer;
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      this.renderEquippedItems(entitylivingbaseIn, scale, partialTicks);
   }

   public void renderEquippedItems(EntityLivingBase entityLiving, float scale, float partialTicks) {
      if (Config.b() && entityLiving instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityLiving;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableRescaleNormal();
         GlStateManager.enableCull();
         ModelPlayer modelPlayer = this.renderPlayer.getMainModel();
         PlayerConfigurations.renderPlayerItems(modelPlayer, abstractClientPlayer, scale, partialTicks);
         GlStateManager.disableCull();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   public static void register(Map renderPlayerMap) {
      Set set = renderPlayerMap.keySet();
      boolean bl = false;

      for (Object object : set) {
         Object object2 = renderPlayerMap.get(object);
         if (object2 instanceof RenderPlayer) {
            RenderPlayer renderPlayer = (RenderPlayer)object2;
            renderPlayer.addLayer(new PlayerItemsLayer(renderPlayer));
            bl = true;
         }
      }

      if (!bl) {
         Config.warn("PlayerItemsLayer not registered");
      }
   }
}
