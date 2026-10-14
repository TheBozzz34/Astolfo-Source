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
   public int e;
   private DropdownComponent j;
   public static final StringSetting config = new StringSetting(
      "Config", "how da fuck are you SEEING THIS, look, lil nigga, ill fucking find you shitty house and burn it, dont mess with zarboss", "default"
   );
   private static int E = new Color(23, 25, 31).getRGB();
   private static int B = new Color(29, 31, 37).getRGB();
   private static int F = new Color(34, 36, 43).getRGB();
   private static final long b = 30857278881646L;

   @Override
   public void load() {
      long l = b ^ 2930101301327L;
      long m = l ^ 45962346738322L;
      Sidebar sidebar = new Sidebar(0, 0, 34, 210);

      for (Category category : Category.values()) {
         if (category != Category.HIDDEN && category != Category.SCRIPTS) {
            CategoryTab categoryTab = new CategoryTab(sidebar, category, (int)sidebar.M(), 25, 34, 30);
            if (category == Category.CONFIG) {
               categoryTab.a(new StringComponent(null, config, 0.0F, 0.0F, 83, 14));
               categoryTab.a(new LoadConfigButton(0.0F, 0.0F, 83, 14));
               categoryTab.a(new SaveConfigButton(0.0F, 0.0F, 83, 14));
               sidebar.a(categoryTab);
            } else {
               for (ModuleInfo moduleInfo : Astolfo.INSTANCE.getModuleManager().getModules(category)) {
                  if (moduleInfo instanceof Module) {
                     Module module = (Module)moduleInfo;
                     ModuleButton moduleButton = new ModuleButton(categoryTab, module, 0, 0, 70, 14);

                     for (Setting setting : module.getSettings()) {
                        if (setting instanceof BooleanSetting) {
                           BooleanComponent booleanComponent = new BooleanComponent(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.a(booleanComponent);
                        } else if (setting instanceof NumberSetting) {
                           NumberSlider numberSlider = new NumberSlider(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.a(numberSlider);
                        } else if (setting instanceof EnumSetting) {
                           EnumComponent enumComponent = new EnumComponent(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.a(enumComponent);
                        } else if (setting instanceof StringSetting) {
                           StringComponent stringComponent = new StringComponent(moduleButton, setting, 0.0F, 0.0F, 145, 12);
                           moduleButton.a(stringComponent);
                        } else if (setting instanceof MapSetting) {
                           MapComponent mapComponent = new MapComponent(moduleButton, (MapSetting)setting, 0, 0, 145, 12);

                           for (Setting setting2 : ((MapSetting)setting).getSettings().values()) {
                              if (setting2 instanceof BooleanSetting) {
                                 BooleanComponent booleanComponent2 = new BooleanComponent(moduleButton, setting2, 0.0F, 0.0F, 115, 12);
                                 booleanComponent2.G(true);
                                 mapComponent.a(booleanComponent2);
                              } else if (setting2 instanceof NumberSetting) {
                                 NumberSlider numberSlider2 = new NumberSlider(moduleButton, setting2, 0.0F, 0.0F, 95, 12);
                                 numberSlider2.G(true);
                                 mapComponent.a(numberSlider2);
                              } else if (setting2 instanceof EnumSetting) {
                                 EnumComponent enumComponent2 = new EnumComponent(moduleButton, setting2, 0.0F, 0.0F, 115, 12);
                                 enumComponent2.G(true);
                                 mapComponent.a(enumComponent2);
                              } else if (setting2 instanceof StringSetting) {
                                 StringComponent stringComponent2 = new StringComponent(moduleButton, setting2, 0.0F, 0.0F, 115, 12);
                                 stringComponent2.G(true);
                                 mapComponent.a(stringComponent2);
                              }
                           }

                           moduleButton.a(mapComponent);
                        }
                     }

                     categoryTab.a(moduleButton);
                  }
               }

               sidebar.a(categoryTab);
            }
         }
      }

      this.addElements(sidebar);
   }

   @Override
   public void a() {
   }

   public int Z() {
      return this.e < 255 ? new Color(23, 25, 31, this.e).getRGB() : E;
   }

   public int T() {
      return this.e < 255 ? new Color(29, 31, 37, this.e).getRGB() : B;
   }

   public int F() {
      return this.e < 255 ? new Color(34, 36, 43, this.e).getRGB() : F;
   }

   public int y(int i) {
      return this.e < 255 ? new Color(255, 255, 255, Math.min(this.e, i)).getRGB() : Color.WHITE.getRGB();
   }

   public DropdownComponent A() {
      return this.j;
   }

   public void G(DropdownComponent dropdownComponent) {
      this.j = dropdownComponent;
   }

   public int M() {
      int i = Minecraft.B();
      if (i == 0) {
         i = 250;
      }

      return i >= 250 ? 1 : 250 / i;
   }

   public int U() {
      float f = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
      return new Color(
            MathHelper.clamp_int((int)(255.0F * f), 0, 255),
            MathHelper.clamp_int((int)(255.0F * f), 0, 255),
            MathHelper.clamp_int((int)(255.0F * f), 0, 255)
         )
         .getRGB();
   }

   public static void c(float f, float g, float h, float f2) {
      ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
      int i = scaledResolution.getScaleFactor();
      GL11.glScissor((int)(f * i), (int)((scaledResolution.getScaledHeight() - f2) * i), (int)((h - f) * i), (int)((f2 - g) * i));
   }
}
