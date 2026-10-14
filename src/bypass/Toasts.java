package bypass;

import bypass.notification.NotificationType;
import org.apache.commons.lang3.StringUtils;

public enum Toasts {
   TOASTS;

   private static final Toasts[] Q = new Toasts[]{TOASTS};

   public void S(String string) {
      this.g(string, null);
   }

   public void g(String string, String string2) {
      this.w(string, string2, 0);
   }

   public void w(String string, String string2, int i) {
      this.m(string, string2, NotificationType.SUCCESS, i);
   }

   public void D(String string) {
      this.B(string, null);
   }

   public void B(String string, String string2) {
      this.G(string, string2, 0);
   }

   public void G(String string, String string2, int i) {
      this.m(string, string2, NotificationType.INFO, i);
   }

   public void m(String string) {
      this.X(string, null);
   }

   public void X(String string, String string2) {
      this.A(string, string2, 0);
   }

   public void A(String string, String string2, int i) {
      this.m(string, string2, NotificationType.WARNING, i);
   }

   public void x(String string) {
      this.k(string, null);
   }

   public void k(String string, String string2) {
      this.R(string, string2, 0);
   }

   public void R(String string, String string2, int i) {
      this.m(string, string2, NotificationType.DANGER, i);
   }

   private void m(String string, String string2, NotificationType notificationType, int i) {
      if (i <= 0) {
         i = this.k(string) + (string2 == null ? 0 : this.k(string2));
      }

      Astolfo.INSTANCE.getNotificationManager().i(string, string2, notificationType, i).K();
   }

   private int k(String string) {
      int i = StringUtils.countMatches(string, " ") + 1;
      int j = string.length() / 5;
      int l = (i + j) / 2;
      float f = 200.0F;
      byte b = 1;
      short s = 1000;
      byte b2 = 60;
      return (int)((l / f * b2 + b) * s);
   }
}
