package bypass.util;

public final class GetProxyListFetcher {
   private static final long a = 79351643575549L;

   public static ProxySource G(String var0, String[] var1) {
      if (var0.equals("getproxylist")) {
         String var5 = var1[0];
         int var6 = Integer.parseInt(var1[1]);
         int var7 = Integer.parseInt(var1[2]);
         System.out.println("[getproxylist] Threads Count: " + var6);
         System.out.println("[getproxylist] Num Requests: " + var7);
         return new GetProxyListSource(var5, var6, var7);
      } else {
         return null;
      }
   }
}
