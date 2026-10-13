package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.IPHlpAPI.FIXED_INFO;
import com.sun.jna.platform.win32.IPHlpAPI.IP_ADDR_STRING;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class WindowsNetworkParams extends AbstractNetworkParams {
   private static final int COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED = 3;
   private static final long b = 103798698825316L;

   @Override
   public String getDomainName() {
      char[] var3 = new char[256];
      IntByReference var4 = new IntByReference(var3.length);
      return !Kernel32.INSTANCE.GetComputerNameEx(3, var3, var4) ? "" : new String(var3).trim();
   }

   @Override
   public String[] getDnsServers() {
      IntByReference var4 = new IntByReference();
      int var5 = IPHlpAPI.INSTANCE.GetNetworkParams(null, var4);
      if (var5 != 111) {
         return new String[0];
      }

      Memory var6 = new Memory(var4.getValue());
      var5 = IPHlpAPI.INSTANCE.GetNetworkParams(var6, var4);
      if (var5 != 0) {
         return new String[0];
      }

      FIXED_INFO var7 = new FIXED_INFO(var6);
      ArrayList var8 = new ArrayList();

      for (IP_ADDR_STRING var9 = var7.DnsServerList; var9 != null; var9 = var9.Next) {
         String var10 = new String(var9.IpAddress.String, StandardCharsets.US_ASCII);
         int var11 = var10.indexOf(0);
         if (var11 != -1) {
            var10 = var10.substring(0, var11);
         }

         var8.add(var10);
      }

      return (java.lang.String[])(var8.toArray(new String[0]));
   }

   @Override
   public String getHostName() {
      return Kernel32Util.getComputerName();
   }

   @Override
   public String getIpv4DefaultGateway() {
      return this.parseIpv4Route();
   }

   @Override
   public String getIpv6DefaultGateway() {
      return this.parseIpv6Route();
   }

   private String parseIpv4Route() {
      for (String var6 : ExecutingCommand.runNative("route print -4 0.0.0.0")) {
         String[] var7 = ParseUtil.Q.split(var6.trim());
         if (var7.length > 2 && "0.0.0.0".equals(var7[0])) {
            return var7[2];
         }
      }

      return "";
   }

   private String parseIpv6Route() {
      for (String var6 : ExecutingCommand.runNative("route print -6 ::/0")) {
         String[] var7 = ParseUtil.Q.split(var6.trim());
         if (var7.length > 3 && "::/0".equals(var7[2])) {
            return var7[3];
         }
      }

      return "";
   }

   static {
      long var11 = b ^ 28044446615679L;
      long var10000 = var11 ^ 8144244101046452795L;
   }
}
