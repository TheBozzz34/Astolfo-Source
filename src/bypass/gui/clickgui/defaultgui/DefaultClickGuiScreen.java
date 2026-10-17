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
   private boolean unusedFlag = true;
   private int openFrames;
   public static boolean showKeybinds;
   private static ConfigsPanel configsPanel;
   private static ScriptsPanel scriptsPanel;
   private final int backgroundColor = new Color(1, 1, 1, 140).getRGB();
   public static CopiedColor copiedColor;
   private Element lastClickedElement;

   @Override
   public void initGui() {
      if (configsPanel == null) {
         configsPanel = new ConfigsPanel();
      }

      if (scriptsPanel == null) {
         scriptsPanel = new ScriptsPanel(this);
      }

      configsPanel.setX(this.width - configsPanel.getWidth() - 0.5F);
      configsPanel.setY(this.height - configsPanel.getHeight() - 1);
      scriptsPanel.setX(this.width - configsPanel.getWidth() - 110);
      scriptsPanel.setY(this.height - configsPanel.getHeight() - 1);
      if (!Astolfo.INSTANCE.getDefaultPanelManager().getList().contains(configsPanel)) {
         Astolfo.INSTANCE.getDefaultPanelManager().add(configsPanel);
      }

      if (!Astolfo.INSTANCE.getDefaultPanelManager().getList().contains(scriptsPanel)) {
         Astolfo.INSTANCE.getDefaultPanelManager().add(scriptsPanel);
      }

      this.refreshScriptModules();
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.openFrames++;
      float f = HUD.gui.getSetting("Scale").asFloat();
      RenderUtil.drawRect(0.0, 0.0, Display.getWidth(), Display.getHeight(), this.backgroundColor);
      if (HUD.toolTips.asBoolean() && this.lastClickedElement != null) {
         if (this.lastClickedElement instanceof BooleanComponent) {
            BooleanComponent booleanComponent = (BooleanComponent)this.lastClickedElement;
            String string = EnumChatFormatting.GRAY + booleanComponent.getSetting().getName() + ": " + EnumChatFormatting.RESET + booleanComponent.getSetting().getDescription();
            GuiMainMenu.latoBoldFont15.drawString(string, 3.0F, this.height - 12, -1);
         }

         if (this.lastClickedElement instanceof NumberSlider) {
            NumberSlider numberSlider = (NumberSlider)this.lastClickedElement;
            String string2 = EnumChatFormatting.GRAY + numberSlider.getSetting().getName() + ": " + EnumChatFormatting.RESET + numberSlider.getSetting().getDescription();
            GuiMainMenu.latoBoldFont15.drawString(string2, 3.0F, this.height - 12, -1);
         }
      }

      configsPanel.render(mouseX, mouseY);
      scriptsPanel.render(mouseX, mouseY);
      mouseX = (int)(mouseX / f);
      mouseY = (int)(mouseY / f);
      GlStateManager.scale(f, f, f);
      Astolfo.INSTANCE.getDefaultPanelManager().updateDragging(mouseX, mouseY);
      Astolfo.INSTANCE.getDefaultPanelManager().renderElements(mouseX, mouseY);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      float f = HUD.gui.getSetting("Scale").asFloat();
      if (scriptsPanel.isHovered(mouseX, mouseY)) {
         scriptsPanel.onClick(mouseButton);
      }

      for (Element element : scriptsPanel.getChildren()) {
         if (element instanceof Component && ((Component)element).isHovered(mouseX, mouseY) && scriptsPanel.isExpanded()) {
            ((Component)element).onMouseClicked(mouseButton, mouseX, mouseY);
            break;
         }
      }

      if (configsPanel.isHovered(mouseX, mouseY)) {
         configsPanel.onClick(mouseButton);
      }

      for (Element element2 : configsPanel.getChildren()) {
         if (element2 instanceof Component && ((Component)element2).isHovered(mouseX, mouseY) && configsPanel.isExpanded()) {
            ((Component)element2).onMouseClicked(mouseButton, mouseX, mouseY);
            break;
         }
      }

      mouseX = (int)(mouseX / f);
      mouseY = (int)(mouseY / f);

      for (Element element3 : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element3 instanceof Component) {
            Component component = (Component)element3;
            if (component.isHovered(mouseX, mouseY)) {
               if (component.isDraggable() && mouseButton == 0) {
                  component.setDragging(true, mouseX, mouseY);
                  break;
               }

               if (component instanceof ScriptsPanel || component instanceof ConfigsPanel) {
                  continue;
               }

               component.onClick(mouseButton);
            }

            if (component instanceof Container) {
               Container container = (Container)component;
               if (!(container instanceof ScriptsPanel) && !(container instanceof ConfigsPanel)) {
                  for (Element element4 : container.getChildren()) {
                     if (element4 instanceof Component) {
                        if (((Component)element4).isHovered(mouseX, mouseY) && container.isExpanded()) {
                           ((Component)element4).onMouseClicked(mouseButton, mouseX, mouseY);
                           this.lastClickedElement = element4;
                           break;
                        }

                        if (element4 instanceof Container) {
                           for (Element element5 : ((Container)element4).getChildren()) {
                              if (element5 instanceof Component
                                 && ((Component)element5).isHovered(mouseX, mouseY)
                                 && container.isExpanded()
                                 && ((Container)element4).isExpanded()) {
                                 ((Component)element5).onMouseClicked(mouseButton, mouseX, mouseY);
                                 this.lastClickedElement = element5;
                                 break;
                              }

                              if (element5 instanceof Container) {
                                 for (Element element6 : ((Container)element5).getChildren()) {
                                    if (element6 instanceof Component
                                       && ((Component)element6).isHovered(mouseX, mouseY)
                                       && container.isExpanded()
                                       && ((Container)element4).isExpanded()
                                       && ((Container)element5).isExpanded()) {
                                       ((Component)element6).onMouseClicked(mouseButton, mouseX, mouseY);
                                       this.lastClickedElement = element6;
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

               for (Element element2 : container.getChildren()) {
                  if (element2 instanceof Component && element2 instanceof Container) {
                     for (Element element3 : ((Container)element2).getChildren()) {
                        if (element3 instanceof Component && element3 instanceof ColorWheel) {
                           ColorWheel colorWheel = (ColorWheel)element3;
                           if (colorWheel.isHovered(mouseX, mouseY)) {
                              colorWheel.updateHueFromMouse(mouseX, mouseY);
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
      Astolfo.INSTANCE.getDefaultPanelManager().resetDragging(false);
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 54 && this.openFrames > 5) {
         showKeybinds = !showKeybinds;
      }

      for (Element element : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component instanceof Container) {
               Container container = (Container)component;

               for (Element element2 : container.getChildren()) {
                  if (element2 instanceof Component && element2 instanceof Container) {
                     ((Container)element2).onKeyTyped(typedChar, keyCode);

                     for (Element element3 : ((Container)element2).getChildren()) {
                        if (element3 instanceof StringComponent) {
                           StringComponent stringComponent = (StringComponent)element3;
                           if (stringComponent.isFocused() && container.isExpanded() && ((Container)element2).isExpanded()) {
                              stringComponent.onKeyTyped(typedChar, keyCode);
                              break;
                           }
                        }

                        if (element3 instanceof Container) {
                           for (Element element4 : ((Container)element3).getChildren()) {
                              if (element4 instanceof StringComponent) {
                                 StringComponent stringComponent2 = (StringComponent)element4;
                                 if (stringComponent2.isFocused() && container.isExpanded() && ((Container)element2).isExpanded() && ((Container)element3).isExpanded()) {
                                    stringComponent2.onKeyTyped(typedChar, keyCode);
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
               if (container2.isExpanded()) {
                  for (Element element6 : container2.getChildren()) {
                     if (element6 instanceof bypass.gui.clickgui.transparent.ModuleButton) {
                        ((bypass.gui.clickgui.transparent.ModuleButton)element6).onKeyTyped(typedChar, keyCode);
                     }

                     if (element6 instanceof Container && ((Container)element6).isExpanded()) {
                        for (Element element7 : ((Container)element6).getChildren()) {
                           if (element7 instanceof bypass.gui.clickgui.transparent.StringComponent
                              && ((bypass.gui.clickgui.transparent.StringComponent)element7).isFocused()) {
                              ((bypass.gui.clickgui.transparent.StringComponent)element7).onKeyTyped(typedChar, keyCode);
                           }

                           if (element7 instanceof bypass.gui.clickgui.transparent.ModuleButton) {
                              ((bypass.gui.clickgui.transparent.ModuleButton)element7).onKeyTyped(typedChar, keyCode);
                           }

                           if (element7 instanceof Container) {
                              for (Element element8 : ((Container)element7).getChildren()) {
                                 if (((Container)element7).isExpanded()
                                    && element8 instanceof bypass.gui.clickgui.transparent.StringComponent
                                    && ((bypass.gui.clickgui.transparent.StringComponent)element8).isFocused()) {
                                    ((bypass.gui.clickgui.transparent.StringComponent)element8).onKeyTyped(typedChar, keyCode);
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

   public void refreshScriptModules() {
      if (ScriptCommand.scriptsChanged) {
         ScriptCommand.scriptsChanged = false;
         CategoryPanel categoryPanel = this.findCategoryPanel("Scripts");
         if (categoryPanel != null) {
            categoryPanel.getChildren().clear();

            for (Module module : Astolfo.INSTANCE.getModuleManager().getModules(Category.SCRIPTS)) {
               ModuleButton moduleButton = new ModuleButton(categoryPanel, module, 103, 15);

               for (Setting setting : module.getSettings()) {
                  if (setting instanceof BooleanSetting) {
                     if (setting.getName().startsWith("Pos-") || setting.getName().equals("Hide")) {
                        continue;
                     }

                     moduleButton.getChildren().add(new BooleanComponent(Category.SCRIPTS, moduleButton, setting, 99, 10));
                  }

                  if (setting instanceof NumberSetting && !setting.getName().startsWith("Pos-") && !setting.getName().equals("Hide")) {
                     moduleButton.getChildren().add(new NumberSlider(Category.SCRIPTS, moduleButton, setting, 99, 10));
                  }
               }

               categoryPanel.addChildren(moduleButton);
            }
         }
      }
   }

   private CategoryPanel findCategoryPanel(String string) {
      for (Element element : Astolfo.INSTANCE.getDefaultPanelManager().getList()) {
         if (element instanceof CategoryPanel && element.getName().equalsIgnoreCase(string)) {
            return (CategoryPanel)element;
         }
      }

      return null;
   }

   @Override
   public void onGuiClosed() {
      this.openFrames = 0;
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
