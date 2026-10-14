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

   public HttpRequest() {
      this.T("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13");
      this.i("application/json");
      this.S("application/json");
   }

   public HttpRequest g(String string) {
      this.o = string;
      return this;
   }

   public HttpRequest T(String string) {
      this.X = string;
      return this;
   }

   public HttpRequest i(String string) {
      this.I = string;
      return this;
   }

   public HttpRequest S(String string) {
      this.M = string;
      return this;
   }

   public HttpRequest x(String... strings) {
      this.G = strings;
      return this;
   }

   public HttpRequest B(String... strings) {
      this.w = strings;
      return this;
   }

   public HttpRequest n(String string) {
      this.s = string;
      return this;
   }

   public HttpRequest W(String... strings) {
      this.H = strings;
      return this;
   }

   public HttpRequest V(String... strings) {
      this.J = strings;
      return this;
   }

   public String N() {
      if (this.s != null) {
         return this.s;
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < this.G.length; i++) {
         stringBuilder.append(this.G[i]).append("=").append(this.w[i]);
      }

      return stringBuilder.toString();
   }
}
