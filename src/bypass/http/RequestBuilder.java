package bypass.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {
   private AbstractRequest s;
   private Map<String, String> N;
   private int Z;
   private int a;

   public RequestBuilder() {
   }

   public RequestBuilder Z(AbstractRequest abstractRequest) {
      this.s = abstractRequest;
      return this;
   }

   public RequestBuilder d(String string, String string2) {
      if (this.N == null) {
         this.N = new HashMap<>();
      }

      this.N.put(string, string2);
      return this;
   }

   public RequestBuilder H(int i) {
      this.a = i;
      return this;
   }

   public RequestBuilder b(int i) {
      this.Z = i;
      return this;
   }

   public RequestOptions P() {
      if (this.s == null) {
      }

      Map map = this.N == null ? Collections.emptyMap() : this.N;
      return new RequestOptions(this.s, map, this.Z, this.a);
   }
}
