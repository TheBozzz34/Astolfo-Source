package bypass.util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ProxyScanner {
   private final boolean o;
   private static final long a = 134739016826435L;

   public ProxyScanner(boolean var1) {
      this.o = var1;
   }

   public List<HostAndPort> scan(Collection<HostAndPort> var1, int var2, int var3) {
      ExecutorService var8 = Executors.newFixedThreadPool(var2);
      ArrayList var9 = new ArrayList();
      ArrayList var10 = new ArrayList();
      var1.forEach(var4 -> var9.add(this.scanProxy(var8, var4, var3)));
      var9.forEach(var2x -> {
         try {
            ProxyCheckResult var6 = (ProxyCheckResult)((java.util.concurrent.Future)var2x).get();
            if (this.o) {
               System.out.println("Proxy " + var6.q() + " scanned. Status: " + (var6.r() ? "valid" : "invalid") + ".\n");
            }

            if (var6.r()) {
               var10.add(var6.q());
            }
         } catch (InterruptedException | ExecutionException var7) {
            System.err.format("Caught an %s while trying to scan a proxy: %s.", var7.getClass().getSimpleName(), var7.getMessage());
         }
      });
      var8.shutdown();
      return var10;
   }

   private Future<ProxyCheckResult> scanProxy(ExecutorService var1, HostAndPort var2, int var3) {
      return var1.submit(() -> {
         boolean var5 = false;

         try {
            InetAddress var6 = InetAddress.getByName(var2.getHost());
            if (var6.isReachable(var3)) {
               var5 = true;
            }
         } catch (IOException var7) {
         }

         return new ProxyCheckResult(var2, var5);
      });
   }
}
