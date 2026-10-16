package bypass.util;

public final class GetProxyListFetcher {
   public static ProxySource createSource(String string, String[] strings) {
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
