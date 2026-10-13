package shadersmod.client;

import net.minecraft.src.Config;
import net.minecraft.util.Util;

public class ShaderMacros {
   private static String PREFIX_MACRO = "MC_";
   public static final String j = "MC_VERSION";
   public static final String D = "MC_GL_VERSION";
   public static final String Z = "MC_GLSL_VERSION";
   public static final String O = "MC_OS_WINDOWS";
   public static final String b = "MC_OS_MAC";
   public static final String M = "MC_OS_LINUX";
   public static final String c = "MC_OS_OTHER";
   public static final String P = "MC_GL_VENDOR_ATI";
   public static final String K = "MC_GL_VENDOR_INTEL";
   public static final String A = "MC_GL_VENDOR_NVIDIA";
   public static final String e = "MC_GL_VENDOR_XORG";
   public static final String k = "MC_GL_VENDOR_OTHER";
   public static final String C = "MC_GL_RENDERER_RADEON";
   public static final String H = "MC_GL_RENDERER_GEFORCE";
   public static final String J = "MC_GL_RENDERER_QUADRO";
   public static final String v = "MC_GL_RENDERER_INTEL";
   public static final String F = "MC_GL_RENDERER_GALLIUM";
   public static final String I = "MC_GL_RENDERER_MESA";
   public static final String u = "MC_GL_RENDERER_OTHER";
   private static String[] extensionMacros;
   private static final long a = 29952932747102L;

   public static String getOs() {
      Util.EnumOS var2 = Util.getOSType();
      switch (var2) {
         case WINDOWS:
            return "MC_OS_WINDOWS";
         case OSX:
            return "MC_OS_MAC";
         case LINUX:
            return "MC_OS_LINUX";
         default:
            return "MC_OS_OTHER";
      }
   }

   public static String getVendor() {
      String var3 = Config.openGlVendor;
      if (var3 == null) {
         return "MC_GL_VENDOR_OTHER";
      }

      var3 = var3.toLowerCase();
      return var3.startsWith("ati")
         ? "MC_GL_VENDOR_ATI"
         : (
            var3.startsWith("intel")
               ? "MC_GL_VENDOR_INTEL"
               : (var3.startsWith("nvidia") ? "MC_GL_VENDOR_NVIDIA" : (var3.startsWith("x.org") ? "MC_GL_VENDOR_XORG" : "MC_GL_VENDOR_OTHER"))
         );
   }

   public static String getRenderer() {
      String var3 = Config.openGlRenderer;
      if (var3 == null) {
         return "MC_GL_RENDERER_OTHER";
      }

      var3 = var3.toLowerCase();
      return var3.startsWith("amd")
         ? "MC_GL_RENDERER_RADEON"
         : (
            var3.startsWith("ati")
               ? "MC_GL_RENDERER_RADEON"
               : (
                  var3.startsWith("radeon")
                     ? "MC_GL_RENDERER_RADEON"
                     : (
                        var3.startsWith("gallium")
                           ? "MC_GL_RENDERER_GALLIUM"
                           : (
                              var3.startsWith("intel")
                                 ? "MC_GL_RENDERER_INTEL"
                                 : (
                                    var3.startsWith("geforce")
                                       ? "MC_GL_RENDERER_GEFORCE"
                                       : (
                                          var3.startsWith("nvidia")
                                             ? "MC_GL_RENDERER_GEFORCE"
                                             : (
                                                var3.startsWith("quadro")
                                                   ? "MC_GL_RENDERER_QUADRO"
                                                   : (
                                                      var3.startsWith("nvs")
                                                         ? "MC_GL_RENDERER_QUADRO"
                                                         : (var3.startsWith("mesa") ? "MC_GL_RENDERER_MESA" : "MC_GL_RENDERER_OTHER")
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public static String getPrefixMacro() {
      return PREFIX_MACRO;
   }

   public static String[] getExtensions() {
      if (extensionMacros == null) {
         String[] var3 = Config.getOpenGlExtensions();
         String[] var4 = new String[var3.length];

         for (int var5 = 0; var5 < var3.length; var5++) {
            var4[var5] = PREFIX_MACRO + var3[var5];
         }

         extensionMacros = var4;
      }

      return extensionMacros;
   }
}
