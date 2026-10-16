package bypass.account;

import bypass.Toasts;
import bypass.manager.JsonFileManager;
import bypass.util.FileUtil;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import net.minecraft.client.gui.GuiScreen;

public class AltManager extends JsonFileManager<Alt, JsonObject> {
   public static int unbannedCount;

   public AltManager(File file) {
      super(file, "alts.json");
   }

   public void loadEntry(String string, JsonObject jsonObject) {
      String string2 = new String(Base64.getDecoder().decode(jsonObject.get("password").getAsString().getBytes()));
      String string3 = jsonObject.get("display").getAsString();
      String string4 = jsonObject.has("accessToken") ? jsonObject.get("accessToken").getAsString() : null;
      String string5 = jsonObject.get("clientToken").getAsString();
      boolean bl = jsonObject.get("banned").getAsBoolean();
      Alt alt = new Alt(string, string2, string3, string5, string4);
      alt.banned = bl;
      if (jsonObject.has("banExpiry") && alt.banned) {
         long l = jsonObject.get("banExpiry").getAsLong();
         long m = alt.banExpiry - System.currentTimeMillis();
         if (m < 0L) {
            alt.banExpiry = l;
         } else {
            unbannedCount++;
            alt.banned = false;
         }
      }

      if (jsonObject.has("favorite")) {
         alt.favorite = jsonObject.get("favorite").getAsBoolean();
      }

      if (jsonObject.has("failedLogin")) {
         alt.failedLogin = jsonObject.get("failedLogin").getAsBoolean();
      }

      if (jsonObject.has("microsoft")) {
         alt.microsoft = jsonObject.get("microsoft").getAsBoolean();
      }

      if (jsonObject.has("refreshToken")) {
         alt.refreshToken = jsonObject.get("refreshToken").getAsString();
      }

      this.items.add(alt);
   }

   @Override
   public void loadDefaults() {
   }

   public void importFromFile(File file) {
      try {
         int i = this.items.size();
         ArrayList arrayList = new ArrayList(i);
         FileUtil.writeLines(file, arrayList);
         if (!((String)arrayList.get(0)).contains(":")) {
            Toasts.TOASTS.dangerMessage("Import", "Failed to import file");
            return;
         }

         for (Object object : arrayList) {
            String string = (String)object;
            String[] strings = string.split(" ");
            String string2 = string;
            if (strings.length >= 2) {
               string2 = strings[0];
            }

            String[] strings2 = string2.split(":");
            switch (strings2.length) {
               case 0:
                  break;
               case 1:
                  this.items.add(new Alt(strings2[0], ""));
                  break;
               case 2:
                  this.items.add(new Alt(strings2[0], strings2[1]));
                  break;
               default:
                  this.items.add(new Alt(strings2[0], strings2[1], strings2[2]));
            }
         }

         Toasts.TOASTS.infoMessage("Import", "Imported " + (this.items.size() - i) + " accounts and resolved " + this.removeDuplicates() + " duplicates");
      } catch (Exception exception) {
         Toasts.TOASTS.dangerMessage("Import", "Failed to import file");
      }
   }

   public void importFromClipboard() {
      try {
         int i = this.items.size();
         String string = GuiScreen.getClipboardString();
         String[] strings = string.split("\\r?\\n");

         for (String string2 : strings) {
            String[] strings2 = string2.split(" ");
            String string3 = string2;
            if (strings2.length >= 2) {
               string3 = strings2[0];
            }

            String[] strings3 = string3.split(":");
            switch (strings3.length) {
               case 0:
                  break;
               case 1:
                  this.items.add(new Alt(strings3[0], ""));
                  break;
               case 2:
                  this.items.add(new Alt(strings3[0], strings3[1]));
                  break;
               default:
                  this.items.add(new Alt(strings3[0], strings3[1], strings3[2]));
            }
         }

         Toasts.TOASTS.infoMessage("Import", "Imported " + (this.items.size() - i) + " accounts and resolved " + this.removeDuplicates() + " duplicates");
      } catch (Exception exception) {
         Toasts.TOASTS.dangerMessage("Import", "Failed to import clipboard");
      }
   }

   public int removeDuplicates() {
      int i = this.items.size();
      LinkedHashSet linkedHashSet = new LinkedHashSet<>(this.items);
      this.items.clear();
      this.items.addAll(linkedHashSet);
      return i - this.items.size();
   }

   public int removeMojang() {
      int i = this.items.size();
      this.items.removeIf(alt -> !alt.microsoft);
      return i - this.items.size();
   }

   public int removeBanned(boolean bl) {
      int i = this.items.size();
      this.items.removeIf(alt -> alt.banned && (alt.banExpiry == -1L || bl) && !alt.favorite);
      return i - this.items.size();
   }

   public int removeFailedLogins() {
      int i = this.items.size();
      this.items.removeIf(alt -> alt.failedLogin && !alt.favorite);
      return i - this.items.size();
   }

   public ArrayList<Alt> getUsableAlts() {
      ArrayList arrayList = new ArrayList();

      for (Alt alt : this.items) {
         if (!alt.banned && !alt.failedLogin && !alt.favorite) {
            arrayList.add(alt);
         }
      }

      return arrayList;
   }
}
