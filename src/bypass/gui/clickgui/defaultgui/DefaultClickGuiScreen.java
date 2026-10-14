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

   @Override
   public void initGui() {
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
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.x++;
      float g = HUD.gui.getSetting("Scale").asFloat();
      RenderUtil.drawRect(0.0, 0.0, Display.getWidth(), Display.getHeight(), this.y);
      if (HUD.toolTips.asBoolean() && this.K != null) {
         if (this.K instanceof BooleanComponent) {
            BooleanComponent booleanComponent = (BooleanComponent)this.K;
            String string = EnumChatFormatting.GRAY + booleanComponent.t().getName() + ": " + EnumChatFormatting.RESET + booleanComponent.t().getDescription();
            GuiMainMenu.m.drawString(string, 3.0F, this.height - 12, -1);
         }

         if (this.K instanceof NumberSlider) {
            NumberSlider numberSlider = (NumberSlider)this.K;
            String string2 = EnumChatFormatting.GRAY + numberSlider.t().getName() + ": " + EnumChatFormatting.RESET + numberSlider.t().getDescription();
            GuiMainMenu.m.drawString(string2, 3.0F, this.height - 12, -1);
         }
      }

      f.render(mouseX, mouseY);
      h.render(mouseX, mouseY);
      mouseX = (int)(mouseX / g);
      mouseY = (int)(mouseY / g);
      GlStateManager.scale(g, g, g);
      Astolfo.INSTANCE.getDefaultPanelManager().F(mouseX, mouseY);
      Astolfo.INSTANCE.getDefaultPanelManager().p(mouseX, mouseY);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      float g = HUD.gui.getSetting("Scale").asFloat();
      if (h.G(mouseX, mouseY)) {
         h.l(mouseButton);
      }

      for (Element element : h.M_ArrayList()) {
         if (element instanceof Component && ((Component)element).G(mouseX, mouseY) && h.s_boolean()) {
            ((Component)element).U(mouseButton, mouseX, mouseY);
            break;
         }
      }

      if (f.G(mouseX, mouseY)) {
         f.l(mouseButton);
      }

      for (Element element2 : f.M_ArrayList()) {
         if (element2 instanceof Component && ((Component)element2).G(mouseX, mouseY) && f.s_boolean()) {
            ((Component)element2).U(mouseButton, mouseX, mouseY);
            break;
         }
      }

      mouseX = (int)(mouseX / g);
      mouseY = (int)(mouseY / g);

      for (Element element3 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element3 instanceof Component) {
            Component component = (Component)element3;
            if (component.G(mouseX, mouseY)) {
               if (component.c_boolean() && mouseButton == 0) {
                  component.F(true, mouseX, mouseY);
                  break;
               }

               if (component instanceof ScriptsPanel || component instanceof ConfigsPanel) {
                  continue;
               }

               component.l(mouseButton);
            }

            if (component instanceof Container) {
               Container container = (Container)component;
               if (!(container instanceof ScriptsPanel) && !(container instanceof ConfigsPanel)) {
                  for (Element element4 : container.M_ArrayList()) {
                     if (element4 instanceof Component) {
                        if (((Component)element4).G(mouseX, mouseY) && container.s_boolean()) {
                           ((Component)element4).U(mouseButton, mouseX, mouseY);
                           this.K = element4;
                           break;
                        }

                        if (element4 instanceof Container) {
                           for (Element element5 : ((Container)element4).M_ArrayList()) {
                              if (element5 instanceof Component && ((Component)element5).G(mouseX, mouseY) && container.s_boolean() && ((Container)element4).s_boolean()) {
                                 ((Component)element5).U(mouseButton, mouseX, mouseY);
                                 this.K = element5;
                                 break;
                              }

                              if (element5 instanceof Container) {
                                 for (Element element6 : ((Container)element5).M_ArrayList()) {
                                    if (element6 instanceof Component
                                       && ((Component)element6).G(mouseX, mouseY)
                                       && container.s_boolean()
                                       && ((Container)element4).s_boolean()
                                       && ((Container)element5).s_boolean()) {
                                       ((Component)element6).U(mouseButton, mouseX, mouseY);
                                       this.K = element6;
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

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      float f = HUD.gui.getSetting("Scale").asFloat();
      mouseX = (int)(mouseX / f);
      mouseY = (int)(mouseY / f);

      for (Element element : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component instanceof Container) {
               Container container = (Container)component;

               for (Element element2 : container.M_ArrayList()) {
                  if (element2 instanceof Component && element2 instanceof Container) {
                     for (Element element3 : ((Container)element2).M_ArrayList()) {
                        if (element3 instanceof Component && element3 instanceof ColorWheel) {
                           ColorWheel colorWheel = (ColorWheel)element3;
                           if (colorWheel.G(mouseX, mouseY)) {
                              colorWheel.R(mouseX, mouseY);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      Astolfo.INSTANCE.getDefaultPanelManager().Q(false);
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 54 && this.x > 5) {
         B = !B;
      }

      for (Element element : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component instanceof Container) {
               Container container = (Container)component;

               for (Element element2 : container.M_ArrayList()) {
                  if (element2 instanceof Component && element2 instanceof Container) {
                     ((Container)element2).P(typedChar, keyCode);

                     for (Element element3 : ((Container)element2).M_ArrayList()) {
                        if (element3 instanceof StringComponent) {
                           StringComponent stringComponent = (StringComponent)element3;
                           if (stringComponent.E() && container.s_boolean() && ((Container)element2).s_boolean()) {
                              stringComponent.P(typedChar, keyCode);
                              break;
                           }
                        }

                        if (element3 instanceof Container) {
                           for (Element element4 : ((Container)element3).M_ArrayList()) {
                              if (element4 instanceof StringComponent) {
                                 StringComponent stringComponent2 = (StringComponent)element4;
                                 if (stringComponent2.E() && container.s_boolean() && ((Container)element2).s_boolean() && ((Container)element3).s_boolean()) {
                                    stringComponent2.P(typedChar, keyCode);
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

      for (Element element5 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element5 instanceof Component) {
            Component component2 = (Component)element5;
            if (component2 instanceof Container) {
               Container container2 = (Container)component2;
               if (container2.s_boolean()) {
                  for (Element element6 : container2.M_ArrayList()) {
                     if (element6 instanceof bypass.gui.clickgui.transparent.ModuleButton) {
                        ((bypass.gui.clickgui.transparent.ModuleButton)element6).P(typedChar, keyCode);
                     }

                     if (element6 instanceof Container && ((Container)element6).s_boolean()) {
                        for (Element element7 : ((Container)element6).M_ArrayList()) {
                           if (element7 instanceof bypass.gui.clickgui.transparent.StringComponent && ((bypass.gui.clickgui.transparent.StringComponent)element7).k()
                              )
                            {
                              ((bypass.gui.clickgui.transparent.StringComponent)element7).P(typedChar, keyCode);
                           }

                           if (element7 instanceof bypass.gui.clickgui.transparent.ModuleButton) {
                              ((bypass.gui.clickgui.transparent.ModuleButton)element7).P(typedChar, keyCode);
                           }

                           if (element7 instanceof Container) {
                              for (Element element8 : ((Container)element7).M_ArrayList()) {
                                 if (((Container)element7).s_boolean()
                                    && element8 instanceof bypass.gui.clickgui.transparent.StringComponent
                                    && ((bypass.gui.clickgui.transparent.StringComponent)element8).k()) {
                                    ((bypass.gui.clickgui.transparent.StringComponent)element8).P(typedChar, keyCode);
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

      super.keyTyped(typedChar, keyCode);
   }

   public void S() {
      if (ScriptCommand.z) {
         ScriptCommand.z = false;
         CategoryPanel categoryPanel = this.A("Scripts");
         if (categoryPanel != null) {
            categoryPanel.M_ArrayList().clear();

            for (Module module : Astolfo.INSTANCE.getModuleManager().getModules(Category.SCRIPTS)) {
               ModuleButton moduleButton = new ModuleButton(categoryPanel, module, 103, 15);

               for (Setting setting : module.getSettings()) {
                  if (setting instanceof BooleanSetting) {
                     if (setting.getName().startsWith("Pos-") || setting.getName().equals("Hide")) {
                        continue;
                     }

                     moduleButton.M_ArrayList().add(new BooleanComponent(Category.SCRIPTS, moduleButton, setting, 99, 10));
                  }

                  if (setting instanceof NumberSetting && !setting.getName().startsWith("Pos-") && !setting.getName().equals("Hide")) {
                     moduleButton.M_ArrayList().add(new NumberSlider(Category.SCRIPTS, moduleButton, setting, 99, 10));
                  }
               }

               categoryPanel.a(moduleButton);
            }
         }
      }
   }

   private CategoryPanel A(String string) {
      for (Element element : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element instanceof CategoryPanel && element.J().equalsIgnoreCase(string)) {
            return (CategoryPanel)element;
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
