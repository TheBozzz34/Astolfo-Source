package bypass.auth.net;

import bypass.loader.check.GuardedConstructor;

public final class ConnectionOptions {
   private int I;
   private String r;
   private String Q;
   private String V;
   private int g;

   public ConnectionOptions(int var1, String var2, String var3, String var4) {
      this.I = var1;
      this.r = var2;
      this.Q = var3;
      this.V = var4;
   }

   public void T(int var1) {
      this.I = var1;
   }

   public void V(String var1) {
      this.r = var1;
   }

   public void j(String var1) {
      this.Q = var1;
   }

   public void U(String var1) {
      this.V = var1;
   }

   public int O() {
      return this.I;
   }

   public String N() {
      return this.r;
   }

   public String c() {
      return this.Q;
   }

   public String B() {
      return this.V;
   }

   public int g() {
      return this.g;
   }

   public Object N(Class<?> var1, Object... var2) {
      return GuardedConstructor.B(var1, var2);
   }

   public static ConnectionOptions M(int var0) {
      return new ConnectionOptions(var0, null, null, null);
   }

   public static ConnectionOptions w(int var0, String var1) {
      return new ConnectionOptions(var0, var1, null, null);
   }

   public static ConnectionOptions m() {
      return M(0);
   }
}
