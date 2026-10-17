package oshi.hardware;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Supplier;
import oshi.SystemInfo;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.hardware.platform.linux.LinuxNetworks;
import oshi.hardware.platform.mac.MacNetworks;
import oshi.hardware.platform.unix.freebsd.FreeBsdNetworks;
import oshi.hardware.platform.unix.solaris.SolarisNetworks;
import oshi.hardware.platform.windows.WindowsNetworks;
import oshi.util.FileUtil;
import oshi.util.FormatUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@NotThreadSafe
public class NetworkIF {
   private NetworkInterface networkInterface;
   private int mtu;
   private String mac;
   private String[] ipv4;
   private Short[] subnetMasks;
   private String[] ipv6;
   private Short[] prefixLengths;
   private int ifType;
   private int ndisPhysicalMediumType;
   private boolean connectorPresent;
   private long bytesRecv;
   private long bytesSent;
   private long packetsRecv;
   private long packetsSent;
   private long inErrors;
   private long outErrors;
   private long inDrops;
   private long collisions;
   private long speed;
   private long timeStamp;
   private final Supplier<Properties> vmMacAddrProps = Memoizer.memoize(NetworkIF::queryVmMacAddrProps);

   public NetworkInterface queryNetworkInterface() {
      return this.networkInterface;
   }

   public void setNetworkInterface(NetworkInterface networkInterface) {
      this.networkInterface = networkInterface;

      try {
         this.mtu = networkInterface.getMTU();
         byte[] bytes = networkInterface.getHardwareAddress();
         if (bytes == null) {
            this.mac = "Unknown";
         } else {
            ArrayList arrayList = new ArrayList(6);

            for (byte b : bytes) {
               arrayList.add(String.format("%02x", b));
            }

            this.mac = String.join(":", arrayList);
         }

         ArrayList arrayList2 = new ArrayList();
         ArrayList arrayList3 = new ArrayList();
         ArrayList arrayList4 = new ArrayList();
         ArrayList arrayList5 = new ArrayList();

         for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress inetAddress = interfaceAddress.getAddress();
            if (inetAddress.getHostAddress().length() > 0) {
               if (inetAddress.getHostAddress().contains(":")) {
                  arrayList4.add(inetAddress.getHostAddress().split("%")[0]);
                  arrayList5.add(interfaceAddress.getNetworkPrefixLength());
               } else {
                  arrayList2.add(inetAddress.getHostAddress());
                  arrayList3.add(interfaceAddress.getNetworkPrefixLength());
               }
            }
         }

         this.ipv4 = (java.lang.String[])(arrayList2.toArray(new String[0]));
         this.subnetMasks = (java.lang.Short[])(arrayList3.toArray(new Short[0]));
         this.ipv6 = (java.lang.String[])(arrayList4.toArray(new String[0]));
         this.prefixLengths = (java.lang.Short[])(arrayList5.toArray(new Short[0]));
      } catch (SocketException socketException) {
      }
   }

   public String getName() {
      return this.networkInterface.getName();
   }

   public String getDisplayName() {
      return this.networkInterface.getDisplayName();
   }

   public int getMTU() {
      return this.mtu;
   }

   public String getMacaddr() {
      return this.mac;
   }

   public String[] getIPv4addr() {
      return Arrays.copyOf(this.ipv4, this.ipv4.length);
   }

   public Short[] getSubnetMasks() {
      return Arrays.copyOf(this.subnetMasks, this.subnetMasks.length);
   }

   public String[] getIPv6addr() {
      return Arrays.copyOf(this.ipv6, this.ipv6.length);
   }

   public Short[] getPrefixLengths() {
      return Arrays.copyOf(this.prefixLengths, this.prefixLengths.length);
   }

   public int getIfType() {
      return this.ifType;
   }

   public void setIfType(int ifType) {
      this.ifType = ifType;
   }

   public int getNdisPhysicalMediumType() {
      return this.ndisPhysicalMediumType;
   }

   public void setNdisPhysicalMediumType(int ndisPhysicalMediumType) {
      this.ndisPhysicalMediumType = ndisPhysicalMediumType;
   }

   public boolean isConnectorPresent() {
      return this.connectorPresent;
   }

   public void setConnectorPresent(boolean connectorPresent) {
      this.connectorPresent = connectorPresent;
   }

   public long getBytesRecv() {
      return this.bytesRecv;
   }

   public void setBytesRecv(long bytesRecv) {
      this.bytesRecv = ParseUtil.unsignedLongToSignedLong(bytesRecv);
   }

   public long getBytesSent() {
      return this.bytesSent;
   }

   public void setBytesSent(long bytesSent) {
      this.bytesSent = ParseUtil.unsignedLongToSignedLong(bytesSent);
   }

   public long getPacketsRecv() {
      return this.packetsRecv;
   }

   public void setPacketsRecv(long packetsRecv) {
      this.packetsRecv = ParseUtil.unsignedLongToSignedLong(packetsRecv);
   }

   public long getPacketsSent() {
      return this.packetsSent;
   }

   public void setPacketsSent(long packetsSent) {
      this.packetsSent = ParseUtil.unsignedLongToSignedLong(packetsSent);
   }

   public long getInErrors() {
      return this.inErrors;
   }

   public void setInErrors(long inErrors) {
      this.inErrors = ParseUtil.unsignedLongToSignedLong(inErrors);
   }

   public long getOutErrors() {
      return this.outErrors;
   }

   public void setOutErrors(long outErrors) {
      this.outErrors = ParseUtil.unsignedLongToSignedLong(outErrors);
   }

   public long getInDrops() {
      return this.inDrops;
   }

   public void setInDrops(long inDrops) {
      this.inDrops = inDrops;
   }

   public long getCollisions() {
      return this.collisions;
   }

   public void setCollisions(long collisions) {
      this.collisions = collisions;
   }

   public long getSpeed() {
      return this.speed;
   }

   public void setSpeed(long speed) {
      this.speed = ParseUtil.unsignedLongToSignedLong(speed);
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(long timeStamp) {
      this.timeStamp = timeStamp;
   }

   public boolean updateAttributes() {
      switch (SystemInfo.getCurrentPlatformEnum()) {
         case WINDOWS:
            return WindowsNetworks.updateNetworkStats(this);
         case LINUX:
            return LinuxNetworks.updateNetworkStats(this);
         case MACOSX:
            return MacNetworks.updateNetworkStats(this);
         case SOLARIS:
            return SolarisNetworks.updateNetworkStats(this);
         case FREEBSD:
            return FreeBsdNetworks.updateNetworkStats(this);
         default:
            return false;
      }
   }

   public boolean isKnownVmMacAddr() {
      String string = this.getMacaddr().length() > 7 ? this.getMacaddr().substring(0, 8) : this.getMacaddr();
      return this.vmMacAddrProps.get().containsKey(string.toUpperCase());
   }

   private static Properties queryVmMacAddrProps() {
      return FileUtil.readPropertiesFromFilename("");
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Name: ").append(this.getName()).append(" ").append("(").append(this.getDisplayName()).append(")").append("\n");
      stringBuilder.append("  MAC Address: ").append(this.getMacaddr()).append("\n");
      stringBuilder.append("  MTU: ").append(this.getMTU()).append(", ").append("Speed: ").append(this.getSpeed()).append("\n");
      stringBuilder.append("  IPv4: ").append(Arrays.toString(this.getIPv4addr())).append("\n");
      stringBuilder.append("  Netmask:  ").append(Arrays.toString(this.getSubnetMasks())).append("\n");
      stringBuilder.append("  IPv6: ").append(Arrays.toString(this.getIPv6addr())).append("\n");
      stringBuilder.append("  Prefix Lengths:  ").append(Arrays.toString(this.getPrefixLengths())).append("\n");
      stringBuilder.append("  Traffic: received ")
         .append(this.getPacketsRecv())
         .append(" packets/")
         .append(FormatUtil.formatBytes(this.getBytesRecv()))
         .append(" (" + this.getInErrors() + " err, ")
         .append(this.getInDrops() + " drop);");
      stringBuilder.append(" transmitted ")
         .append(this.getPacketsSent())
         .append(" packets/")
         .append(FormatUtil.formatBytes(this.getBytesSent()))
         .append(" (" + this.getOutErrors() + " err, ")
         .append(this.getCollisions() + " coll);");
      return stringBuilder.toString();
   }
}
