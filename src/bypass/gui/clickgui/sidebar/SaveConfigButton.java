package bypass.gui.clickgui.sidebar;

import bypass.config.ConfigFiles;
import bypass.gui.component.Button;
import bypass.notification.NotificationType;

public class SaveConfigButton extends Button {
   public SaveConfigButton(float f, float g, int i, int j) {
      super("Save config", f, g, i, j);
   }

   @Override
   public void onPress() {
      String string = SidebarGui.config.getValue();
      ConfigFiles.save(ConfigFiles.serializeModules(), string);
      client.setConfigName(string);
      client.getNotificationManager().createNotification("Config manager", String.format("Saved %s config", string), NotificationType.SUCCESS, 2500).show();
   }
}
