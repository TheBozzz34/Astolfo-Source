package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.config.ConfigFiles;
import bypass.gui.component.Button;
import bypass.notification.NotificationType;
import java.io.File;

public class LoadConfigButton extends Button {
   public LoadConfigButton(float f, float g, int i, int j) {
      super("Load config", f, g, i, j);
   }

   @Override
   public void G() {
      String string = SidebarGui.config.getValue();
      File file = new File(Astolfo.INSTANCE.getDirectory().getAbsolutePath(), string + ".json");
      if (!file.exists()) {
         client.getNotificationManager().i("Config manager", String.format("%s config doesnt exist", string), NotificationType.DANGER, 2500).K();
      } else {
         ConfigFiles.save(ConfigFiles.serializeModules(), client.getConfigName());
         ConfigFiles.load(string);
         client.setConfigName(string);
         client.getNotificationManager().i("Config manager", String.format("Loaded %s config", string), NotificationType.SUCCESS, 2500).K();
      }
   }
}
