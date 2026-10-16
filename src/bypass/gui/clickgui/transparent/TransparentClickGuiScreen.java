package bypass.gui.clickgui.transparent;

import bypass.Astolfo;
import bypass.command.ScriptCommand;
import bypass.gui.clickgui.unused.ColorWheel;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.visuals.HUD;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class TransparentClickGuiScreen extends GuiScreen {
   private boolean shouldLoadBlur = true;
   private boolean unusedFlag;

   @Override
   public void initGui() {
      if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer && this.shouldLoadBlur && !Config.isFastRender()) {
         if (this.mc.entityRenderer.theShaderGroup != null) {
            this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
         }

         this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
         this.shouldLoadBlur = false;
      }

      this.refreshScriptModules();
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      float f = HUD.gui.getSetting("Scale").asFloat();
      mouseX = (int)(mouseX / f);
      mouseY = (int)(mouseY / f);
      GlStateManager.scale(f, f, f);
      Astolfo.INSTANCE.getTransparentPanelManager().updateDragging(mouseX, mouseY);
      Astolfo.INSTANCE.getTransparentPanelManager().renderElements(mouseX, mouseY);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      float f = HUD.gui.getSetting("Scale").asFloat();
      mouseX = (int)(mouseX / f);
      mouseY = (int)(mouseY / f);

      for (Element element : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component.isHovered(mouseX, mouseY)) {
               if (component.isDraggable() && mouseButton == 0) {
                  component.setDragging(true, mouseX, mouseY);
                  break;
               }

               component.onClick(mouseButton);
            }

            if (component instanceof Container) {
               Container container = (Container)component;

               for (Element element2 : container.getChildren()) {
                  if (element2 instanceof Component) {
                     if (((Component)element2).isHovered(mouseX, mouseY) && container.isExpanded()) {
                        ((Component)element2).onClick(mouseButton);
                        break;
                     }

                     if (element2 instanceof Container) {
                        for (Element element3 : ((Container)element2).getChildren()) {
                           if (element3 instanceof Component
                              && ((Component)element3).isHovered(mouseX, mouseY)
                              && container.isExpanded()
                              && ((Container)element2).isExpanded()) {
                              ((Component)element3).onClick(mouseButton);
                              break;
                           }

                           if (element3 instanceof Container) {
                              for (Element element4 : ((Container)element3).getChildren()) {
                                 if (element4 instanceof Component
                                    && ((Component)element4).isHovered(mouseX, mouseY)
                                    && ((Container)element2).isExpanded()
                                    && ((Container)element3).isExpanded()) {
                                    ((Component)element4).onClick(mouseButton);
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

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      float f = HUD.gui.getSetting("Scale").asFloat();
      mouseX = (int)(mouseX / f);
      mouseY = (int)(mouseY / f);

      for (Element element : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
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
      Astolfo.INSTANCE.getTransparentPanelManager().resetDragging(false);
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      for (Element element : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component instanceof Container) {
               Container container = (Container)component;
               if (container.isExpanded()) {
                  for (Element element2 : container.getChildren()) {
                     if (element2 instanceof ModuleButton) {
                        ((ModuleButton)element2).onKeyTyped(typedChar, keyCode);
                     }

                     if (element2 instanceof Container && ((Container)element2).isExpanded()) {
                        for (Element element3 : ((Container)element2).getChildren()) {
                           if (element3 instanceof StringComponent && ((StringComponent)element3).isFocused()) {
                              ((StringComponent)element3).onKeyTyped(typedChar, keyCode);
                           }

                           if (element3 instanceof ModuleButton) {
                              ((ModuleButton)element3).onKeyTyped(typedChar, keyCode);
                           }

                           if (element3 instanceof Container) {
                              for (Element element4 : ((Container)element3).getChildren()) {
                                 if (((Container)element3).isExpanded() && element4 instanceof StringComponent && ((StringComponent)element4).isFocused()) {
                                    ((StringComponent)element4).onKeyTyped(typedChar, keyCode);
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

   private void refreshScriptModules() {
      if (ScriptCommand.scriptsChanged) {
         ScriptCommand.scriptsChanged = false;
         CategoryPanel categoryPanel = findCategoryPanel("Scripts");
         if (categoryPanel != null) {
            categoryPanel.getChildren().clear();

            for (Module module : Astolfo.INSTANCE.getModuleManager().getModules(Category.SCRIPTS)) {
               ModuleButton moduleButton = new ModuleButton(categoryPanel, module, 101, 10);

               for (Setting setting : module.getSettings()) {
                  if (setting instanceof BooleanSetting && !setting.getName().startsWith("Pos-") && !setting.getName().equals("Hide")) {
                     moduleButton.getChildren().add(new BooleanComponent(moduleButton, setting, 101, 10));
                  }
               }

               categoryPanel.addChildren(moduleButton);
            }
         }
      }
   }

   public static CategoryPanel findCategoryPanel(String string) {
      for (Element element : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (element instanceof CategoryPanel && element.getName().equalsIgnoreCase(string)) {
            return (CategoryPanel)element;
         }
      }

      return null;
   }

   @Override
   public void onGuiClosed() {
      if (this.mc.entityRenderer.theShaderGroup != null) {
         this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
         this.mc.entityRenderer.theShaderGroup = null;
      }

      this.shouldLoadBlur = true;
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
