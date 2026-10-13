package bypass.gui.clickgui.sidebar;

import bypass.config.ConfigFiles;
import bypass.gui.component.Button;
import bypass.notification.NotificationType;

public class SaveConfigButton extends Button {
   private static final long d = 17766823488541L;
   private static final long j = -4062567453132060220L;

   public SaveConfigButton(float var1, float var2, int var3, int var4) {
      super("Save config", var1, var2, var3, var4);
   }

   @Override
   public void G() {
      long var1 = d ^ 7645027727338L;
      long var3 = var1 ^ 89011452765868L;
      String var6 = SidebarGui.config.getValue();
      ConfigFiles.save(ConfigFiles.serializeModules(), var6);
      client.setConfigName(var6);
      client.getNotificationManager().i("Config manager", String.format("Saved %s config", var6), NotificationType.SUCCESS, (int)j).K();
   }
}
