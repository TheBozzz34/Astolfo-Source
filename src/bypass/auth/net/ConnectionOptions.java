package bypass.auth.net;

import bypass.loader.check.GuardedConstructor;

public final class ConnectionOptions {
   private int I;
   private String r;
   private String Q;
   private String V;
   private int g;

   public ConnectionOptions(int i, String string, String string2, String string3) {
      this.I = i;
      this.r = string;
      this.Q = string2;
      this.V = string3;
   }

   public void T(int i) {
      this.I = i;
   }

   public void V(String string) {
      this.r = string;
   }

   public void j(String string) {
      this.Q = string;
   }

   public void U(String string) {
      this.V = string;
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

   public Object N(Class<?> class2, Object... objects) {
      return GuardedConstructor.B(class2, objects);
   }

   public static ConnectionOptions M(int i) {
      return new ConnectionOptions(i, null, null, null);
   }

   public static ConnectionOptions w(int i, String string) {
      return new ConnectionOptions(i, string, null, null);
   }

   public static ConnectionOptions m() {
      return M(0);
   }
}
