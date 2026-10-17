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

   @Override
   public String getDomainName() {
      char[] chars = new char[256];
      IntByReference intByReference = new IntByReference(chars.length);
      return !Kernel32.INSTANCE.GetComputerNameEx(3, chars, intByReference) ? "" : new String(chars).trim();
   }

   @Override
   public String[] getDnsServers() {
      IntByReference intByReference = new IntByReference();
      int i = IPHlpAPI.INSTANCE.GetNetworkParams(null, intByReference);
      if (i != 111) {
         return new String[0];
      }

      Memory memory = new Memory(intByReference.getValue());
      i = IPHlpAPI.INSTANCE.GetNetworkParams(memory, intByReference);
      if (i != 0) {
         return new String[0];
      }

      FIXED_INFO fixeD_INFO = new FIXED_INFO(memory);
      ArrayList arrayList = new ArrayList();

      for (IP_ADDR_STRING iP_ADDR_STRING = fixeD_INFO.DnsServerList; iP_ADDR_STRING != null; iP_ADDR_STRING = iP_ADDR_STRING.Next) {
         String string = new String(iP_ADDR_STRING.IpAddress.String, StandardCharsets.US_ASCII);
         int j = string.indexOf(0);
         if (j != -1) {
            string = string.substring(0, j);
         }

         arrayList.add(string);
      }

      return (java.lang.String[])(arrayList.toArray(new String[0]));
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
      for (String string : ExecutingCommand.runNative("route print -4 0.0.0.0")) {
         String[] strings = ParseUtil.whitespaces.split(string.trim());
         if (strings.length > 2 && "0.0.0.0".equals(strings[0])) {
            return strings[2];
         }
      }

      return "";
   }

   private String parseIpv6Route() {
      for (String string : ExecutingCommand.runNative("route print -6 ::/0")) {
         String[] strings = ParseUtil.whitespaces.split(string.trim());
         if (strings.length > 3 && "::/0".equals(strings[2])) {
            return strings[3];
         }
      }

      return "";
   }
}
