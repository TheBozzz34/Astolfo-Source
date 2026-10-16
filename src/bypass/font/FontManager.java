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
   public static String fontName = "default";
   public static int fontSize = 18;

   public FontManager(File file) {
      super(file, "fonts.json");
   }

   public void loadEntry(String string, JsonObject jsonObject) {
      fontName = jsonObject.get("name").getAsString();
      fontSize = jsonObject.get("size").getAsInt();
   }

   @Override
   public void save() {
      this.items.add(new FontEntry(fontName, fontSize));
      super.save();
   }

   @Override
   public void loadDefaults() {
   }

   public static void applyFont(String string) {
      if (!string.equals("default")) {
         Font font2 = new Font(string, 0, fontSize);
         client.setCustomFontRenderer(new CustomFontRenderer());
         client.getCustomFontRenderer().loadFont(font2, true);
         fontName = string;
      } else {
         try {
            Font font3 = Font.createFont(0, GuiMainMenu.class.getResourceAsStream("/assets/minecraft/client/font/SF-UI-Display-Regular.ttf")).deriveFont(18.0F);
            client.setCustomFontRenderer(new CustomFontRenderer());
            client.getCustomFontRenderer().loadFont(font3, true);
         } catch (FontFormatException | IOException value) {
            value.printStackTrace();
         }
      }
   }

   public static void reloadFont() {
      applyFont(fontName);
   }
}
