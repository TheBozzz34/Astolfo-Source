package bypass.font;

import bypass.manager.JsonFileManager;
import bypass.render.font.CustomFontRenderer;
import com.google.gson.JsonObject;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import net.minecraft.client.gui.GuiMainMenu;

public class FontManager extends JsonFileManager<FontEntry, JsonObject> {
   public static String H = "default";
   public static int o = 18;
   private static final long c = 39696472344200L;

   public FontManager(File var1) {
      super(var1, "fonts.json");
   }

   public void t(String var1, JsonObject var2) {
      H = var2.get("name").getAsString();
      o = var2.get("size").getAsInt();
   }

   @Override
   public void a() {
      this.C.add(new FontEntry(H, o));
      super.a();
   }

   @Override
   public void I() {
   }

   public static void U(String var0) {
      if (!var0.equals("default")) {
         Font var4 = new Font(var0, 0, o);
         client.setCustomFontRenderer(new CustomFontRenderer());
         client.getCustomFontRenderer().B(var4, true);
         H = var0;
      } else {
         try {
            Font var7 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/SF-UI-Display-Regular.ttf")).deriveFont(18.0F);
            client.setCustomFontRenderer(new CustomFontRenderer());
            client.getCustomFontRenderer().B(var7, true);
         } catch (FontFormatException | IOException var6) {
            var6.printStackTrace();
         }
      }
   }

   public static void A() {
      U(H);
   }
}
