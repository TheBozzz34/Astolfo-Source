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

   private SmcUtil() {
   }

   public static IOConnect smcOpen() {
      IOService ioService = IOKitUtil.getMatchingService("AppleSMC");
      if (ioService != null) {
         PointerByReference pointerByReference = new PointerByReference();
         int i = IO.IOServiceOpen(ioService, SystemB.INSTANCE.mach_task_self(), 0, pointerByReference);
         ioService.release();
         if (i == 0) {
            return new IOConnect(pointerByReference.getValue());
         }
      }

      return null;
   }

   public static int smcClose(IOConnect conn) {
      return IO.IOServiceClose(conn);
   }

   public static double smcGetFloat(IOConnect conn, String key) {
      SmcUtil.SMCVal smcVal = new SmcUtil.SMCVal();
      int i = smcReadKey(conn, key, smcVal);
      if (i == 0 && smcVal.dataSize > 0) {
         if (Arrays.equals(smcVal.dataType, DATATYPE_SP78) && smcVal.dataSize == 2) {
            return smcVal.bytes[0] + smcVal.bytes[1] / 256.0;
         }

         if (Arrays.equals(smcVal.dataType, DATATYPE_FPE2) && smcVal.dataSize == 2) {
            return ParseUtil.byteArrayToFloat(smcVal.bytes, smcVal.dataSize, 2);
         }

         if (Arrays.equals(smcVal.dataType, DATATYPE_FLT) && smcVal.dataSize == 4) {
            return ByteBuffer.wrap(smcVal.bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
         }
      }

      return 0.0;
   }

   public static long smcGetLong(IOConnect conn, String key) {
      SmcUtil.SMCVal smcVal = new SmcUtil.SMCVal();
      int i = smcReadKey(conn, key, smcVal);
      return i == 0 ? ParseUtil.byteArrayToLong(smcVal.bytes, smcVal.dataSize) : 0L;
   }

   public static int smcGetKeyInfo(IOConnect conn, SmcUtil.SMCKeyData inputStructure, SmcUtil.SMCKeyData outputStructure) {
      if (keyInfoCache.containsKey(inputStructure.key)) {
         SmcUtil.SMCKeyDataKeyInfo smcKeyDataKeyInfo = keyInfoCache.get(inputStructure.key);
         outputStructure.keyInfo.dataSize = smcKeyDataKeyInfo.dataSize;
         outputStructure.keyInfo.dataType = smcKeyDataKeyInfo.dataType;
         outputStructure.keyInfo.dataAttributes = smcKeyDataKeyInfo.dataAttributes;
      } else {
         inputStructure.data8 = 9;
         int i = smcCall(conn, 2, inputStructure, outputStructure);
         if (i != 0) {
            return i;
         }

         SmcUtil.SMCKeyDataKeyInfo smcKeyDataKeyInfo2 = new SmcUtil.SMCKeyDataKeyInfo();
         smcKeyDataKeyInfo2.dataSize = outputStructure.keyInfo.dataSize;
         smcKeyDataKeyInfo2.dataType = outputStructure.keyInfo.dataType;
         smcKeyDataKeyInfo2.dataAttributes = outputStructure.keyInfo.dataAttributes;
         keyInfoCache.put(inputStructure.key, smcKeyDataKeyInfo2);
      }

      return 0;
   }

   public static int smcReadKey(IOConnect conn, String key, SmcUtil.SMCVal val) {
      SmcUtil.SMCKeyData smcKeyData = new SmcUtil.SMCKeyData();
      SmcUtil.SMCKeyData smcKeyData2 = new SmcUtil.SMCKeyData();
      smcKeyData.key = (int)ParseUtil.strToLong(key, 4);
      int i = smcGetKeyInfo(conn, smcKeyData, smcKeyData2);
      if (i == 0) {
         val.dataSize = smcKeyData2.keyInfo.dataSize;
         val.dataType = ParseUtil.longToByteArray(smcKeyData2.keyInfo.dataType, 4, 5);
         smcKeyData.keyInfo.dataSize = val.dataSize;
         smcKeyData.data8 = 5;
         i = smcCall(conn, 2, smcKeyData, smcKeyData2);
         if (i == 0) {
            System.arraycopy(smcKeyData2.bytes, 0, val.bytes, 0, val.bytes.length);
            return 0;
         }
      }

      return i;
   }

   public static int smcCall(IOConnect conn, int index, SmcUtil.SMCKeyData inputStructure, SmcUtil.SMCKeyData outputStructure) {
      return IO.IOConnectCallStructMethod(
         conn, index, inputStructure, new NativeLong(inputStructure.size()), outputStructure, new NativeLongByReference(new NativeLong(outputStructure.size()))
      );
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
      public byte[] bytes = new byte[32];
   }

   @FieldOrder({"dataSize", "dataType", "dataAttributes"})
   public static class SMCKeyDataKeyInfo extends Structure {
      public int dataSize;
      public int dataType;
      public byte dataAttributes;
   }

   @FieldOrder({"version", "length", "cpuPLimit", "gpuPLimit", "memPLimit"})
   public static class SMCKeyDataPLimitData extends Structure {
      public short version;
      public short length;
      public int cpuPLimit;
      public int gpuPLimit;
      public int memPLimit;
   }

   @FieldOrder({"major", "minor", "build", "reserved", "release"})
   public static class SMCKeyDataVers extends Structure {
      public byte major;
      public byte minor;
      public byte build;
      public byte x;
      public short z;
   }

   @FieldOrder({"key", "dataSize", "dataType", "bytes"})
   public static class SMCVal extends Structure {
      public byte[] key = new byte[5];
      public int dataSize;
      public byte[] dataType = new byte[5];
      public byte[] bytes = new byte[32];
   }
}
