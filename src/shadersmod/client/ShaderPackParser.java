package shadersmod.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.minecraft.src.StrUtils;

public class ShaderPackParser {
   private static final Pattern PATTERN_VERSION = Pattern.compile("^\\s*#version\\s+.*$");
   private static final Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
   private static final Set<String> setConstNames = makeSetConstNames();
   private static final long a = 136516725413655L;
   private static final long e = -4641543269564547062L;

   public static ShaderOption[] parseShaderPackOptions(IShaderPack var0, String[] var1, List<Integer> var2) {
      if (var0 == null) {
         return new ShaderOption[0];
      }

      HashMap var6 = new HashMap();
      collectShaderOptions(var0, "/shaders", var1, var6);

      for (int var8 : var2) {
         String var9 = "/shaders/world" + var8;
         collectShaderOptions(var0, var9, var1, var6);
      }

      Collection var11 = var6.values();
      ShaderOption[] var12 = (shadersmod.client.ShaderOption[])(var11.toArray(new ShaderOption[var11.size()]));
      Comparator var10 = new Comparator<ShaderOption>() {
         public int compare(ShaderOption var1, ShaderOption var2x) {
            return var1.getName().compareToIgnoreCase(var2x.getName());
         }
      };
      Arrays.sort(var12, var10);
      return var12;
   }

   private static void collectShaderOptions(IShaderPack var0, String var1, String[] var2, Map<String, ShaderOption> var3) {
      for (int var7 = 0; var7 < var2.length; var7++) {
         String var8 = var2[var7];
         if (!var8.equals("")) {
            String var9 = var1 + "/" + var8 + ".vsh";
            String var10 = var1 + "/" + var8 + ".fsh";
            collectShaderOptions(var0, var9, var3);
            collectShaderOptions(var0, var10, var3);
         }
      }
   }

   private static void collectShaderOptions(IShaderPack var0, String var1, Map<String, ShaderOption> var2) {
      String[] var6 = getLines(var0, var1);

      for (int var7 = 0; var7 < var6.length; var7++) {
         String var8 = var6[var7];
         ShaderOption var9 = getShaderOption(var8, var1);
         if (var9 != null && !var9.getName().startsWith(ShaderMacros.getPrefixMacro()) && (!var9.checkUsed() || isOptionUsed(var9, var6))) {
            String var10 = var9.getName();
            ShaderOption var11 = (ShaderOption)var2.get(var10);
            if (var11 != null) {
               if (!Config.equals(var11.getValueDefault(), var9.getValueDefault())) {
                  Config.warn("Ambiguous shader option: " + var9.getName());
                  Config.warn(" - in " + Config.arrayToString(var11.getPaths()) + ": " + var11.getValueDefault());
                  Config.warn(" - in " + Config.arrayToString(var9.getPaths()) + ": " + var9.getValueDefault());
                  var11.setEnabled(false);
               }

               if (var11.getDescription() == null || var11.getDescription().length() <= 0) {
                  var11.setDescription(var9.getDescription());
               }

               var11.addPaths(var9.getPaths());
            } else {
               var2.put(var10, var9);
            }
         }
      }
   }

   private static boolean isOptionUsed(ShaderOption var0, String[] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         String var6 = var1[var5];
         if (var0.isUsedInLine(var6)) {
            return true;
         }
      }

      return false;
   }

   private static String[] getLines(IShaderPack var0, String var1) {
      try {
         ArrayList var4 = new ArrayList();
         String var5 = loadFile(var1, var0, 0, var4, 0);
         if (var5 == null) {
            return new String[0];
         }

         ByteArrayInputStream var6 = new ByteArrayInputStream(var5.getBytes());
         return Config.readLines(var6);
      } catch (IOException var8) {
         Config.dbg(var8.getClass().getName() + ": " + var8.getMessage());
         return new String[0];
      }
   }

   private static ShaderOption getShaderOption(String var0, String var1) {
      ShaderOption var5 = null;
      if (var5 == null) {
         var5 = ShaderOptionSwitch.parseOption_ShaderOption(var0, var1);
      }

      if (var5 == null) {
         var5 = ShaderOptionVariable.parseOption(var0, var1);
      }

      if (var5 != null) {
         return var5;
      }

      if (var5 == null) {
         var5 = ShaderOptionSwitchConst.parseOption(var0, var1);
      }

      if (var5 == null) {
         var5 = ShaderOptionVariableConst.f(var0, var1);
      }

      return var5 != null && setConstNames.contains(var5.getName()) ? var5 : null;
   }

   private static Set<String> makeSetConstNames() {
      HashSet var2 = new HashSet();
      var2.add("shadowMapResolution");
      var2.add("shadowDistance");
      var2.add("shadowIntervalSize");
      var2.add("generateShadowMipmap");
      var2.add("generateShadowColorMipmap");
      var2.add("shadowHardwareFiltering");
      var2.add("shadowHardwareFiltering0");
      var2.add("shadowHardwareFiltering1");
      var2.add("shadowtex0Mipmap");
      var2.add("shadowtexMipmap");
      var2.add("shadowtex1Mipmap");
      var2.add("shadowcolor0Mipmap");
      var2.add("shadowColor0Mipmap");
      var2.add("shadowcolor1Mipmap");
      var2.add("shadowColor1Mipmap");
      var2.add("shadowtex0Nearest");
      var2.add("shadowtexNearest");
      var2.add("shadow0MinMagNearest");
      var2.add("shadowtex1Nearest");
      var2.add("shadow1MinMagNearest");
      var2.add("shadowcolor0Nearest");
      var2.add("shadowColor0Nearest");
      var2.add("shadowColor0MinMagNearest");
      var2.add("shadowcolor1Nearest");
      var2.add("shadowColor1Nearest");
      var2.add("shadowColor1MinMagNearest");
      var2.add("wetnessHalflife");
      var2.add("drynessHalflife");
      var2.add("eyeBrightnessHalflife");
      var2.add("centerDepthHalflife");
      var2.add("sunPathRotation");
      var2.add("ambientOcclusionLevel");
      var2.add("superSamplingLevel");
      var2.add("noiseTextureResolution");
      return var2;
   }

   public static ShaderProfile[] parseProfiles(Properties var0, ShaderOption[] var1) {
      String var5 = "profile.";
      ArrayList var6 = new ArrayList();

      for (Object var8 : var0.keySet()) {
         String var9 = (String)var8;
         if (var9.startsWith(var5)) {
            String var10 = var9.substring(var5.length());
            var0.getProperty(var9);
            HashSet var11 = new HashSet();
            ShaderProfile var12 = parseProfile(var10, var0, var11, var1);
            if (var12 != null) {
               var6.add(var12);
            }
         }
      }

      return var6.size() <= 0 ? null : (shadersmod.client.ShaderProfile[])(var6.toArray(new ShaderProfile[var6.size()]));
   }

   private static ShaderProfile parseProfile(String var0, Properties var1, Set<String> var2, ShaderOption[] var3) {
      String var7 = "profile.";
      String var8 = var7 + var0;
      if (var2.contains(var8)) {
         Config.warn("[Shaders] Profile already parsed: " + var0);
         return null;
      }

      var2.add(var0);
      ShaderProfile var9 = new ShaderProfile(var0);
      String var10 = var1.getProperty(var8);
      String[] var11 = Config.tokenize(var10, " ");

      for (int var12 = 0; var12 < var11.length; var12++) {
         String var13 = var11[var12];
         if (var13.startsWith(var7)) {
            String var14 = var13.substring(var7.length());
            ShaderProfile var15 = parseProfile(var14, var1, var2, var3);
            if (var9 != null) {
               var9.addOptionValues(var15);
               var9.addDisabledPrograms(var15.getDisabledPrograms());
            }
         } else {
            String[] var19 = Config.tokenize(var13, ":=");
            if (var19.length == 1) {
               String var20 = var19[0];
               boolean var16 = true;
               if (var20.startsWith("!")) {
                  var16 = false;
                  var20 = var20.substring(1);
               }

               String var17 = "program.";
               if (!var16 && var20.startsWith("program.")) {
                  String var24 = var20.substring(var17.length());
                  if (!Shaders.isProgramPath(var24)) {
                     Config.warn("Invalid program: " + var24 + " in profile: " + var9.getName());
                  } else {
                     var9.addDisabledProgram(var24);
                  }
               } else {
                  ShaderOption var18 = ShaderUtils.getShaderOption(var20, var3);
                  if (!(var18 instanceof ShaderOptionSwitch)) {
                     Config.warn("[Shaders] Invalid option: " + var20);
                  } else {
                     var9.addOptionValue(var20, String.valueOf(var16));
                     var18.setVisible(true);
                  }
               }
            } else if (var19.length != 2) {
               Config.warn("[Shaders] Invalid option value: " + var13);
            } else {
               String var21 = var19[0];
               String var22 = var19[1];
               ShaderOption var23 = ShaderUtils.getShaderOption(var21, var3);
               if (var23 == null) {
                  Config.warn("[Shaders] Invalid option: " + var13);
               } else if (!var23.Y(var22)) {
                  Config.warn("[Shaders] Invalid value: " + var13);
               } else {
                  var23.setVisible(true);
                  var9.addOptionValue(var21, var22);
               }
            }
         }
      }

      return var9;
   }

   public static Map<String, ShaderOption[]> parseGuiScreens(Properties var0, ShaderProfile[] var1, ShaderOption[] var2) {
      HashMap var5 = new HashMap();
      parseGuiScreen("screen", var0, var5, var1, var2);
      return var5.isEmpty() ? null : var5;
   }

   private static boolean parseGuiScreen(String var0, Properties var1, Map<String, ShaderOption[]> var2, ShaderProfile[] var3, ShaderOption[] var4) {
      String var8 = var1.getProperty(var0);
      if (var8 == null) {
         return false;
      }

      ArrayList var9 = new ArrayList();
      HashSet var10 = new HashSet();
      String[] var11 = Config.tokenize(var8, " ");

      for (int var12 = 0; var12 < var11.length; var12++) {
         String var13 = var11[var12];
         if (var13.equals("<empty>")) {
            var9.add((ShaderOption)null);
         } else if (var10.contains(var13)) {
            Config.warn("[Shaders] Duplicate option: " + var13 + ", key: " + var0);
         } else {
            var10.add(var13);
            if (var13.equals("<profile>")) {
               if (var3 == null) {
                  Config.warn("[Shaders] Option profile can not be used, no profiles defined: " + var13 + ", key: " + var0);
               } else {
                  ShaderOptionProfile var14 = new ShaderOptionProfile(var3, var4);
                  var9.add(var14);
               }
            } else if (var13.equals("*")) {
               ShaderOptionRest var17 = new ShaderOptionRest("<rest>");
               var9.add(var17);
            } else if (var13.startsWith("[") && var13.endsWith("]")) {
               String var19 = StrUtils.removePrefixSuffix(var13, "[", "]");
               if (!var19.matches("^[a-zA-Z0-9_]+$")) {
                  Config.warn("[Shaders] Invalid screen: " + var13 + ", key: " + var0);
               } else if (!parseGuiScreen("screen." + var19, var1, var2, var3, var4)) {
                  Config.warn("[Shaders] Invalid screen: " + var13 + ", key: " + var0);
               } else {
                  ShaderOptionScreen var15 = new ShaderOptionScreen(var19);
                  var9.add(var15);
               }
            } else {
               ShaderOption var18 = ShaderUtils.getShaderOption(var13, var4);
               if (var18 == null) {
                  Config.warn("[Shaders] Invalid option: " + var13 + ", key: " + var0);
                  var9.add((ShaderOption)null);
               } else {
                  var18.setVisible(true);
                  var9.add(var18);
               }
            }
         }
      }

      ShaderOption[] var16 = (shadersmod.client.ShaderOption[])(var9.toArray(new ShaderOption[var9.size()]));
      var2.put(var0, var16);
      return true;
   }

   public static BufferedReader resolveIncludes(BufferedReader var0, String var1, IShaderPack var2, int var3, List<String> var4, int var5) throws IOException {
      String var9 = "/";
      int var10 = var1.lastIndexOf("/");
      if (var10 >= 0) {
         var9 = var1.substring(0, var10);
      }

      CharArrayWriter var11 = new CharArrayWriter();
      int var12 = -1;
      LinkedHashSet var13 = new LinkedHashSet();
      int var14 = 1;

      while (true) {
         String var15 = var0.readLine();
         if (var15 == null) {
            char[] var23 = var11.toCharArray();
            if (var12 >= 0 && var13.size() > 0) {
               StringBuilder var26 = new StringBuilder();

               for (Object var34$raw : var13) {
                  String var34 = (String)var34$raw;
                  var26.append("#define ");
                  var26.append(var34);
                  var26.append("\n");
               }

               String var31 = var26.toString();
               StringBuilder var35 = new StringBuilder(new String(var23));
               var35.insert(var12, var31);
               String var36 = var35.toString();
               var23 = var36.toCharArray();
            }

            CharArrayReader var27 = new CharArrayReader(var23);
            return new BufferedReader(var27);
         }

         if (var12 < 0) {
            Matcher var16 = PATTERN_VERSION.matcher(var15);
            if (var16.matches()) {
               String var17 = "#define MC_VERSION "
                  + Config.getMinecraftVersionInt()
                  + "\n#define MC_GL_VERSION "
                  + Config.getGlVersion().toInt()
                  + "\n#define MC_GLSL_VERSION "
                  + Config.getGlslVersion().toInt()
                  + "\n#define "
                  + ShaderMacros.getOs()
                  + "\n#define "
                  + ShaderMacros.getVendor()
                  + "\n#define "
                  + ShaderMacros.getRenderer()
                  + "\n";
               String var18 = var15 + "\n" + var17;
               String var19 = "#line " + (var14 + 1) + " " + var3;
               var15 = var18 + var19;
               var12 = var11.size() + var18.length();
            }
         }

         Matcher var22 = PATTERN_INCLUDE.matcher(var15);
         if (var22.matches()) {
            String var24 = var22.group(1);
            boolean var28 = var24.startsWith("/");
            String var32 = var28 ? "/shaders" + var24 : var9 + "/" + var24;
            if (!var4.contains(var32)) {
               var4.add(var32);
            }

            int var20 = var4.indexOf(var32) + 1;
            var15 = loadFile(var32, var2, var20, var4, var5);
            if (var15 == null) {
               throw new IOException("Included file not found: " + var1);
            }

            if (var15.endsWith("\n")) {
               var15 = var15.substring(0, var15.length() - 1);
            }

            var15 = "#line 1 " + var20 + "\n" + var15 + "\n#line " + (var14 + 1) + " " + var3;
         }

         if (var12 >= 0 && var15.contains(ShaderMacros.getPrefixMacro())) {
            String[] var25 = findExtensions(var15, ShaderMacros.getExtensions());

            for (int var29 = 0; var29 < var25.length; var29++) {
               String var33 = var25[var29];
               var13.add(var33);
            }
         }

         var11.write(var15);
         var11.write("\n");
         var14++;
      }
   }

   private static String[] findExtensions(String var0, String[] var1) {
      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < var1.length; var6++) {
         String var7 = var1[var6];
         if (var0.contains(var7)) {
            var5.add(var7);
         }
      }

      return (java.lang.String[])(var5.toArray(new String[var5.size()]));
   }

   private static String loadFile(String var0, IShaderPack var1, int var2, List<String> var3, int var4) throws IOException {
      if (var4 >= (int)e) {
         throw new IOException("#include depth exceeded: " + var4 + ", file: " + var0);
      }

      var4++;
      InputStream var8 = var1.getResourceAsStream(var0);
      if (var8 == null) {
         return null;
      }

      InputStreamReader var9 = new InputStreamReader(var8, "ASCII");
      BufferedReader var10 = new BufferedReader(var9);
      var10 = resolveIncludes(var10, var0, var1, var2, var3, var4);
      CharArrayWriter var11 = new CharArrayWriter();

      while (true) {
         String var12 = var10.readLine();
         if (var12 == null) {
            return var11.toString();
         }

         var11.write(var12);
         var11.write("\n");
      }
   }
}
