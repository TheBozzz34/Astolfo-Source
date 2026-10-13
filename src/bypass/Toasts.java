package bypass;

import bypass.notification.NotificationType;
import org.apache.commons.lang3.StringUtils;

public enum Toasts {
   TOASTS;

   private static final Toasts[] Q = new Toasts[]{TOASTS};
   private static final long a = 125663378243394L;

   public void S(String var1) {
      this.g(var1, null);
   }

   public void g(String var1, String var2) {
      this.w(var1, var2, 0);
   }

   public void w(String var1, String var2, int var3) {
      this.m(var1, var2, NotificationType.SUCCESS, var3);
   }

   public void D(String var1) {
      this.B(var1, null);
   }

   public void B(String var1, String var2) {
      this.G(var1, var2, 0);
   }

   public void G(String var1, String var2, int var3) {
      this.m(var1, var2, NotificationType.INFO, var3);
   }

   public void m(String var1) {
      this.X(var1, null);
   }

   public void X(String var1, String var2) {
      this.A(var1, var2, 0);
   }

   public void A(String var1, String var2, int var3) {
      this.m(var1, var2, NotificationType.WARNING, var3);
   }

   public void x(String var1) {
      this.k(var1, null);
   }

   public void k(String var1, String var2) {
      this.R(var1, var2, 0);
   }

   public void R(String var1, String var2, int var3) {
      this.m(var1, var2, NotificationType.DANGER, var3);
   }

   private void m(String var1, String var2, NotificationType var3, int var4) {
      long var5 = a ^ 73773453798081L;
      long var7 = var5 ^ 131379821799640L;
      if (var4 <= 0) {
         var4 = this.k(var1) + (var2 == null ? 0 : this.k(var2));
      }

      Astolfo.INSTANCE.getNotificationManager().i(var1, var2, var3, var4).K();
   }

   private int k(String var1) {
      int var4 = StringUtils.countMatches(var1, " ") + 1;
      int var5 = var1.length() / 5;
      int var6 = (var4 + var5) / 2;
      float var7 = 200.0F;
      byte var8 = 1;
      short var9 = 1000;
      byte var10 = 60;
      return (int)((var6 / var7 * var10 + var8) * var9);
   }
}
