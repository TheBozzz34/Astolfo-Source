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

   public static ShaderOption[] parseShaderPackOptions(IShaderPack shaderPack, String[] programNames, List<Integer> listDimensions) {
      if (shaderPack == null) {
         return new ShaderOption[0];
      }

      HashMap hashMap = new HashMap();
      collectShaderOptions(shaderPack, "/shaders", programNames, hashMap);

      for (int i : listDimensions) {
         String string = "/shaders/world" + i;
         collectShaderOptions(shaderPack, string, programNames, hashMap);
      }

      Collection collection = hashMap.values();
      ShaderOption[] shaderOptions = (shadersmod.client.ShaderOption[])(collection.toArray(new ShaderOption[collection.size()]));
      Comparator comparator = new Comparator<ShaderOption>() {
         public int compare(ShaderOption o1, ShaderOption o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
         }
      };
      Arrays.sort(shaderOptions, comparator);
      return shaderOptions;
   }

   private static void collectShaderOptions(IShaderPack shaderPack, String dir, String[] programNames, Map<String, ShaderOption> mapOptions) {
      for (int i = 0; i < programNames.length; i++) {
         String string = programNames[i];
         if (!string.equals("")) {
            String string2 = dir + "/" + string + ".vsh";
            String string3 = dir + "/" + string + ".fsh";
            collectShaderOptions(shaderPack, string2, mapOptions);
            collectShaderOptions(shaderPack, string3, mapOptions);
         }
      }
   }

   private static void collectShaderOptions(IShaderPack sp, String path, Map<String, ShaderOption> mapOptions) {
      String[] strings = getLines(sp, path);

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         ShaderOption shaderOption = getShaderOption(string, path);
         if (shaderOption != null && !shaderOption.getName().startsWith(ShaderMacros.getPrefixMacro()) && (!shaderOption.checkUsed() || isOptionUsed(shaderOption, strings))) {
            String string2 = shaderOption.getName();
            ShaderOption shaderOption2 = (ShaderOption)mapOptions.get(string2);
            if (shaderOption2 != null) {
               if (!Config.equals(shaderOption2.getValueDefault(), shaderOption.getValueDefault())) {
                  Config.warn("Ambiguous shader option: " + shaderOption.getName());
                  Config.warn(" - in " + Config.arrayToString(shaderOption2.getPaths()) + ": " + shaderOption2.getValueDefault());
                  Config.warn(" - in " + Config.arrayToString(shaderOption.getPaths()) + ": " + shaderOption.getValueDefault());
                  shaderOption2.setEnabled(false);
               }

               if (shaderOption2.getDescription() == null || shaderOption2.getDescription().length() <= 0) {
                  shaderOption2.setDescription(shaderOption.getDescription());
               }

               shaderOption2.addPaths(shaderOption.getPaths());
            } else {
               mapOptions.put(string2, shaderOption);
            }
         }
      }
   }

   private static boolean isOptionUsed(ShaderOption so, String[] lines) {
      for (int i = 0; i < lines.length; i++) {
         String string = lines[i];
         if (so.isUsedInLine(string)) {
            return true;
         }
      }

      return false;
   }

   private static String[] getLines(IShaderPack sp, String path) {
      try {
         ArrayList arrayList = new ArrayList();
         String string = loadFile(path, sp, 0, arrayList, 0);
         if (string == null) {
            return new String[0];
         }

         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
         return Config.readLines(byteArrayInputStream);
      } catch (IOException ioException) {
         Config.dbg(ioException.getClass().getName() + ": " + ioException.getMessage());
         return new String[0];
      }
   }

   private static ShaderOption getShaderOption(String line, String path) {
      ShaderOption shaderOption = null;
      if (shaderOption == null) {
         shaderOption = ShaderOptionSwitch.parseOption_ShaderOption(line, path);
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionVariable.parseOption(line, path);
      }

      if (shaderOption != null) {
         return shaderOption;
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionSwitchConst.parseOption(line, path);
      }

      if (shaderOption == null) {
         shaderOption = ShaderOptionVariableConst.f(line, path);
      }

      return shaderOption != null && setConstNames.contains(shaderOption.getName()) ? shaderOption : null;
   }

   private static Set<String> makeSetConstNames() {
      HashSet hashSet = new HashSet();
      hashSet.add("shadowMapResolution");
      hashSet.add("shadowDistance");
      hashSet.add("shadowIntervalSize");
      hashSet.add("generateShadowMipmap");
      hashSet.add("generateShadowColorMipmap");
      hashSet.add("shadowHardwareFiltering");
      hashSet.add("shadowHardwareFiltering0");
      hashSet.add("shadowHardwareFiltering1");
      hashSet.add("shadowtex0Mipmap");
      hashSet.add("shadowtexMipmap");
      hashSet.add("shadowtex1Mipmap");
      hashSet.add("shadowcolor0Mipmap");
      hashSet.add("shadowColor0Mipmap");
      hashSet.add("shadowcolor1Mipmap");
      hashSet.add("shadowColor1Mipmap");
      hashSet.add("shadowtex0Nearest");
      hashSet.add("shadowtexNearest");
      hashSet.add("shadow0MinMagNearest");
      hashSet.add("shadowtex1Nearest");
      hashSet.add("shadow1MinMagNearest");
      hashSet.add("shadowcolor0Nearest");
      hashSet.add("shadowColor0Nearest");
      hashSet.add("shadowColor0MinMagNearest");
      hashSet.add("shadowcolor1Nearest");
      hashSet.add("shadowColor1Nearest");
      hashSet.add("shadowColor1MinMagNearest");
      hashSet.add("wetnessHalflife");
      hashSet.add("drynessHalflife");
      hashSet.add("eyeBrightnessHalflife");
      hashSet.add("centerDepthHalflife");
      hashSet.add("sunPathRotation");
      hashSet.add("ambientOcclusionLevel");
      hashSet.add("superSamplingLevel");
      hashSet.add("noiseTextureResolution");
      return hashSet;
   }

   public static ShaderProfile[] parseProfiles(Properties props, ShaderOption[] shaderOptions) {
      String string = "profile.";
      ArrayList arrayList = new ArrayList();

      for (Object object : props.keySet()) {
         String string2 = (String)object;
         if (string2.startsWith(string)) {
            String string3 = string2.substring(string.length());
            props.getProperty(string2);
            HashSet hashSet = new HashSet();
            ShaderProfile shaderProfile = parseProfile(string3, props, hashSet, shaderOptions);
            if (shaderProfile != null) {
               arrayList.add(shaderProfile);
            }
         }
      }

      return arrayList.size() <= 0 ? null : (shadersmod.client.ShaderProfile[])(arrayList.toArray(new ShaderProfile[arrayList.size()]));
   }

   private static ShaderProfile parseProfile(String name, Properties props, Set<String> parsedProfiles, ShaderOption[] shaderOptions) {
      String string = "profile.";
      String string2 = string + name;
      if (parsedProfiles.contains(string2)) {
         Config.warn("[Shaders] Profile already parsed: " + name);
         return null;
      }

      parsedProfiles.add(name);
      ShaderProfile shaderProfile = new ShaderProfile(name);
      String string3 = props.getProperty(string2);
      String[] strings = Config.tokenize(string3, " ");

      for (int i = 0; i < strings.length; i++) {
         String string4 = strings[i];
         if (string4.startsWith(string)) {
            String string5 = string4.substring(string.length());
            ShaderProfile shaderProfile2 = parseProfile(string5, props, parsedProfiles, shaderOptions);
            if (shaderProfile != null) {
               shaderProfile.addOptionValues(shaderProfile2);
               shaderProfile.addDisabledPrograms(shaderProfile2.getDisabledPrograms());
            }
         } else {
            String[] strings2 = Config.tokenize(string4, ":=");
            if (strings2.length == 1) {
               String string6 = strings2[0];
               boolean bl = true;
               if (string6.startsWith("!")) {
                  bl = false;
                  string6 = string6.substring(1);
               }

               String string7 = "program.";
               if (!bl && string6.startsWith("program.")) {
                  String string8 = string6.substring(string7.length());
                  if (!Shaders.isProgramPath(string8)) {
                     Config.warn("Invalid program: " + string8 + " in profile: " + shaderProfile.getName());
                  } else {
                     shaderProfile.addDisabledProgram(string8);
                  }
               } else {
                  ShaderOption shaderOption = ShaderUtils.getShaderOption(string6, shaderOptions);
                  if (!(shaderOption instanceof ShaderOptionSwitch)) {
                     Config.warn("[Shaders] Invalid option: " + string6);
                  } else {
                     shaderProfile.addOptionValue(string6, String.valueOf(bl));
                     shaderOption.setVisible(true);
                  }
               }
            } else if (strings2.length != 2) {
               Config.warn("[Shaders] Invalid option value: " + string4);
            } else {
               String string9 = strings2[0];
               String string10 = strings2[1];
               ShaderOption shaderOption2 = ShaderUtils.getShaderOption(string9, shaderOptions);
               if (shaderOption2 == null) {
                  Config.warn("[Shaders] Invalid option: " + string4);
               } else if (!shaderOption2.isValidValue(string10)) {
                  Config.warn("[Shaders] Invalid value: " + string4);
               } else {
                  shaderOption2.setVisible(true);
                  shaderProfile.addOptionValue(string9, string10);
               }
            }
         }
      }

      return shaderProfile;
   }

   public static Map<String, ShaderOption[]> parseGuiScreens(Properties props, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
      HashMap hashMap = new HashMap();
      parseGuiScreen("screen", props, hashMap, shaderProfiles, shaderOptions);
      return hashMap.isEmpty() ? null : hashMap;
   }

   private static boolean parseGuiScreen(
      String key, Properties props, Map<String, ShaderOption[]> map, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions
   ) {
      String string = props.getProperty(key);
      if (string == null) {
         return false;
      }

      ArrayList arrayList = new ArrayList();
      HashSet hashSet = new HashSet();
      String[] strings = Config.tokenize(string, " ");

      for (int i = 0; i < strings.length; i++) {
         String string2 = strings[i];
         if (string2.equals("<empty>")) {
            arrayList.add((ShaderOption)null);
         } else if (hashSet.contains(string2)) {
            Config.warn("[Shaders] Duplicate option: " + string2 + ", key: " + key);
         } else {
            hashSet.add(string2);
            if (string2.equals("<profile>")) {
               if (shaderProfiles == null) {
                  Config.warn("[Shaders] Option profile can not be used, no profiles defined: " + string2 + ", key: " + key);
               } else {
                  ShaderOptionProfile shaderOptionProfile = new ShaderOptionProfile(shaderProfiles, shaderOptions);
                  arrayList.add(shaderOptionProfile);
               }
            } else if (string2.equals("*")) {
               ShaderOptionRest shaderOptionRest = new ShaderOptionRest("<rest>");
               arrayList.add(shaderOptionRest);
            } else if (string2.startsWith("[") && string2.endsWith("]")) {
               String string3 = StrUtils.removePrefixSuffix(string2, "[", "]");
               if (!string3.matches("^[a-zA-Z0-9_]+$")) {
                  Config.warn("[Shaders] Invalid screen: " + string2 + ", key: " + key);
               } else if (!parseGuiScreen("screen." + string3, props, map, shaderProfiles, shaderOptions)) {
                  Config.warn("[Shaders] Invalid screen: " + string2 + ", key: " + key);
               } else {
                  ShaderOptionScreen shaderOptionScreen = new ShaderOptionScreen(string3);
                  arrayList.add(shaderOptionScreen);
               }
            } else {
               ShaderOption shaderOption = ShaderUtils.getShaderOption(string2, shaderOptions);
               if (shaderOption == null) {
                  Config.warn("[Shaders] Invalid option: " + string2 + ", key: " + key);
                  arrayList.add((ShaderOption)null);
               } else {
                  shaderOption.setVisible(true);
                  arrayList.add(shaderOption);
               }
            }
         }
      }

      ShaderOption[] shaderOptions2 = (shadersmod.client.ShaderOption[])(arrayList.toArray(new ShaderOption[arrayList.size()]));
      map.put(key, shaderOptions2);
      return true;
   }

   public static BufferedReader resolveIncludes(
      BufferedReader reader, String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel
   ) throws IOException {
      String string = "/";
      int i = filePath.lastIndexOf("/");
      if (i >= 0) {
         string = filePath.substring(0, i);
      }

      CharArrayWriter charArrayWriter = new CharArrayWriter();
      int j = -1;
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      int k = 1;

      while (true) {
         String string2 = reader.readLine();
         if (string2 == null) {
            char[] chars = charArrayWriter.toCharArray();
            if (j >= 0 && linkedHashSet.size() > 0) {
               StringBuilder stringBuilder = new StringBuilder();

               for (Object object : linkedHashSet) {
                  String string3 = (String)object;
                  stringBuilder.append("#define ");
                  stringBuilder.append(string3);
                  stringBuilder.append("\n");
               }

               String string4 = stringBuilder.toString();
               StringBuilder stringBuilder2 = new StringBuilder(new String(chars));
               stringBuilder2.insert(j, string4);
               String string5 = stringBuilder2.toString();
               chars = string5.toCharArray();
            }

            CharArrayReader charArrayReader = new CharArrayReader(chars);
            return new BufferedReader(charArrayReader);
         }

         if (j < 0) {
            Matcher matcher2 = PATTERN_VERSION.matcher(string2);
            if (matcher2.matches()) {
               String string6 = "#define MC_VERSION "
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
               String string7 = string2 + "\n" + string6;
               String string8 = "#line " + (k + 1) + " " + fileIndex;
               string2 = string7 + string8;
               j = charArrayWriter.size() + string7.length();
            }
         }

         Matcher matcher3 = PATTERN_INCLUDE.matcher(string2);
         if (matcher3.matches()) {
            String string9 = matcher3.group(1);
            boolean bl = string9.startsWith("/");
            String string10 = bl ? "/shaders" + string9 : string + "/" + string9;
            if (!listFiles.contains(string10)) {
               listFiles.add(string10);
            }

            int l = listFiles.indexOf(string10) + 1;
            string2 = loadFile(string10, shaderPack, l, listFiles, includeLevel);
            if (string2 == null) {
               throw new IOException("Included file not found: " + filePath);
            }

            if (string2.endsWith("\n")) {
               string2 = string2.substring(0, string2.length() - 1);
            }

            string2 = "#line 1 " + l + "\n" + string2 + "\n#line " + (k + 1) + " " + fileIndex;
         }

         if (j >= 0 && string2.contains(ShaderMacros.getPrefixMacro())) {
            String[] strings = findExtensions(string2, ShaderMacros.getExtensions());

            for (int m = 0; m < strings.length; m++) {
               String string11 = strings[m];
               linkedHashSet.add(string11);
            }
         }

         charArrayWriter.write(string2);
         charArrayWriter.write("\n");
         k++;
      }
   }

   private static String[] findExtensions(String line, String[] extensions) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < extensions.length; i++) {
         String string = extensions[i];
         if (line.contains(string)) {
            arrayList.add(string);
         }
      }

      return (java.lang.String[])(arrayList.toArray(new String[arrayList.size()]));
   }

   private static String loadFile(String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel) throws IOException {
      if (includeLevel >= 10) {
         throw new IOException("#include depth exceeded: " + includeLevel + ", file: " + filePath);
      }

      includeLevel++;
      InputStream inputStream = shaderPack.getResourceAsStream(filePath);
      if (inputStream == null) {
         return null;
      }

      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ASCII");
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      bufferedReader = resolveIncludes(bufferedReader, filePath, shaderPack, fileIndex, listFiles, includeLevel);
      CharArrayWriter charArrayWriter = new CharArrayWriter();

      while (true) {
         String string = bufferedReader.readLine();
         if (string == null) {
            return charArrayWriter.toString();
         }

         charArrayWriter.write(string);
         charArrayWriter.write("\n");
      }
   }
}
