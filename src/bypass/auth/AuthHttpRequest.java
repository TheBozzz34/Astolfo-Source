package bypass.auth;

public class AuthHttpRequest {
   public String url;
   public String rawBody;
   public String userAgent;
   public String accept;
   public String contentType;
   public String[] paramNames = new String[0];
   public String[] paramValues = new String[0];
   public String[] headerNames = new String[0];
   public String[] headerValues = new String[0];

   public AuthHttpRequest() {
      this.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13");
      this.setAccept("application/json");
      this.setContentType("application/json");
   }

   public AuthHttpRequest setUrl(String string) {
      this.url = string;
      return this;
   }

   public AuthHttpRequest setUserAgent(String string) {
      this.userAgent = string;
      return this;
   }

   public AuthHttpRequest setAccept(String string) {
      this.accept = string;
      return this;
   }

   public AuthHttpRequest setContentType(String string) {
      this.contentType = string;
      return this;
   }

   public AuthHttpRequest setParamNames(String... strings) {
      this.paramNames = strings;
      return this;
   }

   public AuthHttpRequest setParamValues(String... strings) {
      this.paramValues = strings;
      return this;
   }

   public AuthHttpRequest setRawBody(String string) {
      this.rawBody = string;
      return this;
   }

   public AuthHttpRequest setHeaderNames(String... strings) {
      this.headerNames = strings;
      return this;
   }

   public AuthHttpRequest setHeaderValues(String... strings) {
      this.headerValues = strings;
      return this;
   }

   public String buildBody() {
      if (this.rawBody != null) {
         return this.rawBody;
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < this.paramNames.length; i++) {
         stringBuilder.append(this.paramNames[i]).append("=").append(this.paramValues[i]).append("&");
      }

      return stringBuilder.toString();
   }
}
