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

   public SettingsCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("config", "s", "c"));
   }

   @Override
   public void runCommand(String[] strings) {
      long l = d ^ 22911721602089L;
      long n = l ^ 68337651939526L;
      if (DisconnectHandler.disconnected) {
         toasts.x("You must reconnect to Astolfo before loading a config");
      } else {
         switch (strings.length) {
            case 2:
               switch (strings[1]) {
                  case "folder":
                     m(client.getDirectory());
                     return;
                  default:
                     return;
               }
            case 3:
               String string = strings[2];
               switch (strings[1]) {
                  case "load":
                     File file = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), string + ".json");
                     if (!file.exists()) {
                        client.getNotificationManager().i("Config manager", String.format("%s config doesnt exist", string), NotificationType.DANGER, 2500).K();
                        return;
                     }

                     ConfigFiles.load(string);
                     client.setConfigName(string);
                     client.getNotificationManager().i("Config manager", String.format("Loaded %s config", string), NotificationType.SUCCESS, 2500).K();
                     break;
                  case "save":
                     ConfigFiles.save(ConfigFiles.serializeModules(), string);
                     client.setConfigName(string);
                     client.getNotificationManager().i("Config manager", String.format("Saved %s config", string), NotificationType.SUCCESS, 2500).K();
               }
         }
      }
   }

   public static void m(File file) {
      String string = file.getAbsolutePath();
      if (Util.getOSType() == Util.EnumOS.OSX) {
         try {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", string});
            return;
         } catch (IOException ioException) {
         }
      } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
         String string2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", string);

         try {
            Runtime.getRuntime().exec(string2);
            return;
         } catch (IOException ioException2) {
         }
      }

      boolean bl = false;

      try {
         Class class2 = Class.forName("java.awt.Desktop");
         Object object = class2.getMethod("getDesktop").invoke(null);
         class2.getMethod("browse", URI.class).invoke(object, file.toURI());
      } catch (Throwable throwable) {
         bl = true;
      }

      if (bl) {
         Sys.openURL("file://" + string);
      }
   }
}
