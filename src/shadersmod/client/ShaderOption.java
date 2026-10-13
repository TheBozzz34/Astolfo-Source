package shadersmod.client;

import java.util.Arrays;
import java.util.List;
import net.minecraft.src.Config;
import net.minecraft.src.StrUtils;

public abstract class ShaderOption {
   private String name = null;
   private String description = null;
   private String value = null;
   private String[] values = null;
   private String valueDefault = null;
   private String[] paths = null;
   private boolean enabled = true;
   private boolean visible = true;
   public static final String COLOR_GREEN = "§a";
   public static final String COLOR_RED = "§c";
   public static final String COLOR_BLUE = "§9";
   private static final long a = 65016977813255L;

   public ShaderOption(String var1, String var2, String var3, String[] var4, String var5, String var6) {
      this.name = var1;
      this.description = var2;
      this.value = var3;
      this.values = var4;
      this.valueDefault = var5;
      if (var6 != null) {
         this.paths = new String[]{var6};
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public String getDescriptionText() {
      String var3 = Config.normalize(this.description);
      var3 = StrUtils.removePrefix(var3, "//");
      return Shaders.translate("option." + this.getName() + ".comment", var3);
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public String getValue() {
      return this.value;
   }

   public boolean setValue(String var1) {
      int var5 = getIndex(var1, this.values);
      if (var5 < 0) {
         return false;
      }

      this.value = var1;
      return true;
   }

   public String getValueDefault() {
      return this.valueDefault;
   }

   public void resetValue() {
      this.value = this.valueDefault;
   }

   public void nextValue() {
      int var4 = getIndex(this.value, this.values);
      if (var4 >= 0) {
         var4 = (var4 + 1) % this.values.length;
         this.value = this.values[var4];
      }
   }

   public void B() {
      int var4 = getIndex(this.value, this.values);
      if (var4 >= 0) {
         var4 = (var4 - 1 + this.values.length) % this.values.length;
         this.value = this.values[var4];
      }
   }

   private static int getIndex(String var0, String[] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         String var6 = var1[var5];
         if (var6.equals(var0)) {
            return var5;
         }
      }

      return -1;
   }

   public String[] getPaths() {
      return this.paths;
   }

   public void addPaths(String[] var1) {
      List var5 = Arrays.asList(this.paths);

      for (int var6 = 0; var6 < var1.length; var6++) {
         String var7 = var1[var6];
         if (!var5.contains(var7)) {
            this.paths = (String[])Config.addObjectToArray(this.paths, var7);
         }
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean var1) {
      this.enabled = var1;
   }

   public boolean isChanged() {
      return !Config.equals(this.value, this.valueDefault);
   }

   public boolean V() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   public boolean Y(String var1) {
      return getIndex(var1, this.values) >= 0;
   }

   public String getNameText() {
      return Shaders.translate("option." + this.name, this.name);
   }

   public String getValueText(String var1) {
      return Shaders.translate("value." + this.name + "." + var1, var1);
   }

   public String getValueColor(String var1) {
      return "";
   }

   public boolean matchesLine(String var1) {
      return false;
   }

   public boolean checkUsed() {
      return false;
   }

   public boolean isUsedInLine(String var1) {
      return false;
   }

   public String getSourceLine() {
      return null;
   }

   public String[] getValues() {
      return (String[])this.values.clone();
   }

   @Override
   public String toString() {
      return "" + this.name + ", value: " + this.value + ", valueDefault: " + this.valueDefault + ", paths: " + Config.arrayToString(this.paths);
   }
}
