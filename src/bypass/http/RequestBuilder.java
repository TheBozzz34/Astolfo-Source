package bypass.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {
   private AbstractRequest request;
   private Map<String, String> headers;
   private int readTimeout;
   private int connectTimeout;

   public RequestBuilder() {
   }

   public RequestBuilder setRequest(AbstractRequest abstractRequest) {
      this.request = abstractRequest;
      return this;
   }

   public RequestBuilder addHeader(String string, String string2) {
      if (this.headers == null) {
         this.headers = new HashMap<>();
      }

      this.headers.put(string, string2);
      return this;
   }

   public RequestBuilder setConnectTimeout(int i) {
      this.connectTimeout = i;
      return this;
   }

   public RequestBuilder setReadTimeout(int i) {
      this.readTimeout = i;
      return this;
   }

   public RequestOptions build() {
      if (this.request == null) {
      }

      Map map = this.headers == null ? Collections.emptyMap() : this.headers;
      return new RequestOptions(this.request, map, this.readTimeout, this.connectTimeout);
   }
}
