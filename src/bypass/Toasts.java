package bypass;

import bypass.notification.NotificationType;
import org.apache.commons.lang3.StringUtils;

public enum Toasts {
   TOASTS;

   private static final Toasts[] VALUES = new Toasts[]{TOASTS};

   public void success(String string) {
      this.successMessage(string, null);
   }

   public void successMessage(String string, String string2) {
      this.successTimed(string, string2, 0);
   }

   public void successTimed(String string, String string2, int i) {
      this.show(string, string2, NotificationType.SUCCESS, i);
   }

   public void info(String string) {
      this.infoMessage(string, null);
   }

   public void infoMessage(String string, String string2) {
      this.infoTimed(string, string2, 0);
   }

   public void infoTimed(String string, String string2, int i) {
      this.show(string, string2, NotificationType.INFO, i);
   }

   public void warning(String string) {
      this.warningMessage(string, null);
   }

   public void warningMessage(String string, String string2) {
      this.warningTimed(string, string2, 0);
   }

   public void warningTimed(String string, String string2, int i) {
      this.show(string, string2, NotificationType.WARNING, i);
   }

   public void danger(String string) {
      this.dangerMessage(string, null);
   }

   public void dangerMessage(String string, String string2) {
      this.dangerTimed(string, string2, 0);
   }

   public void dangerTimed(String string, String string2, int i) {
      this.show(string, string2, NotificationType.DANGER, i);
   }

   private void show(String string, String string2, NotificationType notificationType, int i) {
      if (i <= 0) {
         i = this.getReadingDuration(string) + (string2 == null ? 0 : this.getReadingDuration(string2));
      }

      Astolfo.INSTANCE.getNotificationManager().createNotification(string, string2, notificationType, i).show();
   }

   private int getReadingDuration(String string) {
      int i = StringUtils.countMatches(string, " ") + 1;
      int j = string.length() / 5;
      int k = (i + j) / 2;
      float f = 200.0F;
      byte b = 1;
      short s = 1000;
      byte b2 = 60;
      return (int)((k / f * b2 + b) * s);
   }
}
