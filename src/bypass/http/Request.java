package bypass.http;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface Request {
   String e = StandardCharsets.UTF_8.name();
   String G = "&";
   String P = "=";
   String d = "?";

   Map<String, String> I();

   String t();

   ContentType V();

   int d();

   Request m(String string, String string2) throws Exception;

   Request y(String string) throws Exception;

   String D() throws Exception;

   default RequestWrapper u(Request request) {
      return new RequestWrapper(request);
   }

   default RequestWrapper L() {
      return new RequestWrapper(this);
   }
}
