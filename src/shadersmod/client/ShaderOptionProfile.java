package shadersmod.client;

import java.util.ArrayList;
import net.minecraft.src.Lang;

public class ShaderOptionProfile extends ShaderOption {
   private ShaderProfile[] profiles = null;
   private ShaderOption[] options = null;
   private static final long b = 47258307051176L;

   public ShaderOptionProfile(ShaderProfile[] var1, ShaderOption[] var2) {
      super("<profile>", "", detectProfileName(var1, var2), getProfileNames(var1), detectProfileName(var1, var2, true), (String)null);
      this.profiles = var1;
      this.options = var2;
   }

   @Override
   public void nextValue() {
      super.nextValue();
      if (this.getValue().equals("<custom>")) {
         super.nextValue();
      }

      this.applyProfileOptions();
   }

   public void F() {
      ShaderProfile var4 = this.getProfile(this.getValue());
      if (var4 == null || !ShaderUtils.matchProfile(var4, this.options, false)) {
         String var5 = detectProfileName(this.profiles, this.options);
         this.setValue(var5);
      }
   }

   private void applyProfileOptions() {
      ShaderProfile var4 = this.getProfile(this.getValue());
      if (var4 != null) {
         String[] var5 = var4.getOptions();

         for (int var6 = 0; var6 < var5.length; var6++) {
            String var7 = var5[var6];
            ShaderOption var8 = this.getOption(var7);
            if (var8 != null) {
               String var9 = var4.getValue(var7);
               var8.setValue(var9);
            }
         }
      }
   }

   private ShaderOption getOption(String var1) {
      for (int var5 = 0; var5 < this.options.length; var5++) {
         ShaderOption var6 = this.options[var5];
         if (var6.getName().equals(var1)) {
            return var6;
         }
      }

      return null;
   }

   private ShaderProfile getProfile(String var1) {
      for (int var5 = 0; var5 < this.profiles.length; var5++) {
         ShaderProfile var6 = this.profiles[var5];
         if (var6.getName().equals(var1)) {
            return var6;
         }
      }

      return null;
   }

   @Override
   public String getNameText() {
      return Lang.get("of.shaders.profile");
   }

   @Override
   public String getValueText(String var1) {
      return var1.equals("<custom>") ? Lang.get("of.general.custom", "<custom>") : Shaders.translate("profile." + var1, var1);
   }

   @Override
   public String getValueColor(String var1) {
      return var1.equals("<custom>") ? "§c" : "§a";
   }

   private static String detectProfileName(ShaderProfile[] var0, ShaderOption[] var1) {
      return detectProfileName(var0, var1, false);
   }

   private static String detectProfileName(ShaderProfile[] var0, ShaderOption[] var1, boolean var2) {
      ShaderProfile var6 = ShaderUtils.detectProfile(var0, var1, var2);
      return var6 == null ? "<custom>" : var6.getName();
   }

   private static String[] getProfileNames(ShaderProfile[] var0) {
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var0.length; var5++) {
         ShaderProfile var6 = var0[var5];
         var4.add(var6.getName());
      }

      var4.add("<custom>");
      return (java.lang.String[])(var4.toArray(new String[var4.size()]));
   }
}
