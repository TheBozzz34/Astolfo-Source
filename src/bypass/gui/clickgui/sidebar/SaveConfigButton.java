package bypass.gui.clickgui.sidebar;

import bypass.config.ConfigFiles;
import bypass.gui.component.Button;
import bypass.notification.NotificationType;

public class SaveConfigButton extends Button {
   private static final long d = 17766823488541L;
   private static final long j = -4062567453132060220L;

   public SaveConfigButton(float f, float g, int i, int k) {
      super("Save config", f, g, i, k);
   }

   @Override
   public void G() {
      long l = d ^ 7645027727338L;
      long m = l ^ 89011452765868L;
      String string = SidebarGui.config.getValue();
      ConfigFiles.save(ConfigFiles.serializeModules(), string);
      client.setConfigName(string);
      client.getNotificationManager().i("Config manager", String.format("Saved %s config", string), NotificationType.SUCCESS, (int)j).K();
   }
}
