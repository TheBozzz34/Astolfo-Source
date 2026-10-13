package bypass.http;

import java.util.Map;

public final class RequestOptions {
   private AbstractRequest M;
   private Map<String, String> q;
   private int u;
   private int T;

   public RequestOptions(AbstractRequest var1, Map<String, String> var2, int var3, int var4) {
      this.M = var1;
      this.q = var2;
      this.u = var3;
      this.T = var4;
   }

   public int e() {
      return this.T;
   }

   public int o() {
      return this.u;
   }

   public String d() {
      return this.M.t();
   }

   public Map<String, String> Q() {
      return this.q;
   }

   public AbstractRequest S() {
      return this.M;
   }

   public static final RequestBuilder C() {
      return new RequestBuilder();
   }
}
