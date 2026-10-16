package bypass.http;

import java.util.Map;

public final class RequestOptions {
   private AbstractRequest request;
   private Map<String, String> headers;
   private int readTimeout;
   private int connectTimeout;

   public RequestOptions(AbstractRequest abstractRequest, Map<String, String> map, int i, int j) {
      this.request = abstractRequest;
      this.headers = map;
      this.readTimeout = i;
      this.connectTimeout = j;
   }

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   public String getMethod() {
      return this.request.getMethod();
   }

   public Map<String, String> getHeaders() {
      return this.headers;
   }

   public AbstractRequest getRequest() {
      return this.request;
   }

   public static final RequestBuilder builder() {
      return new RequestBuilder();
   }
}
