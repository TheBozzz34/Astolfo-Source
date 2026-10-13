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
   private static final long a = 136137129724701L;

   public static void main(String[] var0) {
      System.setProperty("java.net.preferIPv4Stack", "true");
      OptionParser var5 = new OptionParser();
      var5.allowsUnrecognizedOptions();
      var5.accepts("demo");
      var5.accepts("fullscreen");
      var5.accepts("checkGlErrors");
      ArgumentAcceptingOptionSpec var6 = var5.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec var7 = var5.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
      ArgumentAcceptingOptionSpec var8 = var5.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
      ArgumentAcceptingOptionSpec var9 = var5.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var10 = var5.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec var11 = var5.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec var12 = var5.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
      ArgumentAcceptingOptionSpec var13 = var5.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec var14 = var5.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec var15 = var5.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
      ArgumentAcceptingOptionSpec var16 = var5.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec var17 = var5.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var18 = var5.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec var19 = var5.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
      ArgumentAcceptingOptionSpec var20 = var5.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
      ArgumentAcceptingOptionSpec var21 = var5.accepts("userProperties").withRequiredArg().defaultsTo("{}");
      ArgumentAcceptingOptionSpec var22 = var5.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
      ArgumentAcceptingOptionSpec var23 = var5.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec var24 = var5.accepts("launchToken").withRequiredArg();
      ArgumentAcceptingOptionSpec var25 = var5.accepts("userType").withRequiredArg().defaultsTo("legacy");
      NonOptionArgumentSpec var26 = var5.nonOptions();
      OptionSet var27 = var5.parse(var0);
      List var28 = var27.valuesOf(var26);
      if (!var28.isEmpty()) {
         System.out.println("Completely ignored arguments: " + var28);
      }

      String var29 = (java.lang.String)(var27.valueOf(var11));
      Proxy var30 = Proxy.NO_PROXY;
      if (var29 != null) {
         try {
            var30 = new Proxy(Type.SOCKS, new InetSocketAddress(var29, (int)(var27.valueOf(var12))));
         } catch (Exception var51) {
         }
      }

      String var31 = (java.lang.String)(var27.valueOf(var13));
      String var32 = (java.lang.String)(var27.valueOf(var14));
      if (!var30.equals(Proxy.NO_PROXY) && b(var31) && b(var32)) {
         Authenticator.setDefault(new ProxyAuthenticator(var31, var32));
      }

      int var33 = (int)(var27.valueOf(var19));
      int var34 = (int)(var27.valueOf(var20));
      boolean var35 = var27.has("fullscreen");
      boolean var36 = var27.has("checkGlErrors");
      boolean var37 = var27.has("demo");
      String var38 = (java.lang.String)(var27.valueOf(var18));
      Gson var39 = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap var40 = var39.fromJson((java.lang.String)(var27.valueOf(var21)), PropertyMap.class);
      PropertyMap var41 = var39.fromJson((java.lang.String)(var27.valueOf(var22)), PropertyMap.class);
      File var42 = (java.io.File)(var27.valueOf(var8));
      File var43 = var27.has(var9) ? (java.io.File)(var27.valueOf(var9)) : new File(var42, "assets/");
      File var44 = var27.has(var10) ? (java.io.File)(var27.valueOf(var10)) : new File(var42, "resourcepacks/");
      String var45 = var27.has(var16) ? (String)var16.value(var27) : (String)var15.value(var27);
      String var46 = var27.has(var23) ? (String)var23.value(var27) : null;
      String var47 = (java.lang.String)(var27.valueOf(var6));
      Integer var48 = (java.lang.Integer)(var27.valueOf(var7));
      Session var49 = new Session((String)var15.value(var27), var45, (String)var17.value(var27), (String)var25.value(var27));
      GameConfiguration var50 = new GameConfiguration(
         new GameConfiguration.UserInformation(var49, var40, var41, var30),
         new GameConfiguration.DisplayInformation(var33, var34, var35, var36),
         new GameConfiguration.FolderInformation(var42, var44, var43, var46),
         new GameConfiguration.GameInformation(var37, var38),
         new GameConfiguration.ServerInformation(var47, var48)
      );
      var50.autoLoginToken = (java.lang.String)(var27.valueOf(var24));
      System.out.println(var50.autoLoginToken);
      Runtime.getRuntime().addShutdownHook(new IntegratedServerShutdownHook("Client Shutdown Thread"));
      Thread.currentThread().setName("Client thread");
      new Minecraft(var50).run();
   }

   private static boolean b(String var0) {
      return var0 != null && !var0.isEmpty();
   }
}
