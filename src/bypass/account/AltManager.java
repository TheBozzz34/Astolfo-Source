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
   public static int o;

   public AltManager(File file) {
      super(file, "alts.json");
   }

   public void t(String string, JsonObject jsonObject) {
      String string2 = new String(Base64.getDecoder().decode(jsonObject.get("password").getAsString().getBytes()));
      String string3 = jsonObject.get("display").getAsString();
      String string4 = jsonObject.has("accessToken") ? jsonObject.get("accessToken").getAsString() : null;
      String string5 = jsonObject.get("clientToken").getAsString();
      boolean bl = jsonObject.get("banned").getAsBoolean();
      Alt alt = new Alt(string, string2, string3, string5, string4);
      alt.N = bl;
      if (jsonObject.has("banExpiry") && alt.N) {
         long l = jsonObject.get("banExpiry").getAsLong();
         long n = alt.m - System.currentTimeMillis();
         if (n < 0L) {
            alt.m = l;
         } else {
            o++;
            alt.N = false;
         }
      }

      if (jsonObject.has("favorite")) {
         alt.Y = jsonObject.get("favorite").getAsBoolean();
      }

      if (jsonObject.has("failedLogin")) {
         alt.I = jsonObject.get("failedLogin").getAsBoolean();
      }

      if (jsonObject.has("microsoft")) {
         alt.F = jsonObject.get("microsoft").getAsBoolean();
      }

      if (jsonObject.has("refreshToken")) {
         alt.Z = jsonObject.get("refreshToken").getAsString();
      }

      this.C.add(alt);
   }

   @Override
   public void I() {
   }

   public void r(File file) {
      try {
         int i = this.C.size();
         ArrayList arrayList = new ArrayList(i);
         FileUtil.writeLines(file, arrayList);
         if (!((String)arrayList.get(0)).contains(":")) {
            Toasts.TOASTS.k("Import", "Failed to import file");
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
                  this.C.add(new Alt(strings2[0], ""));
                  break;
               case 2:
                  this.C.add(new Alt(strings2[0], strings2[1]));
                  break;
               default:
                  this.C.add(new Alt(strings2[0], strings2[1], strings2[2]));
            }
         }

         Toasts.TOASTS.B("Import", "Imported " + (this.C.size() - i) + " accounts and resolved " + this.N() + " duplicates");
      } catch (Exception exception) {
         Toasts.TOASTS.k("Import", "Failed to import file");
      }
   }

   public void O() {
      try {
         int i = this.C.size();
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
                  this.C.add(new Alt(strings3[0], ""));
                  break;
               case 2:
                  this.C.add(new Alt(strings3[0], strings3[1]));
                  break;
               default:
                  this.C.add(new Alt(strings3[0], strings3[1], strings3[2]));
            }
         }

         Toasts.TOASTS.B("Import", "Imported " + (this.C.size() - i) + " accounts and resolved " + this.N() + " duplicates");
      } catch (Exception exception) {
         Toasts.TOASTS.k("Import", "Failed to import clipboard");
      }
   }

   public int N() {
      int i = this.C.size();
      LinkedHashSet linkedHashSet = new LinkedHashSet<>(this.C);
      this.C.clear();
      this.C.addAll(linkedHashSet);
      return i - this.C.size();
   }

   public int removeMojang() {
      int i = this.C.size();
      this.C.removeIf(alt -> !alt.F);
      return i - this.C.size();
   }

   public int removeBanned(boolean bl) {
      int i = this.C.size();
      this.C.removeIf(alt -> alt.N && (alt.m == -1L || bl) && !alt.Y);
      return i - this.C.size();
   }

   public int removeFailedLogins() {
      int i = this.C.size();
      this.C.removeIf(alt -> alt.I && !alt.Y);
      return i - this.C.size();
   }

   public ArrayList<Alt> e() {
      ArrayList arrayList = new ArrayList();

      for (Alt alt : this.C) {
         if (!alt.N && !alt.I && !alt.Y) {
            arrayList.add(alt);
         }
      }

      return arrayList;
   }
}
