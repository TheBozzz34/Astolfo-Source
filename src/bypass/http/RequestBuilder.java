package bypass.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {
   private AbstractRequest s;
   private Map<String, String> N;
   private int Z;
   private int a;
   private static final long b = 67840794985689L;

   public RequestBuilder() {
   }

   public RequestBuilder Z(AbstractRequest var1) {
      this.s = var1;
      return this;
   }

   public RequestBuilder d(String var1, String var2) {
      if (this.N == null) {
         this.N = new HashMap<>();
      }

      this.N.put(var1, var2);
      return this;
   }

   public RequestBuilder H(int var1) {
      this.a = var1;
      return this;
   }

   public RequestBuilder b(int var1) {
      this.Z = var1;
      return this;
   }

   public RequestOptions P() {
      if (this.s == null) {
      }

      Map var4 = this.N == null ? Collections.emptyMap() : this.N;
      return new RequestOptions(this.s, var4, this.Z, this.a);
   }
}
