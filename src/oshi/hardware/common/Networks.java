package oshi.hardware.common;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;

@ThreadSafe
public class Networks {
   private static final long a = 71406743786277L;

   public NetworkIF[] getNetworks() {
      ArrayList var4 = new ArrayList();

      for (NetworkInterface var8 : this.getNetworkInterfaces()) {
         NetworkIF var9 = new NetworkIF();
         var9.setNetworkInterface(var8);
         var9.updateAttributes();
         var4.add(var9);
      }

      return (oshi.hardware.NetworkIF[])(var4.toArray(new NetworkIF[0]));
   }

   public NetworkInterface[] getNetworkInterfaces() {
      ArrayList var4 = new ArrayList();
      Enumeration var5 = null;

      try {
         var5 = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException var9) {
      }

      if (var5 != null) {
         while (var5.hasMoreElements()) {
            NetworkInterface var6 = (NetworkInterface)var5.nextElement();

            try {
               if (!var6.isLoopback() && var6.getHardwareAddress() != null) {
                  var4.add(var6);
               }
            } catch (SocketException var8) {
            }
         }
      }

      return (java.net.NetworkInterface[])(var4.toArray(new NetworkInterface[0]));
   }
}
