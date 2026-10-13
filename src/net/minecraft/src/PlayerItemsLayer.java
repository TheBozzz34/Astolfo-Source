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

   public PlayerItemsLayer(RenderPlayer var1) {
      this.renderPlayer = var1;
   }

   @Override
   public void doRenderLayer(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.renderEquippedItems(var1, var8, var4);
   }

   public void renderEquippedItems(EntityLivingBase var1, float var2, float var3) {
      if (Config.b() && var1 instanceof AbstractClientPlayer) {
         AbstractClientPlayer var7 = (AbstractClientPlayer)var1;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableRescaleNormal();
         GlStateManager.enableCull();
         ModelPlayer var8 = this.renderPlayer.getMainModel();
         PlayerConfigurations.renderPlayerItems(var8, var7, var2, var3);
         GlStateManager.disableCull();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   public static void register(Map var0) {
      Set var4 = var0.keySet();
      boolean var5 = false;

      for (Object var7 : var4) {
         Object var8 = var0.get(var7);
         if (var8 instanceof RenderPlayer) {
            RenderPlayer var9 = (RenderPlayer)var8;
            var9.addLayer(new PlayerItemsLayer(var9));
            var5 = true;
         }
      }

      if (!var5) {
         Config.warn("PlayerItemsLayer not registered");
      }
   }
}
