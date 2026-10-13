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
   private static final long c = 1110289341564L;

   public AltManager(File var1) {
      super(var1, "alts.json");
   }

   public void t(String var1, JsonObject var2) {
      String var7 = new String(Base64.getDecoder().decode(var2.get("password").getAsString().getBytes()));
      String var8 = var2.get("display").getAsString();
      String var9 = var2.has("accessToken") ? var2.get("accessToken").getAsString() : null;
      String var10 = var2.get("clientToken").getAsString();
      boolean var11 = var2.get("banned").getAsBoolean();
      Alt var12 = new Alt(var1, var7, var8, var10, var9);
      var12.N = var11;
      if (var2.has("banExpiry") && var12.N) {
         long var13 = var2.get("banExpiry").getAsLong();
         long var15 = var12.m - System.currentTimeMillis();
         if (var15 < 0L) {
            var12.m = var13;
         } else {
            o++;
            var12.N = false;
         }
      }

      if (var2.has("favorite")) {
         var12.Y = var2.get("favorite").getAsBoolean();
      }

      if (var2.has("failedLogin")) {
         var12.I = var2.get("failedLogin").getAsBoolean();
      }

      if (var2.has("microsoft")) {
         var12.F = var2.get("microsoft").getAsBoolean();
      }

      if (var2.has("refreshToken")) {
         var12.Z = var2.get("refreshToken").getAsString();
      }

      this.C.add(var12);
   }

   @Override
   public void I() {
   }

   public void r(File var1) {
      try {
         int var5 = this.C.size();
         ArrayList var6 = new ArrayList(var5);
         FileUtil.writeLines(var1, var6);
         if (!((String)var6.get(0)).contains(":")) {
            Toasts.TOASTS.k("Import", "Failed to import file");
            return;
         }

         for (Object var8$raw : var6) {
            String var8 = (String)var8$raw;
            String[] var9 = var8.split(" ");
            String var10 = var8;
            if (var9.length >= 2) {
               var10 = var9[0];
            }

            String[] var11 = var10.split(":");
            switch (var11.length) {
               case 0:
                  break;
               case 1:
                  this.C.add(new Alt(var11[0], ""));
                  break;
               case 2:
                  this.C.add(new Alt(var11[0], var11[1]));
                  break;
               default:
                  this.C.add(new Alt(var11[0], var11[1], var11[2]));
            }
         }

         Toasts.TOASTS.B("Import", "Imported " + (this.C.size() - var5) + " accounts and resolved " + this.N() + " duplicates");
      } catch (Exception var12) {
         Toasts.TOASTS.k("Import", "Failed to import file");
      }
   }

   public void O() {
      try {
         int var4 = this.C.size();
         String var5 = GuiScreen.getClipboardString();
         String[] var6 = var5.split("\\r?\\n");

         for (String var10 : var6) {
            String[] var11 = var10.split(" ");
            String var12 = var10;
            if (var11.length >= 2) {
               var12 = var11[0];
            }

            String[] var13 = var12.split(":");
            switch (var13.length) {
               case 0:
                  break;
               case 1:
                  this.C.add(new Alt(var13[0], ""));
                  break;
               case 2:
                  this.C.add(new Alt(var13[0], var13[1]));
                  break;
               default:
                  this.C.add(new Alt(var13[0], var13[1], var13[2]));
            }
         }

         Toasts.TOASTS.B("Import", "Imported " + (this.C.size() - var4) + " accounts and resolved " + this.N() + " duplicates");
      } catch (Exception var14) {
         Toasts.TOASTS.k("Import", "Failed to import clipboard");
      }
   }

   public int N() {
      int var1 = this.C.size();
      LinkedHashSet var2 = new LinkedHashSet<>(this.C);
      this.C.clear();
      this.C.addAll(var2);
      return var1 - this.C.size();
   }

   public int removeMojang() {
      int var1 = this.C.size();
      this.C.removeIf(var0 -> !var0.F);
      return var1 - this.C.size();
   }

   public int removeBanned(boolean var1) {
      int var2 = this.C.size();
      this.C.removeIf(var1x -> var1x.N && (var1x.m == -1L || var1) && !var1x.Y);
      return var2 - this.C.size();
   }

   public int removeFailedLogins() {
      int var1 = this.C.size();
      this.C.removeIf(var0 -> var0.I && !var0.Y);
      return var1 - this.C.size();
   }

   public ArrayList<Alt> e() {
      ArrayList var4 = new ArrayList();

      for (Alt var6 : this.C) {
         if (!var6.N && !var6.I && !var6.Y) {
            var4.add(var6);
         }
      }

      return var4;
   }
}
