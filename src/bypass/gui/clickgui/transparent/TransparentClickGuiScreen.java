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
   private boolean e = true;
   private boolean d;

   @Override
   public void initGui() {
      if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer && this.e && !Config.isFastRender()) {
         if (this.mc.entityRenderer.theShaderGroup != null) {
            this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
         }

         this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
         this.e = false;
      }

      this.D();
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      float f = HUD.gui.getSetting("Scale").asFloat();
      mouseX = (int)(mouseX / f);
      mouseY = (int)(mouseY / f);
      GlStateManager.scale(f, f, f);
      Astolfo.INSTANCE.getTransparentPanelManager().F(mouseX, mouseY);
      Astolfo.INSTANCE.getTransparentPanelManager().p(mouseX, mouseY);
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
            if (component.G(mouseX, mouseY)) {
               if (component.c_boolean() && mouseButton == 0) {
                  component.F(true, mouseX, mouseY);
                  break;
               }

               component.l(mouseButton);
            }

            if (component instanceof Container) {
               Container container = (Container)component;

               for (Element element2 : container.M_ArrayList()) {
                  if (element2 instanceof Component) {
                     if (((Component)element2).G(mouseX, mouseY) && container.s_boolean()) {
                        ((Component)element2).l(mouseButton);
                        break;
                     }

                     if (element2 instanceof Container) {
                        for (Element element3 : ((Container)element2).M_ArrayList()) {
                           if (element3 instanceof Component && ((Component)element3).G(mouseX, mouseY) && container.s_boolean() && ((Container)element2).s_boolean()) {
                              ((Component)element3).l(mouseButton);
                              break;
                           }

                           if (element3 instanceof Container) {
                              for (Element element4 : ((Container)element3).M_ArrayList()) {
                                 if (element4 instanceof Component
                                    && ((Component)element4).G(mouseX, mouseY)
                                    && ((Container)element2).s_boolean()
                                    && ((Container)element3).s_boolean()) {
                                    ((Component)element4).l(mouseButton);
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
      Astolfo.INSTANCE.getTransparentPanelManager().Q(false);
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      for (Element element : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component instanceof Container) {
               Container container = (Container)component;
               if (container.s_boolean()) {
                  for (Element element2 : container.M_ArrayList()) {
                     if (element2 instanceof ModuleButton) {
                        ((ModuleButton)element2).P(typedChar, keyCode);
                     }

                     if (element2 instanceof Container && ((Container)element2).s_boolean()) {
                        for (Element element3 : ((Container)element2).M_ArrayList()) {
                           if (element3 instanceof StringComponent && ((StringComponent)element3).k()) {
                              ((StringComponent)element3).P(typedChar, keyCode);
                           }

                           if (element3 instanceof ModuleButton) {
                              ((ModuleButton)element3).P(typedChar, keyCode);
                           }

                           if (element3 instanceof Container) {
                              for (Element element4 : ((Container)element3).M_ArrayList()) {
                                 if (((Container)element3).s_boolean() && element4 instanceof StringComponent && ((StringComponent)element4).k()) {
                                    ((StringComponent)element4).P(typedChar, keyCode);
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

   private void D() {
      if (ScriptCommand.z) {
         ScriptCommand.z = false;
         CategoryPanel categoryPanel = z("Scripts");
         if (categoryPanel != null) {
            categoryPanel.M_ArrayList().clear();

            for (Module module : Astolfo.INSTANCE.getModuleManager().getModules(Category.SCRIPTS)) {
               ModuleButton moduleButton = new ModuleButton(categoryPanel, module, 101, 10);

               for (Setting setting : module.getSettings()) {
                  if (setting instanceof BooleanSetting && !setting.getName().startsWith("Pos-") && !setting.getName().equals("Hide")) {
                     moduleButton.M_ArrayList().add(new BooleanComponent(moduleButton, setting, 101, 10));
                  }
               }

               categoryPanel.a(moduleButton);
            }
         }
      }
   }

   public static CategoryPanel z(String string) {
      for (Element element : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (element instanceof CategoryPanel && element.J().equalsIgnoreCase(string)) {
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

      this.e = true;
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
