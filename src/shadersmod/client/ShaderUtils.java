package shadersmod.client;

import net.minecraft.src.Config;

public class ShaderUtils {
   private static final long a = 108202153654131L;

   public static ShaderOption getShaderOption(String name, ShaderOption[] opts) {
      if (opts == null) {
         return null;
      }

      for (int i = 0; i < opts.length; i++) {
         ShaderOption shaderOption = opts[i];
         if (shaderOption.getName().equals(name)) {
            return shaderOption;
         }
      }

      return null;
   }

   public static ShaderProfile detectProfile(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
      if (profs == null) {
         return null;
      }

      for (int i = 0; i < profs.length; i++) {
         ShaderProfile shaderProfile = profs[i];
         if (matchProfile(shaderProfile, opts, def)) {
            return shaderProfile;
         }
      }

      return null;
   }

   public static boolean matchProfile(ShaderProfile prof, ShaderOption[] opts, boolean def) {
      if (prof == null) {
         return false;
      }

      if (opts == null) {
         return false;
      }

      String[] strings = prof.getOptions();

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         ShaderOption shaderOption = getShaderOption(string, opts);
         if (shaderOption != null) {
            String string2 = def ? shaderOption.getValueDefault() : shaderOption.getValue();
            String string3 = prof.getValue(string);
            if (!Config.equals(string2, string3)) {
               return false;
            }
         }
      }

      return true;
   }
}
