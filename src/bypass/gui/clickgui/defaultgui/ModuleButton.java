package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.clickgui.SettingContainer;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Module;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.input.Keyboard;

public class ModuleButton extends Container {
   private final CategoryPanel h;
   private final Module p;
   private int d = 0;
   private boolean u;
   private static final long e = 84103440236050L;

   public ModuleButton(CategoryPanel var1, Module var2, int var3, int var4) {
      super(var2.getName(), 0.0F, 0.0F, var3, var4);
      this.h = var1;
      this.p = var2;
   }

   @Override
   public void l(int var1) {
      if (this.h.s_boolean()) {
         if (var1 == 0) {
            this.p.setEnabled(!this.p.isEnabled());
         }

         if (var1 == 1 && DefaultClickGuiScreen.B && Keyboard.isKeyDown(42)) {
            this.u = !this.u;
            return;
         }

         if (var1 == 1 && this.d > 0 && !this.M_ArrayList().isEmpty()) {
            this.u(!this.s_boolean());
            if (this.s_boolean()) {
               this.U.backwards().rewind().start();
            } else {
               this.U.forwards().rewind().start();
            }
         }
      }
   }

   @Override
   public void P(char var1, int var2) {
      if (DefaultClickGuiScreen.B) {
         if (this.u && var2 == 1) {
            this.u = false;
         }

         if (this.u) {
            this.p.setKey(var2);
            this.u = false;
         }
      }

      super.P(var1, var2);
   }

   @Override
   public void render(int var1, int var2) {
      int var6 = this.u();
      boolean var7 = HUD.gui.getSetting("Left-Align").asBoolean();
      int var8 = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.h.c().getColor() : client.getColorSetting().getValue().getRGB();
      if (this.p.isEnabled()) {
         if (this.G(var1, var2)) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(var8).darker().getRGB());
         } else {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), var8);
         }
      } else if (this.G(var1, var2 - 1)) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(52, 52, 52).getRGB());
      }

      if (this.s_boolean()) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + var6, new Color(37, 37, 37).darker().getRGB());
      }

      if (DefaultClickGuiScreen.B && (this.p.getKey() != 0 || this.u)) {
         try {
            String var9 = String.format("[%s]", this.u ? "..." : Keyboard.getKeyName(this.p.getKey()));
            GuiMainMenu.m
               .drawString(
                  var9, var7 ? this.M() + this.x() - GuiMainMenu.m.getStringWidth(var9) - 2.0F : this.M() + 3.0F, this.E_float() + 2.0F, Color.GRAY.getRGB()
               );
         } catch (Exception var12) {
         }
      }

      GuiMainMenu.m
         .drawString(
            this.p.getName().toLowerCase(),
            var7 ? this.M() + 3.0F : this.M() + this.x() - GuiMainMenu.m.getStringWidth(this.p.getName().toLowerCase(), true) - 3.0F,
            this.E_float() + 3.0F,
            this.s_boolean() && this.p.isEnabled() ? var8 : 13421772
         );
      if (this.s_boolean()) {
         int var13 = 0;

         for (Element var11 : this.M_ArrayList()) {
            if (var11 instanceof Component) {
               ((Component)var11).r(this.M() + 2.0F, this.E_float() + var13 + 15.0F);
               if (this.s_boolean()) {
                  var11.render(var1, var2);
               }

               var13 += ((Component)var11).Y() + (var11 instanceof MapComponent && ((MapComponent)var11).s_boolean() ? ((MapComponent)var11).i() : 0);
            }
         }
      }

      if (this.h.s_boolean()) {
         this.d++;
      } else {
         this.d = 0;
      }
   }

   public int c() {
      int var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof SettingComponent || var6 instanceof SettingContainer) {
               var4 += ((Component)var6).Y();
            }
         }
      }

      return var4 + 18;
   }

   public int u() {
      int var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof SettingComponent || var6 instanceof SettingContainer) {
               var4 += ((Component)var6).Y();
            }

            if (var6 instanceof MapComponent) {
               var4 += ((MapComponent)var6).i();
            }
         }
      }

      return var4 + 18;
   }

   public CategoryPanel O() {
      return this.h;
   }
}
