package shadersmod.client;

import java.util.ArrayList;
import net.minecraft.src.Lang;

public class ShaderOptionProfile extends ShaderOption {
   private ShaderProfile[] profiles = null;
   private ShaderOption[] options = null;

   public ShaderOptionProfile(ShaderProfile[] profiles, ShaderOption[] options) {
      super("<profile>", "", detectProfileName(profiles, options), getProfileNames(profiles), detectProfileName(profiles, options, true), (String)null);
      this.profiles = profiles;
      this.options = options;
   }

   @Override
   public void nextValue() {
      super.nextValue();
      if (this.getValue().equals("<custom>")) {
         super.nextValue();
      }

      this.applyProfileOptions();
   }

   public void updateProfile() {
      ShaderProfile shaderProfile = this.getProfile(this.getValue());
      if (shaderProfile == null || !ShaderUtils.matchProfile(shaderProfile, this.options, false)) {
         String string = detectProfileName(this.profiles, this.options);
         this.setValue(string);
      }
   }

   private void applyProfileOptions() {
      ShaderProfile shaderProfile = this.getProfile(this.getValue());
      if (shaderProfile != null) {
         String[] strings = shaderProfile.getOptions();

         for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            ShaderOption shaderOption = this.getOption(string);
            if (shaderOption != null) {
               String string2 = shaderProfile.getValue(string);
               shaderOption.setValue(string2);
            }
         }
      }
   }

   private ShaderOption getOption(String name) {
      for (int i = 0; i < this.options.length; i++) {
         ShaderOption shaderOption = this.options[i];
         if (shaderOption.getName().equals(name)) {
            return shaderOption;
         }
      }

      return null;
   }

   private ShaderProfile getProfile(String name) {
      for (int i = 0; i < this.profiles.length; i++) {
         ShaderProfile shaderProfile = this.profiles[i];
         if (shaderProfile.getName().equals(name)) {
            return shaderProfile;
         }
      }

      return null;
   }

   @Override
   public String getNameText() {
      return Lang.get("of.shaders.profile");
   }

   @Override
   public String getValueText(String val) {
      return val.equals("<custom>") ? Lang.get("of.general.custom", "<custom>") : Shaders.translate("profile." + val, val);
   }

   @Override
   public String getValueColor(String val) {
      return val.equals("<custom>") ? "§c" : "§a";
   }

   private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts) {
      return detectProfileName(profs, opts, false);
   }

   private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
      ShaderProfile shaderProfile = ShaderUtils.detectProfile(profs, opts, def);
      return shaderProfile == null ? "<custom>" : shaderProfile.getName();
   }

   private static String[] getProfileNames(ShaderProfile[] profs) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < profs.length; i++) {
         ShaderProfile shaderProfile = profs[i];
         arrayList.add(shaderProfile.getName());
      }

      arrayList.add("<custom>");
      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }
}
