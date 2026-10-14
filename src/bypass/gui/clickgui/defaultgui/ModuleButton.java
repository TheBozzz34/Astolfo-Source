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

   public ModuleButton(CategoryPanel categoryPanel, Module module, int i, int j) {
      super(module.getName(), 0.0F, 0.0F, i, j);
      this.h = categoryPanel;
      this.p = module;
   }

   @Override
   public void l(int i) {
      if (this.h.s_boolean()) {
         if (i == 0) {
            this.p.setEnabled(!this.p.isEnabled());
         }

         if (i == 1 && DefaultClickGuiScreen.B && Keyboard.isKeyDown(42)) {
            this.u = !this.u;
            return;
         }

         if (i == 1 && this.d > 0 && !this.M_ArrayList().isEmpty()) {
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
   public void P(char c, int i) {
      if (DefaultClickGuiScreen.B) {
         if (this.u && i == 1) {
            this.u = false;
         }

         if (this.u) {
            this.p.setKey(i);
            this.u = false;
         }
      }

      super.P(c, i);
   }

   @Override
   public void render(int i, int j) {
      int k = this.u();
      boolean bl = HUD.gui.getSetting("Left-Align").asBoolean();
      int l = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.h.c().getColor() : client.getColorSetting().getValue().getRGB();
      if (this.p.isEnabled()) {
         if (this.G(i, j)) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(l).darker().getRGB());
         } else {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), l);
         }
      } else if (this.G(i, j - 1)) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(52, 52, 52).getRGB());
      }

      if (this.s_boolean()) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + k, new Color(37, 37, 37).darker().getRGB());
      }

      if (DefaultClickGuiScreen.B && (this.p.getKey() != 0 || this.u)) {
         try {
            String string = String.format("[%s]", this.u ? "..." : Keyboard.getKeyName(this.p.getKey()));
            GuiMainMenu.m
               .drawString(
                  string, bl ? this.M() + this.x() - GuiMainMenu.m.getStringWidth(string) - 2.0F : this.M() + 3.0F, this.E_float() + 2.0F, Color.GRAY.getRGB()
               );
         } catch (Exception exception) {
         }
      }

      GuiMainMenu.m
         .drawString(
            this.p.getName().toLowerCase(),
            bl ? this.M() + 3.0F : this.M() + this.x() - GuiMainMenu.m.getStringWidth(this.p.getName().toLowerCase(), true) - 3.0F,
            this.E_float() + 3.0F,
            this.s_boolean() && this.p.isEnabled() ? l : 13421772
         );
      if (this.s_boolean()) {
         int n = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof Component) {
               ((Component)element).r(this.M() + 2.0F, this.E_float() + n + 15.0F);
               if (this.s_boolean()) {
                  element.render(i, j);
               }

               n += ((Component)element).Y() + (element instanceof MapComponent && ((MapComponent)element).s_boolean() ? ((MapComponent)element).i() : 0);
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
      int i = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof SettingComponent || element instanceof SettingContainer) {
               i += ((Component)element).Y();
            }
         }
      }

      return i + 18;
   }

   public int u() {
      int j = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof SettingComponent || element instanceof SettingContainer) {
               j += ((Component)element).Y();
            }

            if (element instanceof MapComponent) {
               j += ((MapComponent)element).i();
            }
         }
      }

      return j + 18;
   }

   public CategoryPanel O() {
      return this.h;
   }
}
