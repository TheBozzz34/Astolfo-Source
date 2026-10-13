package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class SidebarClickGuiScreen extends GuiScreen {
   private Animation b = new TimedAnimation().setDuration(150).setEasing(Easing.LINEAR);
   private ScaledResolution M;
   private int m;
   private int R;
   private int d;
   private static final long a = 8381060872897L;

   @Override
   public void initGui() {
      super.initGui();
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      long var4 = a ^ 5575083860948L;
      long var6 = var4 ^ 32623373855641L;
      this.M = new ScaledResolution(this.mc);
      if (!this.b.isRunning()) {
         this.b.forwards().start();
      }

      Astolfo.INSTANCE.getSidebarGui().e = (int)this.b.getValue(0.0, 255.0);
      if (this.m != 0) {
         for (Element var11 : Astolfo.INSTANCE.getSidebarGui().getList()) {
            if (var11 instanceof Component && var11 instanceof Sidebar) {
               Sidebar var12 = (Sidebar)var11;

               for (Element var14 : var12.M_ArrayList()) {
                  if (var14 instanceof Container) {
                     for (Element var16 : ((Container)var14).M_ArrayList()) {
                        if (var16 instanceof ModuleButton) {
                           ModuleButton var17 = (ModuleButton)var16;
                           if (var12.z() != var14) {
                              break;
                           }

                           if (var17.G(var1, var2)) {
                              var17.W(var1, var2, this.m);
                              break;
                           }

                           for (Element var19 : var17.M_ArrayList()) {
                              if (var19 instanceof SidebarSettingComponent) {
                                 SidebarSettingComponent var20 = (SidebarSettingComponent)var19;
                                 if (var20.G(var1, var2)) {
                                    var20.W(var1, var2, this.m);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         this.m = 0;
      }

      Astolfo.INSTANCE.getSidebarGui().F(var1, var2);
      Astolfo.INSTANCE.getSidebarGui().p(var1, var2);
      if (Astolfo.INSTANCE.getSidebarGui().A() != null) {
         Astolfo.INSTANCE.getSidebarGui().A().M(var1, var2);
         this.R = 0;
      } else {
         this.R++;
      }

      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      long var4 = a ^ 28660197069104L;
      long var6 = var4 ^ 2926158616957L;
      boolean var9 = Astolfo.INSTANCE.getSidebarGui().A() == null && this.R > 50;

      for (Element var11 : Astolfo.INSTANCE.getSidebarGui().getList()) {
         if (var11 instanceof Component) {
            Component var12 = (Component)var11;
            if (var12.G(var1, var2) && var12.c_boolean() && var3 == 0) {
               var12.F(true, var1, var2);
            }

            if (var12 instanceof Sidebar) {
               Sidebar var13 = (Sidebar)var12;

               for (Element var15 : var13.M_ArrayList()) {
                  if (var15 instanceof Component) {
                     if (((Component)var15).G(var1, var2) && var9) {
                        ((Component)var15).l(var3);
                     }

                     if (var15 instanceof Container) {
                        for (Element var17 : ((Container)var15).M_ArrayList()) {
                           if (var17 instanceof ModuleButton) {
                              ModuleButton var26 = (ModuleButton)var17;
                              if (var13.z() != var15) {
                                 break;
                              }

                              if (var26.G(var1, var2) && var9) {
                                 var26.l(var3);
                                 break;
                              }

                              for (Element var20 : var26.M_ArrayList()) {
                                 if (var20 instanceof SidebarSettingComponent) {
                                    SidebarSettingComponent var21 = (SidebarSettingComponent)var20;
                                    if (var26.n().D() == var26) {
                                       if (var21.G(var1, var2)) {
                                          if (var21 instanceof DropdownComponent && Astolfo.INSTANCE.getSidebarGui().A() == var21) {
                                             var21.l(var3);
                                          }

                                          if (var9) {
                                             var21.l(var3);
                                          }
                                       }

                                       if (var21 instanceof MapComponent) {
                                          MapComponent var22 = (MapComponent)var21;
                                          if (var21.G().A() == var22) {
                                             for (Element var24 : var22.M_ArrayList()) {
                                                if (var24 instanceof SidebarSettingComponent) {
                                                   SidebarSettingComponent var25 = (SidebarSettingComponent)var24;
                                                   if (var25.G(var1, var2)) {
                                                      if (var25 instanceof DropdownComponent && Astolfo.INSTANCE.getSidebarGui().A() == var25) {
                                                         var25.l(var3);
                                                      }

                                                      if (var9) {
                                                         var25.l(var3);
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           } else if (var17 instanceof Component) {
                              Component var18 = (Component)var17;
                              if (var13.z() != var15) {
                                 break;
                              }

                              if (var18.G(var1, var2) && var9) {
                                 var18.l(var3);
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

      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      long var3 = a ^ 71306855042604L;
      long var5 = var3 ^ 97185978599009L;

      for (Element var9 : Astolfo.INSTANCE.getSidebarGui().getList()) {
         if (var9 instanceof Component) {
            Component var10 = (Component)var9;
            if (var10 instanceof Sidebar) {
               Sidebar var11 = (Sidebar)var10;

               for (Element var13 : var11.M_ArrayList()) {
                  if (var13 instanceof Component && var13 instanceof Container) {
                     for (Element var15 : ((Container)var13).M_ArrayList()) {
                        if (var15 instanceof ModuleButton) {
                           ModuleButton var24 = (ModuleButton)var15;
                           if (var11.z() != var13) {
                              break;
                           }

                           if (var24.O_boolean()) {
                              var24.P(var1, var2);
                              return;
                           }

                           for (Element var18 : var24.M_ArrayList()) {
                              if (var18 instanceof SidebarSettingComponent) {
                                 SidebarSettingComponent var19 = (SidebarSettingComponent)var18;
                                 if (var24.n().D() == var24) {
                                    if (var19 instanceof StringComponent) {
                                       StringComponent var20 = (StringComponent)var19;
                                       if (var20.q()) {
                                          var20.P(var1, var2);
                                       }
                                    }

                                    if (var19 instanceof MapComponent) {
                                       MapComponent var25 = (MapComponent)var19;
                                       if (var19.G().A() == var25) {
                                          for (Element var22 : var25.M_ArrayList()) {
                                             if (var22 instanceof StringComponent) {
                                                StringComponent var23 = (StringComponent)var22;
                                                if (var23.q()) {
                                                   var23.P(var1, var2);
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        } else if (var15 instanceof SidebarSettingComponent && var15 instanceof StringComponent) {
                           StringComponent var16 = (StringComponent)var15;
                           if (var11.z() != var13) {
                              break;
                           }

                           if (var16.q()) {
                              var16.P(var1, var2);
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

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int var4 = Mouse.getEventDWheel();
      if (var4 != 0) {
         if (var4 > 1) {
            var4 = 1;
         }

         if (var4 < -1) {
            var4 = -1;
         }

         this.m = var4 * 15;
      }
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      long var4 = a ^ 20055596390656L;
      long var6 = var4 ^ 11563038298445L;
      Astolfo.INSTANCE.getSidebarGui().Q(false);
      super.mouseReleased(var1, var2, var3);
   }

   @Override
   public void onGuiClosed() {
      this.b.rewindAndStop();
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
