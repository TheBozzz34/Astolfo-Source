package bypass.http;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

public final class RequestWrapper extends AbstractRequest {
   private String cachedBody;
   private Request delegate;

   public RequestWrapper(Request request) {
      this.delegate = request;
   }

   @Override
   public String getBody() throws Exception {
      if (this.cachedBody != null) {
         return this.cachedBody;
      }

      String string = this.delegate.getBody();
      this.cachedBody = string;
      return string;
   }

   @Override
   public String getMethod() {
      return this.delegate.getMethod();
   }

   @Override
   public ContentType getContentType() {
      return this.delegate.getContentType();
   }

   @Override
   public int getContentLength() {
      if (this.cachedBody != null) {
         return this.cachedBody.length();
      }

      try {
         this.cachedBody = this.getBody();
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return this.cachedBody.length();
   }

   @Override
   public URL buildUrl(URL url) throws Exception {
      return this.delegate instanceof AbstractRequest ? ((AbstractRequest)this.delegate).buildUrl(url) : url;
   }

   @Override
   public Map<String, String> getHeaders() {
      return this.delegate instanceof AbstractRequest ? ((AbstractRequest)this.delegate).getHeaders() : Collections.emptyMap();
   }
}
