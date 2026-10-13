package net.minecraft.client.renderer;

import java.util.Collection;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class InventoryEffectRenderer extends GuiContainer {
   private boolean hasActivePotionEffects;
   private static final long eb = 54053655700859L;

   public InventoryEffectRenderer(Container var1) {
      super(var1);
   }

   @Override
   public void initGui() {
      super.initGui();
      this.updateActivePotionEffects();
   }

   public void updateActivePotionEffects() {
      this.guiLeft = (this.width - this.xSize) / 2;
      this.hasActivePotionEffects = !this.mc.thePlayer.getActivePotionEffects().isEmpty();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      super.drawScreen(var1, var2, var3);
      if (this.hasActivePotionEffects) {
         this.drawActivePotionEffects();
      }
   }

   private void drawActivePotionEffects() {
      long var1 = eb ^ 113550737551618L;
      int var4 = this.guiLeft - 124;
      int var5 = this.guiTop;
      Collection var7 = this.mc.thePlayer.getActivePotionEffects();
      if (!var7.isEmpty()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         long var10000 = 7280758743181350207L ^ var1;
         int var8 = 33;
         if (var7.size() > 5) {
            var8 = 132 / (var7.size() - 1);
         }

         for (PotionEffect var10 : this.mc.thePlayer.getActivePotionEffects()) {
            Potion var11 = Potion.potionTypes[var10.getPotionID()];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(inventoryBackground);
            this.drawTexturedModalRect(var4, var5, 0, 166, 140, 32);
            if (var11.hasStatusIcon()) {
               int var12 = var11.getStatusIconIndex();
               this.drawTexturedModalRect(var4 + 6, var5 + 7, 0 + var12 % 8 * 18, 198 + var12 / 8 * 18, 18, 18);
            }

            String var14 = I18n.format(var11.getName());
            if (var10.getAmplifier() == 1) {
               var14 = var14 + " " + I18n.format("enchantment.level.2");
            } else if (var10.getAmplifier() == 2) {
               var14 = var14 + " " + I18n.format("enchantment.level.3");
            } else if (var10.getAmplifier() == 3) {
               var14 = var14 + " " + I18n.format("enchantment.level.4");
            }

            this.fontRendererObj.drawStringWithShadow(var14, var4 + 10 + 18, var5 + 6, 16777215);
            String var13 = Potion.getDurationString(var10);
            this.fontRendererObj.drawStringWithShadow(var13, var4 + 10 + 18, var5 + 6 + 10, 8355711);
            var5 += var8;
         }
      }
   }
}
