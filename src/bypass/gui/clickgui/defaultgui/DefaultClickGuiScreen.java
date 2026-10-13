package bypass.gui.clickgui.defaultgui;

import bypass.Astolfo;
import bypass.command.ScriptCommand;
import bypass.gui.clickgui.unused.ColorWheel;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.Display;

public class DefaultClickGuiScreen extends GuiScreen {
   private boolean l = true;
   private int x;
   public static boolean B;
   private static ConfigsPanel f;
   private static ScriptsPanel h;
   private final int y = new Color(1, 1, 1, 140).getRGB();
   public static CopiedColor i;
   private Element K;
   private static final long a = 37075201047203L;

   @Override
   public void initGui() {
      long var1 = a ^ 15151420434663L;
      long var3 = var1 ^ 67661234499125L;
      if (f == null) {
         f = new ConfigsPanel();
      }

      if (h == null) {
         h = new ScriptsPanel(this);
      }

      f.o(this.width - f.x() - 0.5F);
      f.q(this.height - f.Y() - 1);
      h.o(this.width - f.x() - 110);
      h.q(this.height - f.Y() - 1);
      if (!Astolfo.INSTANCE.getDefaultPanelManager().getList().contains(f)) {
         Astolfo.INSTANCE.getDefaultPanelManager().add(f);
      }

      if (!Astolfo.INSTANCE.getDefaultPanelManager().getList().contains(h)) {
         Astolfo.INSTANCE.getDefaultPanelManager().add(h);
      }

      this.S();
      super.initGui();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      long var4 = a ^ 38712368128950L;
      long var6 = var4 ^ 21420077999460L;
      this.x++;
      float var10 = HUD.gui.getSetting("Scale").asFloat();
      RenderUtil.drawRect(0.0, 0.0, Display.getWidth(), Display.getHeight(), this.y);
      if (HUD.toolTips.asBoolean() && this.K != null) {
         if (this.K instanceof BooleanComponent) {
            BooleanComponent var11 = (BooleanComponent)this.K;
            String var12 = EnumChatFormatting.GRAY + var11.t().getName() + ": " + EnumChatFormatting.RESET + var11.t().getDescription();
            GuiMainMenu.m.drawString(var12, 3.0F, this.height - 12, -1);
         }

         if (this.K instanceof NumberSlider) {
            NumberSlider var15 = (NumberSlider)this.K;
            String var16 = EnumChatFormatting.GRAY + var15.t().getName() + ": " + EnumChatFormatting.RESET + var15.t().getDescription();
            GuiMainMenu.m.drawString(var16, 3.0F, this.height - 12, -1);
         }
      }

      f.render(var1, var2);
      h.render(var1, var2);
      var1 = (int)(var1 / var10);
      var2 = (int)(var2 / var10);
      GlStateManager.scale(var10, var10, var10);
      Astolfo.INSTANCE.getDefaultPanelManager().F(var1, var2);
      Astolfo.INSTANCE.getDefaultPanelManager().p(var1, var2);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      long var4 = a ^ 66209484263762L;
      long var6 = var4 ^ 13716852234112L;
      float var9 = HUD.gui.getSetting("Scale").asFloat();
      if (h.G(var1, var2)) {
         h.l(var3);
      }

      for (Element var11 : h.M_ArrayList()) {
         if (var11 instanceof Component && ((Component)var11).G(var1, var2) && h.s_boolean()) {
            ((Component)var11).U(var3, var1, var2);
            break;
         }
      }

      if (f.G(var1, var2)) {
         f.l(var3);
      }

      for (Element var24 : f.M_ArrayList()) {
         if (var24 instanceof Component && ((Component)var24).G(var1, var2) && f.s_boolean()) {
            ((Component)var24).U(var3, var1, var2);
            break;
         }
      }

      var1 = (int)(var1 / var9);
      var2 = (int)(var2 / var9);

      for (Element var25 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (var25 instanceof Component) {
            Component var12 = (Component)var25;
            if (var12.G(var1, var2)) {
               if (var12.c_boolean() && var3 == 0) {
                  var12.F(true, var1, var2);
                  break;
               }

               if (var12 instanceof ScriptsPanel || var12 instanceof ConfigsPanel) {
                  continue;
               }

               var12.l(var3);
            }

            if (var12 instanceof Container) {
               Container var13 = (Container)var12;
               if (!(var13 instanceof ScriptsPanel) && !(var13 instanceof ConfigsPanel)) {
                  for (Element var15 : var13.M_ArrayList()) {
                     if (var15 instanceof Component) {
                        if (((Component)var15).G(var1, var2) && var13.s_boolean()) {
                           ((Component)var15).U(var3, var1, var2);
                           this.K = var15;
                           break;
                        }

                        if (var15 instanceof Container) {
                           for (Element var17 : ((Container)var15).M_ArrayList()) {
                              if (var17 instanceof Component && ((Component)var17).G(var1, var2) && var13.s_boolean() && ((Container)var15).s_boolean()) {
                                 ((Component)var17).U(var3, var1, var2);
                                 this.K = var17;
                                 break;
                              }

                              if (var17 instanceof Container) {
                                 for (Element var19 : ((Container)var17).M_ArrayList()) {
                                    if (var19 instanceof Component
                                       && ((Component)var19).G(var1, var2)
                                       && var13.s_boolean()
                                       && ((Container)var15).s_boolean()
                                       && ((Container)var17).s_boolean()) {
                                       ((Component)var19).U(var3, var1, var2);
                                       this.K = var19;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void mouseClickMove(int var1, int var2, int var3, long var4) {
      long var6 = a ^ 47408169902474L;
      long var8 = var6 ^ 30031484086104L;
      float var11 = HUD.gui.getSetting("Scale").asFloat();
      var1 = (int)(var1 / var11);
      var2 = (int)(var2 / var11);

      for (Element var13 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (var13 instanceof Component) {
            Component var14 = (Component)var13;
            if (var14 instanceof Container) {
               Container var15 = (Container)var14;

               for (Element var17 : var15.M_ArrayList()) {
                  if (var17 instanceof Component && var17 instanceof Container) {
                     for (Element var19 : ((Container)var17).M_ArrayList()) {
                        if (var19 instanceof Component && var19 instanceof ColorWheel) {
                           ColorWheel var20 = (ColorWheel)var19;
                           if (var20.G(var1, var2)) {
                              var20.R(var1, var2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      super.mouseClickMove(var1, var2, var3, var4);
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      long var4 = a ^ 57290277591394L;
      long var6 = var4 ^ 4762086252464L;
      Astolfo.INSTANCE.getDefaultPanelManager().Q(false);
      super.mouseReleased(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      long var3 = a ^ 113237011205710L;
      long var5 = var3 ^ 95327453743260L;
      if (var2 == 54 && this.x > 5) {
         B = !B;
      }

      for (Element var9 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (var9 instanceof Component) {
            Component var10 = (Component)var9;
            if (var10 instanceof Container) {
               Container var11 = (Container)var10;

               for (Element var13 : var11.M_ArrayList()) {
                  if (var13 instanceof Component && var13 instanceof Container) {
                     ((Container)var13).P(var1, var2);

                     for (Element var15 : ((Container)var13).M_ArrayList()) {
                        if (var15 instanceof StringComponent) {
                           StringComponent var16 = (StringComponent)var15;
                           if (var16.E() && var11.s_boolean() && ((Container)var13).s_boolean()) {
                              var16.P(var1, var2);
                              break;
                           }
                        }

                        if (var15 instanceof Container) {
                           for (Element var17 : ((Container)var15).M_ArrayList()) {
                              if (var17 instanceof StringComponent) {
                                 StringComponent var18 = (StringComponent)var17;
                                 if (var18.E() && var11.s_boolean() && ((Container)var13).s_boolean() && ((Container)var15).s_boolean()) {
                                    var18.P(var1, var2);
                                    break;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      for (Element var20 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (var20 instanceof Component) {
            Component var21 = (Component)var20;
            if (var21 instanceof Container) {
               Container var22 = (Container)var21;
               if (var22.s_boolean()) {
                  for (Element var24 : var22.M_ArrayList()) {
                     if (var24 instanceof bypass.gui.clickgui.transparent.ModuleButton) {
                        ((bypass.gui.clickgui.transparent.ModuleButton)var24).P(var1, var2);
                     }

                     if (var24 instanceof Container && ((Container)var24).s_boolean()) {
                        for (Element var26 : ((Container)var24).M_ArrayList()) {
                           if (var26 instanceof bypass.gui.clickgui.transparent.StringComponent && ((bypass.gui.clickgui.transparent.StringComponent)var26).k()
                              )
                            {
                              ((bypass.gui.clickgui.transparent.StringComponent)var26).P(var1, var2);
                           }

                           if (var26 instanceof bypass.gui.clickgui.transparent.ModuleButton) {
                              ((bypass.gui.clickgui.transparent.ModuleButton)var26).P(var1, var2);
                           }

                           if (var26 instanceof Container) {
                              for (Element var29 : ((Container)var26).M_ArrayList()) {
                                 if (((Container)var26).s_boolean()
                                    && var29 instanceof bypass.gui.clickgui.transparent.StringComponent
                                    && ((bypass.gui.clickgui.transparent.StringComponent)var29).k()) {
                                    ((bypass.gui.clickgui.transparent.StringComponent)var29).P(var1, var2);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      super.keyTyped(var1, var2);
   }

   public void S() {
      long var1 = a ^ 80818549821563L;
      long var3 = var1 ^ 104792724428651L;
      if (ScriptCommand.z) {
         ScriptCommand.z = false;
         CategoryPanel var6 = this.A("Scripts");
         if (var6 != null) {
            var6.M_ArrayList().clear();

            for (Module var8 : Astolfo.INSTANCE.getModuleManager().getModules(Category.SCRIPTS)) {
               ModuleButton var9 = new ModuleButton(var6, var8, 103, 15);

               for (Setting var11 : var8.getSettings()) {
                  if (var11 instanceof BooleanSetting) {
                     if (var11.getName().startsWith("Pos-") || var11.getName().equals("Hide")) {
                        continue;
                     }

                     var9.M_ArrayList().add(new BooleanComponent(Category.SCRIPTS, var9, var11, 99, 10));
                  }

                  if (var11 instanceof NumberSetting && !var11.getName().startsWith("Pos-") && !var11.getName().equals("Hide")) {
                     var9.M_ArrayList().add(new NumberSlider(Category.SCRIPTS, var9, var11, 99, 10));
                  }
               }

               var6.a(var9);
            }
         }
      }
   }

   private CategoryPanel A(String var1) {
      long var2 = a ^ 119011607330005L;
      long var4 = var2 ^ 101651679853063L;

      for (Element var8 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (var8 instanceof CategoryPanel && var8.J().equalsIgnoreCase(var1)) {
            return (CategoryPanel)var8;
         }
      }

      return null;
   }

   @Override
   public void onGuiClosed() {
      this.x = 0;
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
