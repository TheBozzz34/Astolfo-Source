package bypass.util;

public class HttpRequest {
   public String o;
   public String s;
   public String X;
   public String I;
   public String M;
   public String[] G = new String[0];
   public String[] w = new String[0];
   public String[] H = new String[0];
   public String[] J = new String[0];
   private static final long a = 28860063227564L;

   public HttpRequest() {
      this.T("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13");
      this.i("application/json");
      this.S("application/json");
   }

   public HttpRequest g(String var1) {
      this.o = var1;
      return this;
   }

   public HttpRequest T(String var1) {
      this.X = var1;
      return this;
   }

   public HttpRequest i(String var1) {
      this.I = var1;
      return this;
   }

   public HttpRequest S(String var1) {
      this.M = var1;
      return this;
   }

   public HttpRequest x(String... var1) {
      this.G = var1;
      return this;
   }

   public HttpRequest B(String... var1) {
      this.w = var1;
      return this;
   }

   public HttpRequest n(String var1) {
      this.s = var1;
      return this;
   }

   public HttpRequest W(String... var1) {
      this.H = var1;
      return this;
   }

   public HttpRequest V(String... var1) {
      this.J = var1;
      return this;
   }

   public String N() {
      if (this.s != null) {
         return this.s;
      }

      StringBuilder var4 = new StringBuilder();

      for (int var5 = 0; var5 < this.G.length; var5++) {
         var4.append(this.G[var5]).append("=").append(this.w[var5]);
      }

      return var4.toString();
   }
}
