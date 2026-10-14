package net.minecraft.client.renderer;

import java.util.Collection;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class InventoryEffectRenderer extends GuiContainer {
   private boolean hasActivePotionEffects;

   public InventoryEffectRenderer(Container inventorySlotsIn) {
      super(inventorySlotsIn);
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
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.hasActivePotionEffects) {
         this.drawActivePotionEffects();
      }
   }

   private void drawActivePotionEffects() {
      int i = this.guiLeft - 124;
      int j = this.guiTop;
      Collection collection = this.mc.thePlayer.getActivePotionEffects();
      if (!collection.isEmpty()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         int k = 33;
         if (collection.size() > 5) {
            k = 132 / (collection.size() - 1);
         }

         for (PotionEffect potionEffect : this.mc.thePlayer.getActivePotionEffects()) {
            Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(inventoryBackground);
            this.drawTexturedModalRect(i, j, 0, 166, 140, 32);
            if (potion.hasStatusIcon()) {
               int l = potion.getStatusIconIndex();
               this.drawTexturedModalRect(i + 6, j + 7, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
            }

            String string = I18n.format(potion.getName());
            if (potionEffect.getAmplifier() == 1) {
               string = string + " " + I18n.format("enchantment.level.2");
            } else if (potionEffect.getAmplifier() == 2) {
               string = string + " " + I18n.format("enchantment.level.3");
            } else if (potionEffect.getAmplifier() == 3) {
               string = string + " " + I18n.format("enchantment.level.4");
            }

            this.fontRendererObj.drawStringWithShadow(string, i + 10 + 18, j + 6, 16777215);
            String string2 = Potion.getDurationString(potionEffect);
            this.fontRendererObj.drawStringWithShadow(string2, i + 10 + 18, j + 6 + 10, 8355711);
            j += k;
         }
      }
   }
}
