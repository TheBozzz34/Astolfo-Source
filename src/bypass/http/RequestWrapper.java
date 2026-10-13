package bypass.http;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

public final class RequestWrapper extends AbstractRequest {
   private String B;
   private Request U;
   private static final long b = 95054725548215L;

   public RequestWrapper(Request var1) {
      this.U = var1;
   }

   @Override
   public String D() throws Exception {
      if (this.B != null) {
         return this.B;
      }

      String var4 = this.U.D();
      this.B = var4;
      return var4;
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
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return this.B.length();
   }

   @Override
   public URL m(URL var1) throws Exception {
      return this.U instanceof AbstractRequest ? ((AbstractRequest)this.U).m(var1) : var1;
   }

   @Override
   public Map<String, String> U() {
      return this.U instanceof AbstractRequest ? ((AbstractRequest)this.U).U() : Collections.emptyMap();
   }
}
