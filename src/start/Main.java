package start;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;

public class Main {
   public static void main(String[] strings) {
      System.setProperty("java.net.preferIPv4Stack", "true");
      OptionParser optionParser = new OptionParser();
      optionParser.allowsUnrecognizedOptions();
      optionParser.accepts("demo");
      optionParser.accepts("fullscreen");
      optionParser.accepts("checkGlErrors");
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec = optionParser.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec2 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec3 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec5 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec6 = optionParser.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec7 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec8 = optionParser.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec9 = optionParser.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec11 = optionParser.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec12 = optionParser.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec13 = optionParser.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec14 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec15 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec16 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec17 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec18 = optionParser.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec19 = optionParser.accepts("launchToken").withRequiredArg();
      ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec20 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy");
      NonOptionArgumentSpec nonOptionArgumentSpec = optionParser.nonOptions();
      OptionSet optionSet = optionParser.parse(strings);
      List list = optionSet.valuesOf(nonOptionArgumentSpec);
      if (!list.isEmpty()) {
         System.out.println("Completely ignored arguments: " + list);
      }

      String string = (java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec6));
      Proxy proxy = Proxy.NO_PROXY;
      if (string != null) {
         try {
            proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string, (int)(optionSet.valueOf(argumentAcceptingOptionSpec7))));
         } catch (Exception exception) {
         }
      }

      String string2 = (java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec8));
      String string3 = (java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec9));
      if (!proxy.equals(Proxy.NO_PROXY) && isNotEmpty(string2) && isNotEmpty(string3)) {
         Authenticator.setDefault(new ProxyAuthenticator(string2, string3));
      }

      int i = (int)(optionSet.valueOf(argumentAcceptingOptionSpec14));
      int j = (int)(optionSet.valueOf(argumentAcceptingOptionSpec15));
      boolean bl = optionSet.has("fullscreen");
      boolean bl2 = optionSet.has("checkGlErrors");
      boolean bl3 = optionSet.has("demo");
      String string4 = (java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec13));
      Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap propertyMap = gson.fromJson((java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec16)), PropertyMap.class);
      PropertyMap propertyMap2 = gson.fromJson((java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec17)), PropertyMap.class);
      File file = (java.io.File)(optionSet.valueOf(argumentAcceptingOptionSpec3));
      File file2 = optionSet.has(argumentAcceptingOptionSpec4) ? (java.io.File)(optionSet.valueOf(argumentAcceptingOptionSpec4)) : new File(file, "assets/");
      File file3 = optionSet.has(argumentAcceptingOptionSpec5) ? (java.io.File)(optionSet.valueOf(argumentAcceptingOptionSpec5)) : new File(file, "resourcepacks/");
      String string5 = optionSet.has(argumentAcceptingOptionSpec11) ? (String)argumentAcceptingOptionSpec11.value(optionSet) : (String)argumentAcceptingOptionSpec10.value(optionSet);
      String string6 = optionSet.has(argumentAcceptingOptionSpec18) ? (String)argumentAcceptingOptionSpec18.value(optionSet) : null;
      String string7 = (java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec));
      Integer integer = (java.lang.Integer)(optionSet.valueOf(argumentAcceptingOptionSpec2));
      Session session = new Session((String)argumentAcceptingOptionSpec10.value(optionSet), string5, (String)argumentAcceptingOptionSpec12.value(optionSet), (String)argumentAcceptingOptionSpec20.value(optionSet));
      GameConfiguration gameConfiguration = new GameConfiguration(
         new GameConfiguration.UserInformation(session, propertyMap, propertyMap2, proxy),
         new GameConfiguration.DisplayInformation(i, j, bl, bl2),
         new GameConfiguration.FolderInformation(file, file3, file2, string6),
         new GameConfiguration.GameInformation(bl3, string4),
         new GameConfiguration.ServerInformation(string7, integer)
      );
      gameConfiguration.autoLoginToken = (java.lang.String)(optionSet.valueOf(argumentAcceptingOptionSpec19));
      System.out.println(gameConfiguration.autoLoginToken);
      Runtime.getRuntime().addShutdownHook(new IntegratedServerShutdownHook("Client Shutdown Thread"));
      Thread.currentThread().setName("Client thread");
      new Minecraft(gameConfiguration).run();
   }

   private static boolean isNotEmpty(String string) {
      return string != null && !string.isEmpty();
   }
}
