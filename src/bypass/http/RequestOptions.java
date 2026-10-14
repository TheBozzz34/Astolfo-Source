package bypass.http;

import java.util.Map;

public final class RequestOptions {
   private AbstractRequest M;
   private Map<String, String> q;
   private int u;
   private int T;

   public RequestOptions(AbstractRequest abstractRequest, Map<String, String> map, int i, int j) {
      this.M = abstractRequest;
      this.q = map;
      this.u = i;
      this.T = j;
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
