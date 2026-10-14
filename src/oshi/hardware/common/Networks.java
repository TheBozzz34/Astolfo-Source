package oshi.hardware.common;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.NetworkIF;

@ThreadSafe
public class Networks {
   public NetworkIF[] getNetworks() {
      ArrayList arrayList = new ArrayList();

      for (NetworkInterface networkInterface : this.getNetworkInterfaces()) {
         NetworkIF networkIF = new NetworkIF();
         networkIF.setNetworkInterface(networkInterface);
         networkIF.updateAttributes();
         arrayList.add(networkIF);
      }

      return (oshi.hardware.NetworkIF[])(arrayList.toArray(new NetworkIF[0]));
   }

   public NetworkInterface[] getNetworkInterfaces() {
      ArrayList arrayList = new ArrayList();
      Enumeration enumeration = null;

      try {
         enumeration = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException socketException) {
      }

      if (enumeration != null) {
         while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface)enumeration.nextElement();

            try {
               if (!networkInterface.isLoopback() && networkInterface.getHardwareAddress() != null) {
                  arrayList.add(networkInterface);
               }
            } catch (SocketException socketException2) {
            }
         }
      }

      return (java.net.NetworkInterface[])(arrayList.toArray(new NetworkInterface[0]));
   }
}
