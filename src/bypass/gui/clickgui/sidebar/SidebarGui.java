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
      long var1 = b ^ 2930101301327L;
      long var3 = var1 ^ 45962346738322L;
      Sidebar var6 = new Sidebar(0, 0, 34, 210);

      for (Category var10 : Category.values()) {
         if (var10 != Category.HIDDEN && var10 != Category.SCRIPTS) {
            CategoryTab var11 = new CategoryTab(var6, var10, (int)var6.M(), 25, 34, 30);
            if (var10 == Category.CONFIG) {
               var11.a(new StringComponent(null, config, 0.0F, 0.0F, 83, 14));
               var11.a(new LoadConfigButton(0.0F, 0.0F, 83, 14));
               var11.a(new SaveConfigButton(0.0F, 0.0F, 83, 14));
               var6.a(var11);
            } else {
               for (ModuleInfo var13 : Astolfo.INSTANCE.getModuleManager().getModules(var10)) {
                  if (var13 instanceof Module) {
                     Module var14 = (Module)var13;
                     ModuleButton var15 = new ModuleButton(var11, var14, 0, 0, 70, 14);

                     for (Setting var17 : var14.getSettings()) {
                        if (var17 instanceof BooleanSetting) {
                           BooleanComponent var25 = new BooleanComponent(var15, var17, 0.0F, 0.0F, 145, 12);
                           var15.a(var25);
                        } else if (var17 instanceof NumberSetting) {
                           NumberSlider var24 = new NumberSlider(var15, var17, 0.0F, 0.0F, 145, 12);
                           var15.a(var24);
                        } else if (var17 instanceof EnumSetting) {
                           EnumComponent var23 = new EnumComponent(var15, var17, 0.0F, 0.0F, 145, 12);
                           var15.a(var23);
                        } else if (var17 instanceof StringSetting) {
                           StringComponent var22 = new StringComponent(var15, var17, 0.0F, 0.0F, 145, 12);
                           var15.a(var22);
                        } else if (var17 instanceof MapSetting) {
                           MapComponent var18 = new MapComponent(var15, (MapSetting)var17, 0, 0, 145, 12);

                           for (Setting var20 : ((MapSetting)var17).getSettings().values()) {
                              if (var20 instanceof BooleanSetting) {
                                 BooleanComponent var21 = new BooleanComponent(var15, var20, 0.0F, 0.0F, 115, 12);
                                 var21.G(true);
                                 var18.a(var21);
                              } else if (var20 instanceof NumberSetting) {
                                 NumberSlider var26 = new NumberSlider(var15, var20, 0.0F, 0.0F, 95, 12);
                                 var26.G(true);
                                 var18.a(var26);
                              } else if (var20 instanceof EnumSetting) {
                                 EnumComponent var27 = new EnumComponent(var15, var20, 0.0F, 0.0F, 115, 12);
                                 var27.G(true);
                                 var18.a(var27);
                              } else if (var20 instanceof StringSetting) {
                                 StringComponent var28 = new StringComponent(var15, var20, 0.0F, 0.0F, 115, 12);
                                 var28.G(true);
                                 var18.a(var28);
                              }
                           }

                           var15.a(var18);
                        }
                     }

                     var11.a(var15);
                  }
               }

               var6.a(var11);
            }
         }
      }

      this.addElements(var6);
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

   public int y(int var1) {
      return this.e < 255 ? new Color(255, 255, 255, Math.min(this.e, var1)).getRGB() : Color.WHITE.getRGB();
   }

   public DropdownComponent A() {
      return this.j;
   }

   public void G(DropdownComponent var1) {
      this.j = var1;
   }

   public int M() {
      int var4 = Minecraft.B();
      if (var4 == 0) {
         var4 = 250;
      }

      return var4 >= 250 ? 1 : 250 / var4;
   }

   public int U() {
      float var4 = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
      return new Color(
            MathHelper.clamp_int((int)(255.0F * var4), 0, 255),
            MathHelper.clamp_int((int)(255.0F * var4), 0, 255),
            MathHelper.clamp_int((int)(255.0F * var4), 0, 255)
         )
         .getRGB();
   }

   public static void c(float var0, float var1, float var2, float var3) {
      ScaledResolution var4 = new ScaledResolution(Minecraft.getMinecraft());
      int var5 = var4.getScaleFactor();
      GL11.glScissor((int)(var0 * var5), (int)((var4.getScaledHeight() - var3) * var5), (int)((var2 - var0) * var5), (int)((var3 - var1) * var5));
   }
}
