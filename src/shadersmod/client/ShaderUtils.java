package shadersmod.client;

import net.minecraft.src.Config;

public class ShaderUtils {
   private static final long a = 108202153654131L;

   public static ShaderOption getShaderOption(String var0, ShaderOption[] var1) {
      if (var1 == null) {
         return null;
      }

      for (int var5 = 0; var5 < var1.length; var5++) {
         ShaderOption var6 = var1[var5];
         if (var6.getName().equals(var0)) {
            return var6;
         }
      }

      return null;
   }

   public static ShaderProfile detectProfile(ShaderProfile[] var0, ShaderOption[] var1, boolean var2) {
      if (var0 == null) {
         return null;
      }

      for (int var6 = 0; var6 < var0.length; var6++) {
         ShaderProfile var7 = var0[var6];
         if (matchProfile(var7, var1, var2)) {
            return var7;
         }
      }

      return null;
   }

   public static boolean matchProfile(ShaderProfile var0, ShaderOption[] var1, boolean var2) {
      if (var0 == null) {
         return false;
      }

      if (var1 == null) {
         return false;
      }

      String[] var6 = var0.getOptions();

      for (int var7 = 0; var7 < var6.length; var7++) {
         String var8 = var6[var7];
         ShaderOption var9 = getShaderOption(var8, var1);
         if (var9 != null) {
            String var10 = var2 ? var9.getValueDefault() : var9.getValue();
            String var11 = var0.getValue(var8);
            if (!Config.equals(var10, var11)) {
               return false;
            }
         }
      }

      return true;
   }
}
