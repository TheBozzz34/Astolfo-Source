package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLockIconButton extends GuiButton {
   private boolean field_175231_o = false;

   public GuiLockIconButton(int i, int j, int k) {
      super(i, j, k, 20, 20, "");
   }

   public boolean func_175230_c() {
      return this.field_175231_o;
   }

   public void func_175229_b(boolean bl) {
      this.field_175231_o = bl;
   }

   @Override
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean bl = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         GuiLockIconButton.Icon icon;
         if (this.field_175231_o) {
            if (!this.enabled) {
               icon = GuiLockIconButton.Icon.LOCKED_DISABLED;
            } else if (bl) {
               icon = GuiLockIconButton.Icon.LOCKED_HOVER;
            } else {
               icon = GuiLockIconButton.Icon.LOCKED;
            }
         } else if (!this.enabled) {
            icon = GuiLockIconButton.Icon.UNLOCKED_DISABLED;
         } else if (bl) {
            icon = GuiLockIconButton.Icon.UNLOCKED_HOVER;
         } else {
            icon = GuiLockIconButton.Icon.UNLOCKED;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, icon.func_178910_a(), icon.func_178912_b(), this.width, this.height);
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

      Icon(int j, int k) {
         this.field_178914_g = j;
         this.field_178920_h = k;
      }

      public int func_178910_a() {
         return this.field_178914_g;
      }

      public int func_178912_b() {
         return this.field_178920_h;
      }
   }
}
