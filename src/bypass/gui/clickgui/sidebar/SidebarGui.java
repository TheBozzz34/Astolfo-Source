package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.gui.component.ComponentManager;
import bypass.manager.Loadable;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import bypass.setting.StringSetting;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class SidebarGui extends ComponentManager implements Loadable {
   public int fadeAlpha;
   private DropdownComponent openDropdown;
   public static final StringSetting config = new StringSetting(
      "Config", "how da fuck are you SEEING THIS, look, lil nigga, ill fucking find you shitty house and burn it, dont mess with zarboss", "default"
   );
   private static int baseColor = new Color(23, 25, 31).getRGB();
   private static int panelColor = new Color(29, 31, 37).getRGB();
   private static int surfaceColor = new Color(34, 36, 43).getRGB();

   @Override
   public void load() {
      Sidebar sidebar = new Sidebar(0, 0, 34, 210);

      for (Category category : Category.values()) {
         if (category != Category.HIDDEN && category != Category.SCRIPTS) {
            CategoryTab categoryTab = new CategoryTab(sidebar, category, (int)sidebar.getX(), 25, 34, 30);
            if (category == Category.CONFIG) {
               categoryTab.addChildren(new StringComponent(null, config, 0.0F, 0.0F, 83, 14));
               categoryTab.addChildren(new LoadConfigButton(0.0F, 0.0F, 83, 14));
               categoryTab.addChildren(new SaveConfigButton(0.0F, 0.0F, 83, 14));
               sidebar.addChildren(categoryTab);
            } else {
               for (ModuleInfo moduleInfo : Astolfo.INSTANCE.getModuleManager().getModules(category)) {
                  if (moduleInfo instanceof Module) {
                     Module module = (Module)moduleInfo;
                     ModuleButton moduleButton = new ModuleButton(categoryTab, module, 0, 0, 70, 14);

                     for (Setting setting : module.getSettings()) {
                        if (setting instanceof BooleanSetting) {
                           BooleanComponent booleanComponent = new BooleanComponent(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.addChildren(booleanComponent);
                        } else if (setting instanceof NumberSetting) {
                           NumberSlider numberSlider = new NumberSlider(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.addChildren(numberSlider);
                        } else if (setting instanceof EnumSetting) {
                           EnumComponent enumComponent = new EnumComponent(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.addChildren(enumComponent);
                        } else if (setting instanceof StringSetting) {
                           StringComponent stringComponent = new StringComponent(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.addChildren(stringComponent);
                        } else if (setting instanceof MapSetting) {
                           MapComponent mapComponent = new MapComponent(moduleButton, (MapSetting)setting, 0, 0, 145, 12);

                           for (Setting setting2 : ((MapSetting)setting).getSettings().values()) {
                              if (setting2 instanceof BooleanSetting) {
                                 BooleanComponent booleanComponent2 = new BooleanComponent(moduleButton, setting2, 0.0F, 0.0F, 115, 12);
                                 booleanComponent2.setForceVisible(true);
                                 mapComponent.addChildren(booleanComponent2);
                              } else if (setting2 instanceof NumberSetting) {
                                 NumberSlider numberSlider2 = new NumberSlider(moduleButton, setting2, 0.0F, 0.0F, 95, 12);
                                 numberSlider2.setForceVisible(true);
                                 mapComponent.addChildren(numberSlider2);
                              } else if (setting2 instanceof EnumSetting) {
                                 EnumComponent enumComponent2 = new EnumComponent(moduleButton, setting2, 0.0F, 0.0F, 115, 12);
                                 enumComponent2.setForceVisible(true);
                                 mapComponent.addChildren(enumComponent2);
                              } else if (setting2 instanceof StringSetting) {
                                 StringComponent stringComponent2 = new StringComponent(moduleButton, setting2, 0.0F, 0.0F, 115, 12);
                                 stringComponent2.setForceVisible(true);
                                 mapComponent.addChildren(stringComponent2);
                              }
                           }

                           moduleButton.addChildren(mapComponent);
                        }
                     }

                     categoryTab.addChildren(moduleButton);
                  }
               }

               sidebar.addChildren(categoryTab);
            }
         }
      }

      this.addElements(sidebar);
   }

   @Override
   public void save() {
   }

   public int getBaseColor() {
      return this.fadeAlpha < 255 ? new Color(23, 25, 31, this.fadeAlpha).getRGB() : baseColor;
   }

   public int getPanelColor() {
      return this.fadeAlpha < 255 ? new Color(29, 31, 37, this.fadeAlpha).getRGB() : panelColor;
   }

   public int getSurfaceColor() {
      return this.fadeAlpha < 255 ? new Color(34, 36, 43, this.fadeAlpha).getRGB() : surfaceColor;
   }

   public int getFadedWhite(int i) {
      return this.fadeAlpha < 255 ? new Color(255, 255, 255, Math.min(this.fadeAlpha, i)).getRGB() : Color.WHITE.getRGB();
   }

   public DropdownComponent getOpenDropdown() {
      return this.openDropdown;
   }

   public void setOpenDropdown(DropdownComponent dropdownComponent) {
      this.openDropdown = dropdownComponent;
   }

   public int getAnimationStep() {
      int i = Minecraft.B();
      if (i == 0) {
         i = 250;
      }

      return i >= 250 ? 1 : 250 / i;
   }

   public int getPulseColor() {
      float f = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
      return new Color(
            MathHelper.clamp_int((int)(255.0F * f), 0, 255),
            MathHelper.clamp_int((int)(255.0F * f), 0, 255),
            MathHelper.clamp_int((int)(255.0F * f), 0, 255)
         )
         .getRGB();
   }

   public static void scissor(float f, float g, float h, float f2) {
      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      int i = scaledResolution.getScaleFactor();
      GL11.glScissor((int)(f * i), (int)((scaledResolution.getScaledHeight() - f2) * i), (int)((h - f) * i), (int)((f2 - g) * i));
   }
}
