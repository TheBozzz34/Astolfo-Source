package oshi.util.platform.unix.solaris;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.solaris.LibKstat;
import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import com.sun.jna.platform.unix.solaris.LibKstat.KstatCtl;
import com.sun.jna.platform.unix.solaris.LibKstat.KstatNamed;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FormatUtil;
import oshi.util.Util;

@ThreadSafe
public final class KstatUtil {
   private static final LibKstat KS = LibKstat.INSTANCE;
   private static final KstatCtl KC = KS.kstat_open();
   private static final ReentrantLock CHAIN = new ReentrantLock();
   private static final long a = 121704915534839L;

   private KstatUtil() {
   }

   public static KstatUtil.KstatChain openChain() {
      return new KstatUtil.KstatChain();
   }

   public static String dataLookupString(Kstat var0, String var1) {
      if (var0.ks_type != 1 && var0.ks_type != 4) {
         throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
      }

      Pointer var5 = KS.kstat_data_lookup(var0, var1);
      if (var5 == null) {
         return "";
      }

      KstatNamed var6 = new KstatNamed(var5);
      switch (var6.data_type) {
         case 0:
            return new String(var6.value.charc, StandardCharsets.UTF_8).trim();
         case 1:
            return Integer.toString(var6.value.i32);
         case 2:
            return FormatUtil.toUnsignedString(var6.value.ui32);
         case 3:
            return Long.toString(var6.value.i64);
         case 4:
            return FormatUtil.toUnsignedString(var6.value.ui64);
         case 5:
         case 6:
         case 7:
         case 8:
         default:
            return "";
         case 9:
            return var6.value.str.addr.getString(0L);
      }
   }

   public static long dataLookupLong(Kstat var0, String var1) {
      if (var0.ks_type != 1 && var0.ks_type != 4) {
         throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
      }

      Pointer var4 = KS.kstat_data_lookup(var0, var1);
      if (var4 == null) {
         return 0L;
      }

      KstatNamed var5 = new KstatNamed(var4);
      switch (var5.data_type) {
         case 1:
            return var5.value.i32;
         case 2:
            return FormatUtil.getUnsignedInt(var5.value.ui32);
         case 3:
            return var5.value.i64;
         case 4:
            return var5.value.ui64;
         default:
            return 0L;
      }
   }

   public static ReentrantLock j() {
      return CHAIN;
   }

   public static KstatCtl U() {
      return KC;
   }

   public static LibKstat I() {
      return KS;
   }

   public static class KstatChain implements AutoCloseable {
      private static final long a = 68567881504371L;

      public KstatChain() {
         KstatUtil.j().lock();
         this.update();
      }

      public boolean read(Kstat var1) {
         int var4 = 0;

         while (0 > KstatUtil.I().kstat_read(KstatUtil.U(), var1, null)) {
            if (11 != Native.getLastError() || 5 <= ++var4) {
               return false;
            }

            Util.sleep(8 << var4);
         }

         return true;
      }

      public Kstat lookup(String var1, int var2, String var3) {
         return KstatUtil.I().kstat_lookup(KstatUtil.U(), var1, var2, var3);
      }

      public List<Kstat> lookupAll(String var1, int var2, String var3) {
         ArrayList var8 = new ArrayList();

         for (Kstat var9 = KstatUtil.I().kstat_lookup(KstatUtil.U(), var1, var2, var3); var9 != null; var9 = var9.next()) {
            if ((var1 == null || var1.equals(new String(var9.ks_module, StandardCharsets.US_ASCII).trim()))
               && (var2 < 0 || var2 == var9.ks_instance)
               && (var3 == null || var3.equals(new String(var9.ks_name, StandardCharsets.US_ASCII).trim()))) {
               var8.add(var9);
            }
         }

         return var8;
      }

      public int update() {
         return KstatUtil.I().kstat_chain_update(KstatUtil.U());
      }

      @Override
      public void close() {
         KstatUtil.j().unlock();
      }
   }
}
