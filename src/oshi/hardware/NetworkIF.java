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
   private static final long a = 51326179852591L;

   public NetworkInterface queryNetworkInterface() {
      return this.networkInterface;
   }

   public void setNetworkInterface(NetworkInterface var1) {
      this.networkInterface = var1;

      try {
         this.mtu = var1.getMTU();
         byte[] var5 = var1.getHardwareAddress();
         if (var5 == null) {
            this.mac = "Unknown";
         } else {
            ArrayList var6 = new ArrayList(6);

            for (byte var10 : var5) {
               var6.add(String.format("%02x", var10));
            }

            this.mac = String.join(":", var6);
         }

         ArrayList var14 = new ArrayList();
         ArrayList var15 = new ArrayList();
         ArrayList var16 = new ArrayList();
         ArrayList var17 = new ArrayList();

         for (InterfaceAddress var11 : var1.getInterfaceAddresses()) {
            InetAddress var12 = var11.getAddress();
            if (var12.getHostAddress().length() > 0) {
               if (var12.getHostAddress().contains(":")) {
                  var16.add(var12.getHostAddress().split("%")[0]);
                  var17.add(var11.getNetworkPrefixLength());
               } else {
                  var14.add(var12.getHostAddress());
                  var15.add(var11.getNetworkPrefixLength());
               }
            }
         }

         this.ipv4 = (java.lang.String[])(var14.toArray(new String[0]));
         this.subnetMasks = (java.lang.Short[])(var15.toArray(new Short[0]));
         this.ipv6 = (java.lang.String[])(var16.toArray(new String[0]));
         this.prefixLengths = (java.lang.Short[])(var17.toArray(new Short[0]));
      } catch (SocketException var13) {
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

   public int s() {
      return this.ifType;
   }

   public void setIfType(int var1) {
      this.ifType = var1;
   }

   public int U() {
      return this.ndisPhysicalMediumType;
   }

   public void d(int var1) {
      this.ndisPhysicalMediumType = var1;
   }

   public boolean k() {
      return this.connectorPresent;
   }

   public void setConnectorPresent(boolean var1) {
      this.connectorPresent = var1;
   }

   public long getBytesRecv() {
      return this.bytesRecv;
   }

   public void setBytesRecv(long var1) {
      this.bytesRecv = ParseUtil.unsignedLongToSignedLong(var1);
   }

   public long getBytesSent() {
      return this.bytesSent;
   }

   public void setBytesSent(long var1) {
      this.bytesSent = ParseUtil.unsignedLongToSignedLong(var1);
   }

   public long getPacketsRecv() {
      return this.packetsRecv;
   }

   public void setPacketsRecv(long var1) {
      this.packetsRecv = ParseUtil.unsignedLongToSignedLong(var1);
   }

   public long getPacketsSent() {
      return this.packetsSent;
   }

   public void setPacketsSent(long var1) {
      this.packetsSent = ParseUtil.unsignedLongToSignedLong(var1);
   }

   public long getInErrors() {
      return this.inErrors;
   }

   public void setInErrors(long var1) {
      this.inErrors = ParseUtil.unsignedLongToSignedLong(var1);
   }

   public long getOutErrors() {
      return this.outErrors;
   }

   public void setOutErrors(long var1) {
      this.outErrors = ParseUtil.unsignedLongToSignedLong(var1);
   }

   public long getInDrops() {
      return this.inDrops;
   }

   public void setInDrops(long var1) {
      this.inDrops = var1;
   }

   public long getCollisions() {
      return this.collisions;
   }

   public void setCollisions(long var1) {
      this.collisions = var1;
   }

   public long getSpeed() {
      return this.speed;
   }

   public void setSpeed(long var1) {
      this.speed = ParseUtil.unsignedLongToSignedLong(var1);
   }

   public long a() {
      return this.timeStamp;
   }

   public void setTimeStamp(long var1) {
      this.timeStamp = var1;
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
      String var4 = this.getMacaddr().length() > 7 ? this.getMacaddr().substring(0, 8) : this.getMacaddr();
      return this.vmMacAddrProps.get().containsKey(var4.toUpperCase());
   }

   private static Properties queryVmMacAddrProps() {
      return FileUtil.readPropertiesFromFilename("");
   }

   @Override
   public String toString() {
      StringBuilder var5 = new StringBuilder();
      var5.append("Name: ").append(this.getName()).append(" ").append("(").append(this.getDisplayName()).append(")").append("\n");
      var5.append("  MAC Address: ").append(this.getMacaddr()).append("\n");
      var5.append("  MTU: ").append(this.getMTU()).append(", ").append("Speed: ").append(this.getSpeed()).append("\n");
      var5.append("  IPv4: ").append(Arrays.toString(this.getIPv4addr())).append("\n");
      var5.append("  Netmask:  ").append(Arrays.toString(this.getSubnetMasks())).append("\n");
      var5.append("  IPv6: ").append(Arrays.toString(this.getIPv6addr())).append("\n");
      var5.append("  Prefix Lengths:  ").append(Arrays.toString(this.getPrefixLengths())).append("\n");
      var5.append("  Traffic: received ")
         .append(this.getPacketsRecv())
         .append(" packets/")
         .append(FormatUtil.formatBytes(this.getBytesRecv()))
         .append(" (" + this.getInErrors() + " err, ")
         .append(this.getInDrops() + " drop);");
      var5.append(" transmitted ")
         .append(this.getPacketsSent())
         .append(" packets/")
         .append(FormatUtil.formatBytes(this.getBytesSent()))
         .append(" (" + this.getOutErrors() + " err, ")
         .append(this.getCollisions() + " coll);");
      return var5.toString();
   }
}
