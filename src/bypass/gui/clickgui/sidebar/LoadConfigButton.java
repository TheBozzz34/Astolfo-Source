package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.config.ConfigFiles;
import bypass.gui.component.Button;
import bypass.notification.NotificationType;
import java.io.File;

public class LoadConfigButton extends Button {
   private static final long d = 37056502659067L;

   public LoadConfigButton(float var1, float var2, int var3, int var4) {
      super("Load config", var1, var2, var3, var4);
   }

   @Override
   public void G() {
      long var1 = d ^ 60922864856076L;
      long var3 = var1 ^ 89011452765868L;
      String var7 = SidebarGui.config.getValue();
      File var8 = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), var7 + ".json");
      if (!var8.exists()) {
         client.getNotificationManager().i("Config manager", String.format("%s config doesnt exist", var7), NotificationType.DANGER, 2500).K();
      } else {
         ConfigFiles.save(ConfigFiles.serializeModules(), client.getConfigName());
         ConfigFiles.load(var7);
         client.setConfigName(var7);
         client.getNotificationManager().i("Config manager", String.format("Loaded %s config", var7), NotificationType.SUCCESS, 2500).K();
      }
   }
}
