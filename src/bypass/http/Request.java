package bypass.http;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface Request {
   String CHARSET = StandardCharsets.UTF_8.name();
   String PARAM_SEPARATOR = "&";
   String KEY_VALUE_SEPARATOR = "=";
   String QUERY_PREFIX = "?";

   Map<String, String> getParameters();

   String getMethod();

   ContentType getContentType();

   int getContentLength();

   Request addParameter(String string, String string2) throws Exception;

   Request removeParameter(String string) throws Exception;

   String getBody() throws Exception;

   default RequestWrapper wrap(Request request) {
      return new RequestWrapper(request);
   }

   default RequestWrapper toWrapper() {
      return new RequestWrapper(this);
   }
}
