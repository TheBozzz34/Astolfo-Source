package bypass.auth;

public class AuthHttpRequest {
   public String v;
   public String s;
   public String F;
   public String P;
   public String u;
   public String[] X = new String[0];
   public String[] E = new String[0];
   public String[] i = new String[0];
   public String[] n = new String[0];
   private static final long a = 19429755765316L;

   public AuthHttpRequest() {
      this.v("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13");
      this.U("application/json");
      this.F("application/json");
   }

   public AuthHttpRequest r(String var1) {
      this.v = var1;
      return this;
   }

   public AuthHttpRequest v(String var1) {
      this.F = var1;
      return this;
   }

   public AuthHttpRequest U(String var1) {
      this.P = var1;
      return this;
   }

   public AuthHttpRequest F(String var1) {
      this.u = var1;
      return this;
   }

   public AuthHttpRequest a(String... var1) {
      this.X = var1;
      return this;
   }

   public AuthHttpRequest O(String... var1) {
      this.E = var1;
      return this;
   }

   public AuthHttpRequest y(String var1) {
      this.s = var1;
      return this;
   }

   public AuthHttpRequest d(String... var1) {
      this.i = var1;
      return this;
   }

   public AuthHttpRequest y(String... var1) {
      this.n = var1;
      return this;
   }

   public String h() {
      if (this.s != null) {
         return this.s;
      }

      StringBuilder var4 = new StringBuilder();

      for (int var5 = 0; var5 < this.X.length; var5++) {
         var4.append(this.X[var5]).append("=").append(this.E[var5]).append("&");
      }

      return var4.toString();
   }
}
