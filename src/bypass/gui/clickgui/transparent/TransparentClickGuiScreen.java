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
   private static final long a = 134723671816197L;

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
   public void drawScreen(int var1, int var2, float var3) {
      long var4 = a ^ 131987001745680L;
      long var6 = var4 ^ 18435155387639L;
      float var10 = HUD.gui.getSetting("Scale").asFloat();
      var1 = (int)(var1 / var10);
      var2 = (int)(var2 / var10);
      GlStateManager.scale(var10, var10, var10);
      Astolfo.INSTANCE.getTransparentPanelManager().F(var1, var2);
      Astolfo.INSTANCE.getTransparentPanelManager().p(var1, var2);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      long var4 = a ^ 113285842310132L;
      long var6 = var4 ^ 17346045040147L;
      float var9 = HUD.gui.getSetting("Scale").asFloat();
      var1 = (int)(var1 / var9);
      var2 = (int)(var2 / var9);

      for (Element var11 : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (var11 instanceof Component) {
            Component var12 = (Component)var11;
            if (var12.G(var1, var2)) {
               if (var12.c_boolean() && var3 == 0) {
                  var12.F(true, var1, var2);
                  break;
               }

               var12.l(var3);
            }

            if (var12 instanceof Container) {
               Container var13 = (Container)var12;

               for (Element var15 : var13.M_ArrayList()) {
                  if (var15 instanceof Component) {
                     if (((Component)var15).G(var1, var2) && var13.s_boolean()) {
                        ((Component)var15).l(var3);
                        break;
                     }

                     if (var15 instanceof Container) {
                        for (Element var17 : ((Container)var15).M_ArrayList()) {
                           if (var17 instanceof Component && ((Component)var17).G(var1, var2) && var13.s_boolean() && ((Container)var15).s_boolean()) {
                              ((Component)var17).l(var3);
                              break;
                           }

                           if (var17 instanceof Container) {
                              for (Element var19 : ((Container)var17).M_ArrayList()) {
                                 if (var19 instanceof Component
                                    && ((Component)var19).G(var1, var2)
                                    && ((Container)var15).s_boolean()
                                    && ((Container)var17).s_boolean()) {
                                    ((Component)var19).l(var3);
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

      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void mouseClickMove(int var1, int var2, int var3, long var4) {
      long var6 = a ^ 123290820125484L;
      long var8 = var6 ^ 27415769278155L;
      float var11 = HUD.gui.getSetting("Scale").asFloat();
      var1 = (int)(var1 / var11);
      var2 = (int)(var2 / var11);

      for (Element var13 : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
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
      long var4 = a ^ 122205175077828L;
      long var6 = var4 ^ 8709232467491L;
      Astolfo.INSTANCE.getTransparentPanelManager().Q(false);
      super.mouseReleased(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      long var3 = a ^ 67907618721000L;
      long var5 = var3 ^ 93510628018447L;

      for (Element var9 : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (var9 instanceof Component) {
            Component var10 = (Component)var9;
            if (var10 instanceof Container) {
               Container var11 = (Container)var10;
               if (var11.s_boolean()) {
                  for (Element var13 : var11.M_ArrayList()) {
                     if (var13 instanceof ModuleButton) {
                        ((ModuleButton)var13).P(var1, var2);
                     }

                     if (var13 instanceof Container && ((Container)var13).s_boolean()) {
                        for (Element var15 : ((Container)var13).M_ArrayList()) {
                           if (var15 instanceof StringComponent && ((StringComponent)var15).k()) {
                              ((StringComponent)var15).P(var1, var2);
                           }

                           if (var15 instanceof ModuleButton) {
                              ((ModuleButton)var15).P(var1, var2);
                           }

                           if (var15 instanceof Container) {
                              for (Element var17 : ((Container)var15).M_ArrayList()) {
                                 if (((Container)var15).s_boolean() && var17 instanceof StringComponent && ((StringComponent)var17).k()) {
                                    ((StringComponent)var17).P(var1, var2);
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

   private void D() {
      long var1 = a ^ 92706501178946L;
      long var3 = var1 ^ 28178832864244L;
      if (ScriptCommand.z) {
         ScriptCommand.z = false;
         CategoryPanel var6 = z("Scripts");
         if (var6 != null) {
            var6.M_ArrayList().clear();

            for (Module var8 : Astolfo.INSTANCE.getModuleManager().getModules(Category.SCRIPTS)) {
               ModuleButton var9 = new ModuleButton(var6, var8, 101, 10);

               for (Setting var11 : var8.getSettings()) {
                  if (var11 instanceof BooleanSetting && !var11.getName().startsWith("Pos-") && !var11.getName().equals("Hide")) {
                     var9.M_ArrayList().add(new BooleanComponent(var9, var11, 101, 10));
                  }
               }

               var6.a(var9);
            }
         }
      }
   }

   public static CategoryPanel z(String var0) {
      long var1 = a ^ 94487644044546L;
      long var3 = var1 ^ 67213097482469L;

      for (Element var7 : Astolfo.INSTANCE.getTransparentPanelManager().getList()) {
         if (var7 instanceof CategoryPanel && var7.J().equalsIgnoreCase(var0)) {
            return (CategoryPanel)var7;
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
