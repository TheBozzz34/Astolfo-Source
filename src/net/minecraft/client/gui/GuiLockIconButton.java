package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLockIconButton extends GuiButton {
   private boolean field_175231_o = false;
   private static final long c = 60781847749777L;

   public GuiLockIconButton(int var1, int var2, int var3) {
      super(var1, var2, var3, 20, 20, "");
   }

   public boolean func_175230_c() {
      return this.field_175231_o;
   }

   public void func_175229_b(boolean var1) {
      this.field_175231_o = var1;
   }

   @Override
   public void drawButton(Minecraft var1, int var2, int var3) {
      if (this.visible) {
         var1.getTextureManager().bindTexture(GuiButton.buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var7 = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
         GuiLockIconButton.Icon var8;
         if (this.field_175231_o) {
            if (!this.enabled) {
               var8 = GuiLockIconButton.Icon.LOCKED_DISABLED;
            } else if (var7) {
               var8 = GuiLockIconButton.Icon.LOCKED_HOVER;
            } else {
               var8 = GuiLockIconButton.Icon.LOCKED;
            }
         } else if (!this.enabled) {
            var8 = GuiLockIconButton.Icon.UNLOCKED_DISABLED;
         } else if (var7) {
            var8 = GuiLockIconButton.Icon.UNLOCKED_HOVER;
         } else {
            var8 = GuiLockIconButton.Icon.UNLOCKED;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, var8.func_178910_a(), var8.func_178912_b(), this.width, this.height);
      }
   }

   public enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int field_178914_g;
      private final int field_178920_h;

      Icon(int var3, int var4) {
         this.field_178914_g = var3;
         this.field_178920_h = var4;
      }

      public int func_178910_a() {
         return this.field_178914_g;
      }

      public int func_178912_b() {
         return this.field_178920_h;
      }
   }
}
