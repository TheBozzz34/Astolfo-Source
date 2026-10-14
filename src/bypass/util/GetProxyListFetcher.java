package bypass.util;

public final class GetProxyListFetcher {
   private static final long a = 79351643575549L;

   public static ProxySource G(String string, String[] strings) {
      if (string.equals("getproxylist")) {
         String string2 = strings[0];
         int i = Integer.parseInt(strings[1]);
         int j = Integer.parseInt(strings[2]);
         System.out.println("[getproxylist] Threads Count: " + i);
         System.out.println("[getproxylist] Num Requests: " + j);
         return new GetProxyListSource(string2, i, j);
      } else {
         return null;
      }
   }
}
