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
   private Animation fadeInAnimation = new TimedAnimation().setDuration(150).setEasing(Easing.LINEAR);
   private ScaledResolution scaledResolution;
   private int pendingScroll;
   private int ticksSinceDropdownClosed;
   private int unusedInt;

   @Override
   public void initGui() {
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.scaledResolution = new ScaledResolution(this.mc);
      if (!this.fadeInAnimation.isRunning()) {
         this.fadeInAnimation.forwards().start();
      }

      Astolfo.INSTANCE.getSidebarGui().fadeAlpha = (int)this.fadeInAnimation.getValue(0.0, 255.0);
      if (this.pendingScroll != 0) {
         for (Element element : Astolfo.INSTANCE.getSidebarGui().getList()) {
            if (element instanceof Component && element instanceof Sidebar) {
               Sidebar sidebar = (Sidebar)element;

               for (Element element2 : sidebar.getChildren()) {
                  if (element2 instanceof Container) {
                     for (Element element3 : ((Container)element2).getChildren()) {
                        if (element3 instanceof ModuleButton) {
                           ModuleButton moduleButton = (ModuleButton)element3;
                           if (sidebar.getSelectedTab() != element2) {
                              break;
                           }

                           if (moduleButton.isHovered(mouseX, mouseY)) {
                              moduleButton.onMouseScroll(mouseX, mouseY, this.pendingScroll);
                              break;
                           }

                           for (Element element4 : moduleButton.getChildren()) {
                              if (element4 instanceof SidebarSettingComponent) {
                                 SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element4;
                                 if (sidebarSettingComponent.isHovered(mouseX, mouseY)) {
                                    sidebarSettingComponent.onMouseScroll(mouseX, mouseY, this.pendingScroll);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         this.pendingScroll = 0;
      }

      Astolfo.INSTANCE.getSidebarGui().updateDragging(mouseX, mouseY);
      Astolfo.INSTANCE.getSidebarGui().renderElements(mouseX, mouseY);
      if (Astolfo.INSTANCE.getSidebarGui().getOpenDropdown() != null) {
         Astolfo.INSTANCE.getSidebarGui().getOpenDropdown().renderDropdown(mouseX, mouseY);
         this.ticksSinceDropdownClosed = 0;
      } else {
         this.ticksSinceDropdownClosed++;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      boolean bl = Astolfo.INSTANCE.getSidebarGui().getOpenDropdown() == null && this.ticksSinceDropdownClosed > 50;

      for (Element element : Astolfo.INSTANCE.getSidebarGui().getList()) {
         if (element instanceof Component) {
            Component component = (Component)element;
            if (component.isHovered(mouseX, mouseY) && component.isDraggable() && mouseButton == 0) {
               component.setDragging(true, mouseX, mouseY);
            }

            if (component instanceof Sidebar) {
               Sidebar sidebar = (Sidebar)component;

               for (Element element2 : sidebar.getChildren()) {
                  if (element2 instanceof Component) {
                     if (((Component)element2).isHovered(mouseX, mouseY) && bl) {
                        ((Component)element2).onClick(mouseButton);
                     }

                     if (element2 instanceof Container) {
                        for (Element element3 : ((Container)element2).getChildren()) {
                           if (element3 instanceof ModuleButton) {
                              ModuleButton moduleButton = (ModuleButton)element3;
                              if (sidebar.getSelectedTab() != element2) {
                                 break;
                              }

                              if (moduleButton.isHovered(mouseX, mouseY) && bl) {
                                 moduleButton.onClick(mouseButton);
                                 break;
                              }

                              for (Element element4 : moduleButton.getChildren()) {
                                 if (element4 instanceof SidebarSettingComponent) {
                                    SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element4;
                                    if (moduleButton.getCategoryTab().getSelectedModule() == moduleButton) {
                                       if (sidebarSettingComponent.isHovered(mouseX, mouseY)) {
                                          if (sidebarSettingComponent instanceof DropdownComponent && Astolfo.INSTANCE.getSidebarGui().getOpenDropdown() == sidebarSettingComponent) {
                                             sidebarSettingComponent.onClick(mouseButton);
                                          }

                                          if (bl) {
                                             sidebarSettingComponent.onClick(mouseButton);
                                          }
                                       }

                                       if (sidebarSettingComponent instanceof MapComponent) {
                                          MapComponent mapComponent = (MapComponent)sidebarSettingComponent;
                                          if (sidebarSettingComponent.getModuleButton().getSelectedMap() == mapComponent) {
                                             for (Element element5 : mapComponent.getChildren()) {
                                                if (element5 instanceof SidebarSettingComponent) {
                                                   SidebarSettingComponent sidebarSettingComponent2 = (SidebarSettingComponent)element5;
                                                   if (sidebarSettingComponent2.isHovered(mouseX, mouseY)) {
                                                      if (sidebarSettingComponent2 instanceof DropdownComponent && Astolfo.INSTANCE.getSidebarGui().getOpenDropdown() == sidebarSettingComponent2) {
                                                         sidebarSettingComponent2.onClick(mouseButton);
                                                      }

                                                      if (bl) {
                                                         sidebarSettingComponent2.onClick(mouseButton);
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
                              if (sidebar.getSelectedTab() != element2) {
                                 break;
                              }

                              if (component2.isHovered(mouseX, mouseY) && bl) {
                                 component2.onClick(mouseButton);
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

               for (Element element2 : sidebar.getChildren()) {
                  if (element2 instanceof Component && element2 instanceof Container) {
                     for (Element element3 : ((Container)element2).getChildren()) {
                        if (element3 instanceof ModuleButton) {
                           ModuleButton moduleButton = (ModuleButton)element3;
                           if (sidebar.getSelectedTab() != element2) {
                              break;
                           }

                           if (moduleButton.isListeningForBind()) {
                              moduleButton.onKeyTyped(typedChar, keyCode);
                              return;
                           }

                           for (Element element4 : moduleButton.getChildren()) {
                              if (element4 instanceof SidebarSettingComponent) {
                                 SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element4;
                                 if (moduleButton.getCategoryTab().getSelectedModule() == moduleButton) {
                                    if (sidebarSettingComponent instanceof StringComponent) {
                                       StringComponent stringComponent = (StringComponent)sidebarSettingComponent;
                                       if (stringComponent.isFocused()) {
                                          stringComponent.onKeyTyped(typedChar, keyCode);
                                       }
                                    }

                                    if (sidebarSettingComponent instanceof MapComponent) {
                                       MapComponent mapComponent = (MapComponent)sidebarSettingComponent;
                                       if (sidebarSettingComponent.getModuleButton().getSelectedMap() == mapComponent) {
                                          for (Element element5 : mapComponent.getChildren()) {
                                             if (element5 instanceof StringComponent) {
                                                StringComponent stringComponent2 = (StringComponent)element5;
                                                if (stringComponent2.isFocused()) {
                                                   stringComponent2.onKeyTyped(typedChar, keyCode);
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
                           if (sidebar.getSelectedTab() != element2) {
                              break;
                           }

                           if (stringComponent3.isFocused()) {
                              stringComponent3.onKeyTyped(typedChar, keyCode);
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

         this.pendingScroll = i * 15;
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      Astolfo.INSTANCE.getSidebarGui().resetDragging(false);
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void onGuiClosed() {
      this.fadeInAnimation.rewindAndStop();
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
