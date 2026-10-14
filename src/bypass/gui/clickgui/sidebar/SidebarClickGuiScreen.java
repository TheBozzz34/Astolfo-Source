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

   @Override
   public void initGui() {
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.M = new ScaledResolution(this.mc);
      if (!this.b.isRunning()) {
         this.b.forwards().start();
      }

      Astolfo.INSTANCE.getSidebarGui().e = (int)this.b.getValue(0.0, 255.0);
      if (this.m != 0) {
         for (Element element : Astolfo.INSTANCE.getSidebarGui().getList()) {
            if (element instanceof Component && element instanceof Sidebar) {
               Sidebar sidebar = (Sidebar)element;

               for (Element element2 : sidebar.M_ArrayList()) {
                  if (element2 instanceof Container) {
                     for (Element element3 : ((Container)element2).M_ArrayList()) {
                        if (element3 instanceof ModuleButton) {
                           ModuleButton moduleButton = (ModuleButton)element3;
                           if (sidebar.z() != element2) {
                              break;
                           }

                           if (moduleButton.G(mouseX, mouseY)) {
                              moduleButton.W(mouseX, mouseY, this.m);
                              break;
                           }

                           for (Element element4 : moduleButton.M_ArrayList()) {
                              if (element4 instanceof SidebarSettingComponent) {
                                 SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element4;
                                 if (sidebarSettingComponent.G(mouseX, mouseY)) {
                                    sidebarSettingComponent.W(mouseX, mouseY, this.m);
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

      Astolfo.INSTANCE.getSidebarGui().F(mouseX, mouseY);
      Astolfo.INSTANCE.getSidebarGui().p(mouseX, mouseY);
      if (Astolfo.INSTANCE.getSidebarGui().A() != null) {
         Astolfo.INSTANCE.getSidebarGui().A().M(mouseX, mouseY);
         this.R = 0;
      } else {
         this.R++;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      boolean bl = Astolfo.INSTANCE.getSidebarGui().A() == null && this.R > 50;

      for (Element element : Astolfo.INSTANCE.getSidebarGui().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component.G(mouseX, mouseY) && component.c_boolean() && mouseButton == 0) {
               component.F(true, mouseX, mouseY);
            }

            if (component instanceof Sidebar) {
               Sidebar sidebar = (Sidebar)component;

               for (Element element2 : sidebar.M_ArrayList()) {
                  if (element2 instanceof Component) {
                     if (((Component)element2).G(mouseX, mouseY) && bl) {
                        ((Component)element2).l(mouseButton);
                     }

                     if (element2 instanceof Container) {
                        for (Element element3 : ((Container)element2).M_ArrayList()) {
                           if (element3 instanceof ModuleButton) {
                              ModuleButton moduleButton = (ModuleButton)element3;
                              if (sidebar.z() != element2) {
                                 break;
                              }

                              if (moduleButton.G(mouseX, mouseY) && bl) {
                                 moduleButton.l(mouseButton);
                                 break;
                              }

                              for (Element element4 : moduleButton.M_ArrayList()) {
                                 if (element4 instanceof SidebarSettingComponent) {
                                    SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element4;
                                    if (moduleButton.n().D() == moduleButton) {
                                       if (sidebarSettingComponent.G(mouseX, mouseY)) {
                                          if (sidebarSettingComponent instanceof DropdownComponent && Astolfo.INSTANCE.getSidebarGui().A() == sidebarSettingComponent) {
                                             sidebarSettingComponent.l(mouseButton);
                                          }

                                          if (bl) {
                                             sidebarSettingComponent.l(mouseButton);
                                          }
                                       }

                                       if (sidebarSettingComponent instanceof MapComponent) {
                                          MapComponent mapComponent = (MapComponent)sidebarSettingComponent;
                                          if (sidebarSettingComponent.G().A() == mapComponent) {
                                             for (Element element5 : mapComponent.M_ArrayList()) {
                                                if (element5 instanceof SidebarSettingComponent) {
                                                   SidebarSettingComponent sidebarSettingComponent2 = (SidebarSettingComponent)element5;
                                                   if (sidebarSettingComponent2.G(mouseX, mouseY)) {
                                                      if (sidebarSettingComponent2 instanceof DropdownComponent && Astolfo.INSTANCE.getSidebarGui().A() == sidebarSettingComponent2) {
                                                         sidebarSettingComponent2.l(mouseButton);
                                                      }

                                                      if (bl) {
                                                         sidebarSettingComponent2.l(mouseButton);
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           } else if (element3 instanceof Component) {
                              Component component2 = (Component)element3;
                              if (sidebar.z() != element2) {
                                 break;
                              }

                              if (component2.G(mouseX, mouseY) && bl) {
                                 component2.l(mouseButton);
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

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      for (Element element : Astolfo.INSTANCE.getSidebarGui().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component instanceof Sidebar) {
               Sidebar sidebar = (Sidebar)component;

               for (Element element2 : sidebar.M_ArrayList()) {
                  if (element2 instanceof Component && element2 instanceof Container) {
                     for (Element element3 : ((Container)element2).M_ArrayList()) {
                        if (element3 instanceof ModuleButton) {
                           ModuleButton moduleButton = (ModuleButton)element3;
                           if (sidebar.z() != element2) {
                              break;
                           }

                           if (moduleButton.O_boolean()) {
                              moduleButton.P(typedChar, keyCode);
                              return;
                           }

                           for (Element element4 : moduleButton.M_ArrayList()) {
                              if (element4 instanceof SidebarSettingComponent) {
                                 SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element4;
                                 if (moduleButton.n().D() == moduleButton) {
                                    if (sidebarSettingComponent instanceof StringComponent) {
                                       StringComponent stringComponent = (StringComponent)sidebarSettingComponent;
                                       if (stringComponent.q()) {
                                          stringComponent.P(typedChar, keyCode);
                                       }
                                    }

                                    if (sidebarSettingComponent instanceof MapComponent) {
                                       MapComponent mapComponent = (MapComponent)sidebarSettingComponent;
                                       if (sidebarSettingComponent.G().A() == mapComponent) {
                                          for (Element element5 : mapComponent.M_ArrayList()) {
                                             if (element5 instanceof StringComponent) {
                                                StringComponent stringComponent2 = (StringComponent)element5;
                                                if (stringComponent2.q()) {
                                                   stringComponent2.P(typedChar, keyCode);
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        } else if (element3 instanceof SidebarSettingComponent && element3 instanceof StringComponent) {
                           StringComponent stringComponent3 = (StringComponent)element3;
                           if (sidebar.z() != element2) {
                              break;
                           }

                           if (stringComponent3.q()) {
                              stringComponent3.P(typedChar, keyCode);
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

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int i = Mouse.getEventDWheel();
      if (i != 0) {
         if (i > 1) {
            i = 1;
         }

         if (i < -1) {
            i = -1;
         }

         this.m = i * 15;
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      Astolfo.INSTANCE.getSidebarGui().Q(false);
      super.mouseReleased(mouseX, mouseY, state);
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
