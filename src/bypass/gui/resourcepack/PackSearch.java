package bypass.gui.resourcepack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;

public class PackSearch {
   public static final Comparator<ResourcePackListEntry> b = new PackNameAscending();
   public static final Comparator<ResourcePackListEntry> A = new PackNameDescending();
   private final List<ResourcePackListEntry> M;
   private final List<ResourcePackListEntry> J;
   private Comparator<ResourcePackListEntry> F;
   private Pattern H;
   private static final long a = 33754350313956L;

   private static String T(ResourcePackListEntry var0) {
      if (var0 instanceof CustomPackEntry) {
         return ((CustomPackEntry)var0).func_148312_b();
      } else {
         return var0 instanceof ResourcePackListEntryFound ? ((ResourcePackListEntryFound)var0).func_148318_i().getResourcePackName() : "<INVALID>";
      }
   }

   private static String c(ResourcePackListEntry var0, boolean var1) {
      String var5 = !var1 ? "a" : "z";
      String var6 = !var1 ? "b" : "z";
      String var7 = !var1 ? "z" : "a";
      if (var0 instanceof FolderPackEntry) {
         FolderPackEntry var8 = (FolderPackEntry)var0;
         return var8.Y ? var5 + var8.B : var6 + var8.B;
      } else if (var0 instanceof CustomPackEntry) {
         return var7 + ((CustomPackEntry)var0).func_148312_b();
      } else {
         return var0 instanceof ResourcePackListEntryFound
            ? var7 + ((ResourcePackListEntryFound)var0).func_148318_i().getResourcePackName()
            : var7 + "<INVALID>";
      }
   }

   private static String F(ResourcePackListEntry var0) {
      if (var0 instanceof CustomPackEntry) {
         return ((CustomPackEntry)var0).func_148311_a();
      } else {
         return var0 instanceof ResourcePackListEntryFound ? ((ResourcePackListEntryFound)var0).func_148318_i().getTexturePackDescription() : "<INVALID>";
      }
   }

   public PackSearch(List<ResourcePackListEntry> var1, List<ResourcePackListEntry> var2) {
      this.M = var1;
      this.J = var2;
      this.f();
   }

   public void E(Comparator<ResourcePackListEntry> var1) {
      this.F = var1;
      this.f();
   }

   public void R(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.H = Pattern.compile("\\Q" + var1.replace("*", "\\E.*\\Q") + "\\E", 2);
      } else {
         this.H = null;
      }

      this.f();
   }

   public void f() {
      this.J.clear();

      for (ResourcePackListEntry var5 : this.M) {
         if (this.S(T(var5)) || this.S(F(var5))) {
            this.J.add(var5);
         }
      }

      if (this.F != null) {
         Collections.sort(this.J, this.F);
      }
   }

   private boolean S(String var1) {
      return this.H == null || this.H.matcher(var1.toLowerCase(Locale.ENGLISH)).find();
   }

   public static String A(ResourcePackListEntry var0, boolean var1) {
      return c(var0, var1);
   }
}
