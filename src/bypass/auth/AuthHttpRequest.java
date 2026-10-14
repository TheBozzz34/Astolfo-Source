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

   public AuthHttpRequest r(String string) {
      this.v = string;
      return this;
   }

   public AuthHttpRequest v(String string) {
      this.F = string;
      return this;
   }

   public AuthHttpRequest U(String string) {
      this.P = string;
      return this;
   }

   public AuthHttpRequest F(String string) {
      this.u = string;
      return this;
   }

   public AuthHttpRequest a(String... strings) {
      this.X = strings;
      return this;
   }

   public AuthHttpRequest O(String... strings) {
      this.E = strings;
      return this;
   }

   public AuthHttpRequest y(String string) {
      this.s = string;
      return this;
   }

   public AuthHttpRequest d(String... strings) {
      this.i = strings;
      return this;
   }

   public AuthHttpRequest y(String... strings) {
      this.n = strings;
      return this;
   }

   public String h() {
      if (this.s != null) {
         return this.s;
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < this.X.length; i++) {
         stringBuilder.append(this.X[i]).append("=").append(this.E[i]).append("&");
      }

      return stringBuilder.toString();
   }
}
