package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.render.RenderUtil;
import bypass.setting.EnumSetting;
import bypass.setting.Setting;
import java.awt.Color;
import org.lwjgl.input.Mouse;

public class EnumComponent extends DropdownComponent {
   public Animation u = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private float G;
   private int[] r = new int[128];
   private static final long d = 76371513487449L;

   public EnumComponent(ModuleButton var1, Setting var2, float var3, float var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 90685667257899L;
      long var5 = var3 ^ 8972929756414L;
      float var8 = this.M() + this.x() / 1.7F;
      RenderUtil.drawRect(var8 - 1.0F, this.E_float() - 2.0F, this.M() + this.x() + 1.0F, this.E_float() + 9.0F, new Color(34, 36, 43, this.X).getRGB());
      RenderUtil.drawRect(var8, this.E_float() - 1.0F, this.M() + this.x(), this.E_float() + 8.0F, new Color(29, 31, 37, this.X).getRGB());
      if (this.G > 0.0F && client.getSidebarGui().A() != this) {
         RenderUtil.drawRect(
            var8 - 0.5F, this.E_float() + 8.0F, this.M() + this.x() + 0.5F, this.E_float() + this.G + 8.5F, new Color(34, 36, 43, this.X).getRGB()
         );
         RenderUtil.drawRect(var8, this.E_float() + 8.0F, this.M() + this.x(), this.E_float() + this.G + 8.0F, new Color(29, 31, 37, this.X).getRGB());
         this.r = new int[128];
         if (!this.u.isRunning()) {
            this.u.backwards().start();
         }

         this.G = (float)this.u.getValue(0.0, this.O_int());
      }

      if (this.X > 40) {
         client.getCustomFontRenderer().drawString(this.n().getName(), this.M(), this.E_float() - 2.0F, new Color(255, 255, 255, this.X).getRGB());
         client.getCustomFontRenderer()
            .drawString(
               this.n().getValue().name(),
               var8 + this.x() / 4.5F - client.getCustomFontRenderer().getStringWidth(this.n().getValue().name()) / 2.0F,
               this.E_float() - 2.0F,
               new Color(136, 141, 157, this.X).getRGB()
            );
      }
   }

   @Override
   public void M(int var1, int var2) {
      long var3 = d ^ 2296424361950L;
      long var5 = var3 ^ 97057255664907L;
      float var8 = this.M() + this.x() / 1.7F;
      Enum[] var9 = (Enum[])this.n().getValue().getClass().getEnumConstants();
      RenderUtil.drawRect(
         var8 - 1.0F, this.E_float() + 8.0F, this.M() + this.x() + 1.0F, this.E_float() + this.G + 9.0F, new Color(34, 36, 43, this.X).getRGB()
      );
      RenderUtil.drawRect(var8, this.E_float() + 8.0F, this.M() + this.x(), this.E_float() + this.G + 8.0F, new Color(29, 31, 37, this.X).getRGB());
      int var10 = client.getSidebarGui().M();
      byte var11 = 13;

      for (int var12 = 0; var12 < var9.length; var12++) {
         Enum var13 = var9[var12];
         if (this.r[var12] > 30) {
            client.getCustomFontRenderer()
               .drawString(
                  var13.name(),
                  var8 + this.x() / 4.5F - client.getCustomFontRenderer().getStringWidth(var13.name()) / 2.0F,
                  this.E_float() + var11 - 3.0F,
                  new Color(136, 141, 157, this.r[var12]).getRGB()
               );
         }

         if (this.o(var1, var2, (int)(this.E_float() + var11)) && Mouse.isButtonDown(0)) {
            this.n().setValue(var13);
            client.getSidebarGui().G(null);
         }

         if (this.G >= var11 && this.r[var12] < 255) {
            this.r[var12] = (int)this.u.getValue(0.0, 255.0);
         }

         var11 += 13;
      }

      if (!this.u.isRunning()) {
         this.u.forwards().start();
      }

      this.G = (float)this.u.getValue(0.0, this.O_int());
      super.M(var1, var2);
   }

   public boolean o(int var1, int var2, int var3) {
      return var1 >= this.M() + this.x() / 1.7F && var1 <= this.M() + this.x() && var2 >= var3 && var2 <= var3 + this.Y();
   }

   private int O_int() {
      Enum[] var4 = (Enum[])this.n().getValue().getClass().getEnumConstants();
      byte var5 = 0;

      for (Enum var9 : var4) {
         var5 += 13;
      }

      return var5;
   }

   public EnumSetting n() {
      return (EnumSetting)this.H();
   }
}
