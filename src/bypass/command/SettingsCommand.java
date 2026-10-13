package bypass.command;

import bypass.Astolfo;
import bypass.auth.DisconnectHandler;
import bypass.config.ConfigFiles;
import bypass.notification.NotificationType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import net.minecraft.util.Util;
import org.lwjgl.Sys;

public final class SettingsCommand extends Command {
   private static final long d = 119702255729076L;

   public SettingsCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Arrays.asList("config", "s", "c"));
   }

   @Override
   public void runCommand(String[] var1) {
      long var2 = d ^ 22911721602089L;
      long var4 = var2 ^ 68337651939526L;
      if (DisconnectHandler.disconnected) {
         toasts.x("You must reconnect to Astolfo before loading a config");
      } else {
         switch (var1.length) {
            case 2:
               switch (var1[1]) {
                  case "folder":
                     m(client.getDirectory());
                     return;
                  default:
                     return;
               }
            case 3:
               String var7 = var1[2];
               switch (var1[1]) {
                  case "load":
                     File var10 = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), var7 + ".json");
                     if (!var10.exists()) {
                        client.getNotificationManager().i("Config manager", String.format("%s config doesnt exist", var7), NotificationType.DANGER, 2500).K();
                        return;
                     }

                     ConfigFiles.load(var7);
                     client.setConfigName(var7);
                     client.getNotificationManager().i("Config manager", String.format("Loaded %s config", var7), NotificationType.SUCCESS, 2500).K();
                     break;
                  case "save":
                     ConfigFiles.save(ConfigFiles.serializeModules(), var7);
                     client.setConfigName(var7);
                     client.getNotificationManager().i("Config manager", String.format("Saved %s config", var7), NotificationType.SUCCESS, 2500).K();
               }
         }
      }
   }

   public static void m(File var0) {
      String var4 = var0.getAbsolutePath();
      if (Util.getOSType() == Util.EnumOS.OSX) {
         try {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", var4});
            return;
         } catch (IOException var10) {
         }
      } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
         String var5 = String.format("cmd.exe /C start \"Open file\" \"%s\"", var4);

         try {
            Runtime.getRuntime().exec(var5);
            return;
         } catch (IOException var9) {
         }
      }

      boolean var11 = false;

      try {
         Class var6 = Class.forName("java.awt.Desktop");
         Object var7 = var6.getMethod("getDesktop").invoke(null);
         var6.getMethod("browse", URI.class).invoke(var7, var0.toURI());
      } catch (Throwable var8) {
         var11 = true;
      }

      if (var11) {
         Sys.openURL("file://" + var4);
      }
   }
}
