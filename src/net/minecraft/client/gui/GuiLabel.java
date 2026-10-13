package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiLabel extends Gui {
   public int field_146167_a = 200;
   public int field_146161_f = 20;
   public int field_146162_g;
   public int field_146174_h;
   private List<String> field_146173_k;
   public int field_175204_i;
   private boolean centered;
   public boolean visible = true;
   private boolean labelBgEnabled;
   private int field_146168_n;
   private int field_146169_o;
   private int field_146166_p;
   private int field_146165_q;
   private FontRenderer fontRenderer;
   private int field_146163_s;
   private static final long b = 26853328968459L;

   public GuiLabel(FontRenderer var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fontRenderer = var1;
      this.field_175204_i = var2;
      this.field_146162_g = var3;
      this.field_146174_h = var4;
      this.field_146167_a = var5;
      this.field_146161_f = var6;
      this.field_146173_k = Lists.newArrayList();
      this.centered = false;
      this.labelBgEnabled = false;
      this.field_146168_n = var7;
      this.field_146169_o = -1;
      this.field_146166_p = -1;
      this.field_146165_q = -1;
      this.field_146163_s = 0;
   }

   public void func_175202_a(String var1) {
      this.field_146173_k.add(I18n.format(var1));
   }

   public GuiLabel setCentered() {
      this.centered = true;
      return this;
   }

   public void drawLabel(Minecraft var1, int var2, int var3) {
      if (this.visible) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         this.drawLabelBackground(var1, var2, var3);
         int var7 = this.field_146174_h + this.field_146161_f / 2 + this.field_146163_s / 2;
         int var8 = var7 - this.field_146173_k.size() * 10 / 2;

         for (int var9 = 0; var9 < this.field_146173_k.size(); var9++) {
            if (this.centered) {
               this.d(this.fontRenderer, this.field_146173_k.get(var9), this.field_146162_g + this.field_146167_a / 2, var8 + var9 * 10, this.field_146168_n);
            } else {
               this.I(this.fontRenderer, this.field_146173_k.get(var9), this.field_146162_g, var8 + var9 * 10, this.field_146168_n);
            }
         }
      }
   }

   public void drawLabelBackground(Minecraft var1, int var2, int var3) {
      if (this.labelBgEnabled) {
         int var7 = this.field_146167_a + this.field_146163_s * 2;
         int var8 = this.field_146161_f + this.field_146163_s * 2;
         int var9 = this.field_146162_g - this.field_146163_s;
         int var10 = this.field_146174_h - this.field_146163_s;
         drawRect(var9, var10, var9 + var7, var10 + var8, this.field_146169_o);
         S(var9, var9 + var7, var10, this.field_146166_p);
         S(var9, var9 + var7, var10 + var8, this.field_146165_q);
         J(var9, var10, var10 + var8, this.field_146166_p);
         J(var9 + var7, var10, var10 + var8, this.field_146165_q);
      }
   }
}
