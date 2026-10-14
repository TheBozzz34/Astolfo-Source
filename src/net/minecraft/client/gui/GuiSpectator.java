package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiSpectator extends Gui implements ISpectatorMenuRecipient {
   private static final ResourceLocation field_175267_f = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation field_175269_a = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft field_175268_g;
   private long field_175270_h;
   private SpectatorMenu field_175271_i;

   public GuiSpectator(Minecraft mcIn) {
      this.field_175268_g = mcIn;
   }

   public void func_175260_a(int i) {
      this.field_175270_h = Minecraft.getSystemTime();
      if (this.field_175271_i != null) {
         this.field_175271_i.func_178644_b(i);
      } else {
         this.field_175271_i = new SpectatorMenu(this);
      }
   }

   private float func_175265_c() {
      long l = this.field_175270_h - Minecraft.getSystemTime() + 5000L;
      return MathHelper.clamp_float((float)l / 2000.0F, 0.0F, 1.0F);
   }

   public void renderTooltip(ScaledResolution scaledResolution, float f) {
      if (this.field_175271_i != null) {
         float g = this.func_175265_c();
         if (g <= 0.0F) {
            this.field_175271_i.func_178641_d();
         } else {
            int i = scaledResolution.getScaledWidth() / 2;
            float h = this.zLevel;
            this.zLevel = -90.0F;
            float f2 = scaledResolution.getScaledHeight() - 22.0F * g;
            SpectatorDetails spectatorDetails = this.field_175271_i.func_178646_f();
            this.func_175258_a(scaledResolution, g, i, f2, spectatorDetails);
            this.zLevel = h;
         }
      }
   }

   public void func_175258_a(ScaledResolution scaledResolution, float f, int i, float g, SpectatorDetails spectatorDetails) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, f);
      this.field_175268_g.getTextureManager().bindTexture(field_175267_f);
      this.drawTexturedModalRect(i - 91, g, 0, 0, 182, 22);
      if (spectatorDetails.func_178681_b() >= 0) {
         this.drawTexturedModalRect(i - 91 - 1 + spectatorDetails.func_178681_b() * 20, g - 1.0F, 0, 22, 24, 22);
      }

      RenderHelper.enableGUIStandardItemLighting();

      for (int j = 0; j < 9; j++) {
         this.func_175266_a(j, scaledResolution.getScaledWidth() / 2 - 90 + j * 20 + 2, g + 3.0F, f, spectatorDetails.func_178680_a(j));
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
   }

   private void func_175266_a(int i, int j, float f, float g, ISpectatorMenuObject iSpectatorMenuObject) {
      this.field_175268_g.getTextureManager().bindTexture(field_175269_a);
      if (iSpectatorMenuObject != SpectatorMenu.field_178657_a) {
         int k = (int)(g * 255.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate(j, f, 0.0F);
         float h = iSpectatorMenuObject.func_178662_A_() ? 1.0F : 0.25F;
         GlStateManager.color(h, h, h, g);
         iSpectatorMenuObject.func_178663_a(h, k);
         GlStateManager.popMatrix();
         String string = String.valueOf(GameSettings.getKeyDisplayString(this.field_175268_g.gameSettings.keyBindsHotbar[i].getKeyCode()));
         if (k > 3 && iSpectatorMenuObject.func_178662_A_()) {
            this.field_175268_g
               .fontRendererObj
               .drawStringWithShadow(string, j + 19 - 2 - this.field_175268_g.fontRendererObj.getStringWidth(string), f + 6.0F + 3.0F, 16777215 + (k << 24));
         }
      }
   }

   public void renderSelectedItem(ScaledResolution scaledResolution) {
      int i = (int)(this.func_175265_c() * 255.0F);
      if (i > 3 && this.field_175271_i != null) {
         ISpectatorMenuObject iSpectatorMenuObject = this.field_175271_i.func_178645_b();
         String string = iSpectatorMenuObject != SpectatorMenu.field_178657_a
            ? iSpectatorMenuObject.getSpectatorName().getFormattedText()
            : this.field_175271_i.func_178650_c().func_178670_b().getFormattedText();
         if (string != null) {
            int j = (scaledResolution.getScaledWidth() - this.field_175268_g.fontRendererObj.getStringWidth(string)) / 2;
            int k = scaledResolution.getScaledHeight() - 35;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.field_175268_g.fontRendererObj.drawStringWithShadow(string, j, k, 16777215 + (i << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public void func_175257_a(SpectatorMenu spectatorMenu) {
      this.field_175271_i = null;
      this.field_175270_h = 0L;
   }

   public boolean func_175262_a() {
      return this.field_175271_i != null;
   }

   public void func_175259_b(int i) {
      int j = this.field_175271_i.func_178648_e() + i;

      while (
         j >= 0
            && j <= 8
            && (this.field_175271_i.func_178643_a(j) == SpectatorMenu.field_178657_a || !this.field_175271_i.func_178643_a(j).func_178662_A_())
      ) {
         j += i;
      }

      if (j >= 0 && j <= 8) {
         this.field_175271_i.func_178644_b(j);
         this.field_175270_h = Minecraft.getSystemTime();
      }
   }

   public void func_175261_b() {
      this.field_175270_h = Minecraft.getSystemTime();
      if (this.func_175262_a()) {
         int i = this.field_175271_i.func_178648_e();
         if (i != -1) {
            this.field_175271_i.func_178644_b(i);
         }
      } else {
         this.field_175271_i = new SpectatorMenu(this);
      }
   }
}
