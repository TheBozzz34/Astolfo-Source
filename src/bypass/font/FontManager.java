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

   public FontManager(File file) {
      super(file, "fonts.json");
   }

   public void t(String string, JsonObject jsonObject) {
      H = jsonObject.get("name").getAsString();
      o = jsonObject.get("size").getAsInt();
   }

   @Override
   public void a() {
      this.C.add(new FontEntry(H, o));
      super.a();
   }

   @Override
   public void I() {
   }

   public static void U(String string) {
      if (!string.equals("default")) {
         Font font2 = new Font(string, 0, o);
         client.setCustomFontRenderer(new CustomFontRenderer());
         client.getCustomFontRenderer().B(font2, true);
         H = string;
      } else {
         try {
            Font font3 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/SF-UI-Display-Regular.ttf")).deriveFont(18.0F);
            client.setCustomFontRenderer(new CustomFontRenderer());
            client.getCustomFontRenderer().B(font3, true);
         } catch (FontFormatException | IOException value) {
            value.printStackTrace();
         }
      }
   }

   public static void A() {
      U(H);
   }
}
