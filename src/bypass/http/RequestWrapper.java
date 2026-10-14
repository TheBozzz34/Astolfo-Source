package bypass.http;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

public final class RequestWrapper extends AbstractRequest {
   private String B;
   private Request U;

   public RequestWrapper(Request request) {
      this.U = request;
   }

   @Override
   public String D() throws Exception {
      if (this.B != null) {
         return this.B;
      }

      String string = this.U.D();
      this.B = string;
      return string;
   }

   @Override
   public String t() {
      return this.U.t();
   }

   @Override
   public ContentType V() {
      return this.U.V();
   }

   @Override
   public int d() {
      if (this.B != null) {
         return this.B.length();
      }

      try {
         this.B = this.D();
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return this.B.length();
   }

   @Override
   public URL m(URL url) throws Exception {
      return this.U instanceof AbstractRequest ? ((AbstractRequest)this.U).m(url) : url;
   }

   @Override
   public Map<String, String> U() {
      return this.U instanceof AbstractRequest ? ((AbstractRequest)this.U).U() : Collections.emptyMap();
   }
}
