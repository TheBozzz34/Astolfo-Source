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
   private static final long a = 62532206398646L;

   public GuiSpectator(Minecraft var1) {
      this.field_175268_g = var1;
   }

   public void func_175260_a(int var1) {
      this.field_175270_h = Minecraft.getSystemTime();
      if (this.field_175271_i != null) {
         this.field_175271_i.func_178644_b(var1);
      } else {
         this.field_175271_i = new SpectatorMenu(this);
      }
   }

   private float func_175265_c() {
      long var1 = this.field_175270_h - Minecraft.getSystemTime() + 5000L;
      return MathHelper.clamp_float((float)var1 / 2000.0F, 0.0F, 1.0F);
   }

   public void renderTooltip(ScaledResolution var1, float var2) {
      if (this.field_175271_i != null) {
         float var6 = this.func_175265_c();
         if (var6 <= 0.0F) {
            this.field_175271_i.func_178641_d();
         } else {
            int var7 = var1.getScaledWidth() / 2;
            float var8 = this.zLevel;
            this.zLevel = -90.0F;
            float var9 = var1.getScaledHeight() - 22.0F * var6;
            SpectatorDetails var10 = this.field_175271_i.func_178646_f();
            this.func_175258_a(var1, var6, var7, var9, var10);
            this.zLevel = var8;
         }
      }
   }

   public void func_175258_a(ScaledResolution var1, float var2, int var3, float var4, SpectatorDetails var5) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, var2);
      this.field_175268_g.getTextureManager().bindTexture(field_175267_f);
      this.drawTexturedModalRect(var3 - 91, var4, 0, 0, 182, 22);
      if (var5.func_178681_b() >= 0) {
         this.drawTexturedModalRect(var3 - 91 - 1 + var5.func_178681_b() * 20, var4 - 1.0F, 0, 22, 24, 22);
      }

      RenderHelper.enableGUIStandardItemLighting();

      for (int var9 = 0; var9 < 9; var9++) {
         this.func_175266_a(var9, var1.getScaledWidth() / 2 - 90 + var9 * 20 + 2, var4 + 3.0F, var2, var5.func_178680_a(var9));
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
   }

   private void func_175266_a(int var1, int var2, float var3, float var4, ISpectatorMenuObject var5) {
      this.field_175268_g.getTextureManager().bindTexture(field_175269_a);
      if (var5 != SpectatorMenu.field_178657_a) {
         int var9 = (int)(var4 * 255.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate(var2, var3, 0.0F);
         float var10 = var5.func_178662_A_() ? 1.0F : 0.25F;
         GlStateManager.color(var10, var10, var10, var4);
         var5.func_178663_a(var10, var9);
         GlStateManager.popMatrix();
         String var11 = String.valueOf(GameSettings.getKeyDisplayString(this.field_175268_g.gameSettings.keyBindsHotbar[var1].getKeyCode()));
         if (var9 > 3 && var5.func_178662_A_()) {
            this.field_175268_g
               .fontRendererObj
               .drawStringWithShadow(
                  var11, var2 + 19 - 2 - this.field_175268_g.fontRendererObj.getStringWidth(var11), var3 + 6.0F + 3.0F, 16777215 + (var9 << 24)
               );
         }
      }
   }

   public void renderSelectedItem(ScaledResolution var1) {
      long var2 = a ^ 113440549419096L;
      int var5 = (int)(this.func_175265_c() * 255.0F);
      if (var5 > 3 && this.field_175271_i != null) {
         ISpectatorMenuObject var6 = this.field_175271_i.func_178645_b();
         String var7 = var6 != SpectatorMenu.field_178657_a
            ? var6.getSpectatorName().getFormattedText()
            : this.field_175271_i.func_178650_c().func_178670_b().getFormattedText();
         if (var7 != null) {
            int var8 = (var1.getScaledWidth() - this.field_175268_g.fontRendererObj.getStringWidth(var7)) / 2;
            int var9 = var1.getScaledHeight() - 35;
            GlStateManager.pushMatrix();
            long var10000 = 1040590098252509686L ^ var2;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.field_175268_g.fontRendererObj.drawStringWithShadow(var7, var8, var9, 16777215 + (var5 << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public void func_175257_a(SpectatorMenu var1) {
      this.field_175271_i = null;
      this.field_175270_h = 0L;
   }

   public boolean func_175262_a() {
      return this.field_175271_i != null;
   }

   public void func_175259_b(int var1) {
      int var5 = this.field_175271_i.func_178648_e() + var1;

      while (
         var5 >= 0
            && var5 <= 8
            && (this.field_175271_i.func_178643_a(var5) == SpectatorMenu.field_178657_a || !this.field_175271_i.func_178643_a(var5).func_178662_A_())
      ) {
         var5 += var1;
      }

      if (var5 >= 0 && var5 <= 8) {
         this.field_175271_i.func_178644_b(var5);
         this.field_175270_h = Minecraft.getSystemTime();
      }
   }

   public void func_175261_b() {
      this.field_175270_h = Minecraft.getSystemTime();
      if (this.func_175262_a()) {
         int var4 = this.field_175271_i.func_178648_e();
         if (var4 != -1) {
            this.field_175271_i.func_178644_b(var4);
         }
      } else {
         this.field_175271_i = new SpectatorMenu(this);
      }
   }
}
