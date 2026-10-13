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

   Request m(String var1, String var2) throws Exception;

   Request y(String var1) throws Exception;

   String D() throws Exception;

   default RequestWrapper u(Request var1) {
      return new RequestWrapper(var1);
   }

   default RequestWrapper L() {
      return new RequestWrapper(this);
   }
}
