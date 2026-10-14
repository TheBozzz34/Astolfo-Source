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

   public ProxyScanner(boolean bl) {
      this.o = bl;
   }

   public List<HostAndPort> scan(Collection<HostAndPort> collection, int i, int j) {
      ExecutorService executorService = Executors.newFixedThreadPool(i);
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      collection.forEach(hostAndPort -> arrayList.add(this.scanProxy(executorService, hostAndPort, j)));
      arrayList.forEach(object -> {
         try {
            ProxyCheckResult proxyCheckResult = (ProxyCheckResult)((java.util.concurrent.Future)object).get();
            if (this.o) {
               System.out.println("Proxy " + proxyCheckResult.q() + " scanned. Status: " + (proxyCheckResult.r() ? "valid" : "invalid") + ".\n");
            }

            if (proxyCheckResult.r()) {
               arrayList2.add(proxyCheckResult.q());
            }
         } catch (InterruptedException | ExecutionException value) {
            System.err.format("Caught an %s while trying to scan a proxy: %s.", value.getClass().getSimpleName(), value.getMessage());
         }
      });
      executorService.shutdown();
      return arrayList2;
   }

   private Future<ProxyCheckResult> scanProxy(ExecutorService executorService, HostAndPort hostAndPort, int i) {
      return executorService.submit(() -> {
         boolean bl = false;

         try {
            InetAddress inetAddress = InetAddress.getByName(hostAndPort.getHost());
            if (inetAddress.isReachable(i)) {
               bl = true;
            }
         } catch (IOException ioException) {
         }

         return new ProxyCheckResult(hostAndPort, bl);
      });
   }
}
