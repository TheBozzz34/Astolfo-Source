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

   private KstatUtil() {
   }

   public static KstatUtil.KstatChain openChain() {
      return new KstatUtil.KstatChain();
   }

   public static String dataLookupString(Kstat ksp, String name) {
      if (ksp.ks_type != 1 && ksp.ks_type != 4) {
         throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
      }

      Pointer pointer = KS.kstat_data_lookup(ksp, name);
      if (pointer == null) {
         return "";
      }

      KstatNamed kstatNamed = new KstatNamed(pointer);
      switch (kstatNamed.data_type) {
         case 0:
            return new String(kstatNamed.value.charc, StandardCharsets.UTF_8).trim();
         case 1:
            return Integer.toString(kstatNamed.value.i32);
         case 2:
            return FormatUtil.toUnsignedString(kstatNamed.value.ui32);
         case 3:
            return Long.toString(kstatNamed.value.i64);
         case 4:
            return FormatUtil.toUnsignedString(kstatNamed.value.ui64);
         case 5:
         case 6:
         case 7:
         case 8:
         default:
            return "";
         case 9:
            return kstatNamed.value.str.addr.getString(0L);
      }
   }

   public static long dataLookupLong(Kstat ksp, String name) {
      if (ksp.ks_type != 1 && ksp.ks_type != 4) {
         throw new IllegalArgumentException("Not a kstat_named or kstat_timer kstat.");
      }

      Pointer pointer = KS.kstat_data_lookup(ksp, name);
      if (pointer == null) {
         return 0L;
      }

      KstatNamed kstatNamed = new KstatNamed(pointer);
      switch (kstatNamed.data_type) {
         case 1:
            return kstatNamed.value.i32;
         case 2:
            return FormatUtil.getUnsignedInt(kstatNamed.value.ui32);
         case 3:
            return kstatNamed.value.i64;
         case 4:
            return kstatNamed.value.ui64;
         default:
            return 0L;
      }
   }

   public static ReentrantLock getChainLock() {
      return CHAIN;
   }

   public static KstatCtl getKstatCtl() {
      return KC;
   }

   public static LibKstat getLibKstat() {
      return KS;
   }

   public static class KstatChain implements AutoCloseable {
      public KstatChain() {
         KstatUtil.getChainLock().lock();
         this.update();
      }

      public boolean read(Kstat ksp) {
         int i = 0;

         while (0 > KstatUtil.getLibKstat().kstat_read(KstatUtil.getKstatCtl(), ksp, null)) {
            if (11 != Native.getLastError() || 5 <= ++i) {
               return false;
            }

            Util.sleep(8 << i);
         }

         return true;
      }

      public Kstat lookup(String module, int instance, String name) {
         return KstatUtil.getLibKstat().kstat_lookup(KstatUtil.getKstatCtl(), module, instance, name);
      }

      public List<Kstat> lookupAll(String module, int instance, String name) {
         ArrayList arrayList = new ArrayList();

         for (Kstat kstat = KstatUtil.getLibKstat().kstat_lookup(KstatUtil.getKstatCtl(), module, instance, name); kstat != null; kstat = kstat.next()) {
            if ((module == null || module.equals(new String(kstat.ks_module, StandardCharsets.US_ASCII).trim()))
               && (instance < 0 || instance == kstat.ks_instance)
               && (name == null || name.equals(new String(kstat.ks_name, StandardCharsets.US_ASCII).trim()))) {
               arrayList.add(kstat);
            }
         }

         return arrayList;
      }

      public int update() {
         return KstatUtil.getLibKstat().kstat_chain_update(KstatUtil.getKstatCtl());
      }

      @Override
      public void close() {
         KstatUtil.getChainLock().unlock();
      }
   }
}
