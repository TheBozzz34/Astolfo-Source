package oshi.util.platform.mac;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.IOKit.IOConnect;
import com.sun.jna.platform.mac.IOKit.IOService;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.IOKit;
import oshi.jna.platform.mac.SystemB;
import oshi.util.ParseUtil;

@ThreadSafe
public final class SmcUtil {
   private static final IOKit IO = IOKit.INSTANCE;
   private static Map<Integer, SmcUtil.SMCKeyDataKeyInfo> keyInfoCache = new ConcurrentHashMap<>();
   private static final byte[] DATATYPE_SP78 = ParseUtil.asciiStringToByteArray("sp78", 5);
   private static final byte[] DATATYPE_FPE2 = ParseUtil.asciiStringToByteArray("fpe2", 5);
   private static final byte[] DATATYPE_FLT = ParseUtil.asciiStringToByteArray("flt ", 5);
   public static final String SMC_KEY_FAN_NUM = "FNum";
   public static final String SMC_KEY_FAN_SPEED = "F%dAc";
   public static final String SMC_KEY_CPU_TEMP = "TC0P";
   public static final String SMC_KEY_CPU_VOLTAGE = "VC0C";
   public static final byte SMC_CMD_READ_BYTES = 5;
   public static final byte SMC_CMD_READ_KEYINFO = 9;
   public static final int KERNEL_INDEX_SMC = 2;
   private static final long b = 65213212453898L;

   private SmcUtil() {
   }

   public static IOConnect smcOpen() {
      IOService var2 = IOKitUtil.getMatchingService("AppleSMC");
      if (var2 != null) {
         PointerByReference var3 = new PointerByReference();
         int var4 = IO.IOServiceOpen(var2, SystemB.INSTANCE.mach_task_self(), 0, var3);
         var2.release();
         if (var4 == 0) {
            return new IOConnect(var3.getValue());
         }
      }

      return null;
   }

   public static int smcClose(IOConnect var0) {
      return IO.IOServiceClose(var0);
   }

   public static double smcGetFloat(IOConnect var0, String var1) {
      SmcUtil.SMCVal var5 = new SmcUtil.SMCVal();
      int var6 = smcReadKey(var0, var1, var5);
      if (var6 == 0 && var5.dataSize > 0) {
         if (Arrays.equals(var5.dataType, DATATYPE_SP78) && var5.dataSize == 2) {
            return var5.bytes[0] + var5.bytes[1] / 256.0;
         }

         if (Arrays.equals(var5.dataType, DATATYPE_FPE2) && var5.dataSize == 2) {
            return ParseUtil.byteArrayToFloat(var5.bytes, var5.dataSize, 2);
         }

         if (Arrays.equals(var5.dataType, DATATYPE_FLT) && var5.dataSize == 4) {
            return ByteBuffer.wrap(var5.bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
         }
      }

      return 0.0;
   }

   public static long smcGetLong(IOConnect var0, String var1) {
      SmcUtil.SMCVal var4 = new SmcUtil.SMCVal();
      int var5 = smcReadKey(var0, var1, var4);
      return var5 == 0 ? ParseUtil.byteArrayToLong(var4.bytes, var4.dataSize) : 0L;
   }

   public static int smcGetKeyInfo(IOConnect var0, SmcUtil.SMCKeyData var1, SmcUtil.SMCKeyData var2) {
      if (keyInfoCache.containsKey(var1.key)) {
         SmcUtil.SMCKeyDataKeyInfo var6 = keyInfoCache.get(var1.key);
         var2.keyInfo.dataSize = var6.dataSize;
         var2.keyInfo.dataType = var6.dataType;
         var2.keyInfo.dataAttributes = var6.dataAttributes;
      } else {
         var1.data8 = 9;
         int var8 = smcCall(var0, 2, var1, var2);
         if (var8 != 0) {
            return var8;
         }

         SmcUtil.SMCKeyDataKeyInfo var7 = new SmcUtil.SMCKeyDataKeyInfo();
         var7.dataSize = var2.keyInfo.dataSize;
         var7.dataType = var2.keyInfo.dataType;
         var7.dataAttributes = var2.keyInfo.dataAttributes;
         keyInfoCache.put(var1.key, var7);
      }

      return 0;
   }

   public static int smcReadKey(IOConnect var0, String var1, SmcUtil.SMCVal var2) {
      SmcUtil.SMCKeyData var7 = new SmcUtil.SMCKeyData();
      SmcUtil.SMCKeyData var8 = new SmcUtil.SMCKeyData();
      var7.key = (int)ParseUtil.strToLong(var1, 4);
      int var9 = smcGetKeyInfo(var0, var7, var8);
      if (var9 == 0) {
         var2.dataSize = var8.keyInfo.dataSize;
         var2.dataType = ParseUtil.longToByteArray(var8.keyInfo.dataType, 4, 5);
         var7.keyInfo.dataSize = var2.dataSize;
         var7.data8 = 5;
         var9 = smcCall(var0, 2, var7, var8);
         if (var9 == 0) {
            System.arraycopy(var8.bytes, 0, var2.bytes, 0, var2.bytes.length);
            return 0;
         }
      }

      return var9;
   }

   public static int smcCall(IOConnect var0, int var1, SmcUtil.SMCKeyData var2, SmcUtil.SMCKeyData var3) {
      return IO.IOConnectCallStructMethod(var0, var1, var2, new NativeLong(var2.size()), var3, new NativeLongByReference(new NativeLong(var3.size())));
   }

   static {
      long var20 = b ^ 115926065461821L;
      long var10000 = var20 ^ 2610946401399286723L;
      var10000 = var20 ^ 4768613574004857308L;
      var10000 = var20 ^ 6708171798858456207L;
   }

   @FieldOrder({"key", "vers", "pLimitData", "keyInfo", "result", "status", "data8", "data32", "bytes"})
   public static class SMCKeyData extends Structure {
      public int key;
      public SmcUtil.SMCKeyDataVers vers;
      public SmcUtil.SMCKeyDataPLimitData pLimitData;
      public SmcUtil.SMCKeyDataKeyInfo keyInfo;
      public byte result;
      public byte status;
      public byte data8;
      public int data32;
      public byte[] bytes;
      private static final long a = 112294942794875L;
      private static final long b = 8958239380640628768L;

      public SMCKeyData() {
         this.bytes = new byte[(int)b];
      }
   }

   @FieldOrder({"dataSize", "dataType", "dataAttributes"})
   public static class SMCKeyDataKeyInfo extends Structure {
      public int dataSize;
      public int dataType;
      public byte dataAttributes;
   }

   @FieldOrder({"version", "length", "cpuPLimit", "gpuPLimit", "memPLimit"})
   public static class SMCKeyDataPLimitData extends Structure {
      public short w;
      public short n;
      public int V;
      public int O;
      public int Y;
   }

   @FieldOrder({"major", "minor", "build", "reserved", "release"})
   public static class SMCKeyDataVers extends Structure {
      public byte c;
      public byte O;
      public byte w;
      public byte x;
      public short z;
   }

   @FieldOrder({"key", "dataSize", "dataType", "bytes"})
   public static class SMCVal extends Structure {
      public byte[] key = new byte[5];
      public int dataSize;
      public byte[] dataType = new byte[5];
      public byte[] bytes;
      private static final long a = 26655861008615L;
      private static final long b = -5969582801421336544L;

      public SMCVal() {
         this.bytes = new byte[(int)b];
      }
   }
}
