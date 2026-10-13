package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.CoreFoundation.CFArrayRef;
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef;
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.IOKit.IOService;
import java.time.LocalDate;
import java.util.ArrayList;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;

@ThreadSafe
public final class MacPowerSource extends AbstractPowerSource {
   private static final CoreFoundation CF = CoreFoundation.INSTANCE;
   private static final IOKit IO = IOKit.INSTANCE;
   private static final long e = 78848464837096L;

   public MacPowerSource(
      String var1,
      String var2,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      boolean var15,
      boolean var16,
      boolean var17,
      PowerSource.CapacityUnits var18,
      int var19,
      int var20,
      int var21,
      int var22,
      String var23,
      LocalDate var24,
      String var25,
      String var26,
      double var27
   ) {
      super(var1, var2, var3, var5, var7, var9, var11, var13, var15, var16, var17, var18, var19, var20, var21, var22, var23, var24, var25, var26, var27);
   }

   public static PowerSource[] Y() {
      long var0 = e ^ 131356754242093L;
      String var4 = "unknown";
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = -1.0;
      double var11 = 0.0;
      boolean var13 = false;
      boolean var14 = false;
      boolean var15 = false;
      PowerSource.CapacityUnits var16 = PowerSource.CapacityUnits.RELATIVE;
      int var17 = 0;
      int var18 = 1;
      int var19 = 1;
      int var20 = -1;
      String var21 = "unknown";
      LocalDate var22 = null;
      String var23 = "unknown";
      String var24 = "unknown";
      double var25 = 0.0;
      IOService var27 = IOKitUtil.getMatchingService("AppleSmartBattery");
      if (var27 != null) {
         String var28 = var27.getStringProperty("DeviceName");
         if (var28 != null) {
            var4 = var28;
         }

         var28 = var27.getStringProperty("Manufacturer");
         if (var28 != null) {
            var23 = var28;
         }

         var28 = var27.getStringProperty("BatterySerialNumber");
         if (var28 != null) {
            var24 = var28;
         }

         Integer var29 = var27.getIntegerProperty("ManufactureDate");
         if (var29 != null) {
            int var30 = var29 & 31;
            int var31 = var29 >> 5 & 15;
            int var32 = var29 >> 9 & 127;
            var22 = LocalDate.of(1980 + var32, var31, var30);
         }

         var29 = var27.getIntegerProperty("DesignCapacity");
         if (var29 != null) {
            var19 = var29;
         }

         var29 = var27.getIntegerProperty("MaxCapacity");
         if (var29 != null) {
            var18 = var29;
         }

         var29 = var27.getIntegerProperty("CurrentCapacity");
         if (var29 != null) {
            var17 = var29;
         }

         var16 = PowerSource.CapacityUnits.MAH;
         var29 = var27.getIntegerProperty("TimeRemaining");
         if (var29 != null) {
            var5 = var29.intValue() * 60.0;
         }

         var29 = var27.getIntegerProperty("CycleCount");
         if (var29 != null) {
            var20 = var29;
         }

         var29 = var27.getIntegerProperty("Temperature");
         if (var29 != null) {
            var25 = var29.intValue() / 100.0;
         }

         var29 = var27.getIntegerProperty("Voltage");
         if (var29 != null) {
            var9 = var29.intValue() / 1000.0;
         }

         var29 = var27.getIntegerProperty("Amperage");
         if (var29 != null) {
            var11 = var29.intValue();
         }

         var7 = var9 * var11;
         Boolean var64 = var27.getBooleanProperty("ExternalConnected");
         if (var64 != null) {
            var13 = var64;
         }

         var64 = var27.getBooleanProperty("IsCharging");
         if (var64 != null) {
            var14 = var64;
         }

         var15 = !var14;
         var27.release();
      }

      CFTypeRef var54 = IO.IOPSCopyPowerSourcesInfo();
      CFArrayRef var63 = IO.IOPSCopyPowerSourcesList(var54);
      long var10000 = 4820673702787070533L ^ var0;
      int var66 = var63.getCount();
      double var67 = IO.IOPSGetTimeRemainingEstimate();
      CFStringRef var33 = CFStringRef.createCFString("Name");
      CFStringRef var34 = CFStringRef.createCFString("Is Present");
      CFStringRef var35 = CFStringRef.createCFString("Current Capacity");
      CFStringRef var36 = CFStringRef.createCFString("Max Capacity");
      ArrayList var37 = new ArrayList(var66);

      for (int var38 = 0; var38 < var66; var38++) {
         Pointer var39 = var63.getValueAtIndex(var38);
         CFTypeRef var40 = new CFTypeRef();
         var40.setPointer(var39);
         CFDictionaryRef var41 = IO.IOPSGetPowerSourceDescription(var54, var40);
         Pointer var42 = var41.getValue(var34);
         if (var42 != null) {
            CFBooleanRef var43 = new CFBooleanRef(var42);
            if (0 != CF.CFBooleanGetValue(var43)) {
               var42 = var41.getValue(var33);
               CFStringRef var44 = new CFStringRef(var42);
               String var45 = var44.stringValue();
               if (var45 == null) {
                  var45 = "unknown";
               }

               double var46 = 0.0;
               if (var41.getValueIfPresent(var35, null)) {
                  var42 = var41.getValue(var35);
                  CFNumberRef var48 = new CFNumberRef(var42);
                  var46 = var48.intValue();
               }

               double var71 = 1.0;
               if (var41.getValueIfPresent(var36, null)) {
                  var42 = var41.getValue(var36);
                  CFNumberRef var50 = new CFNumberRef(var42);
                  var71 = var50.intValue();
               }

               double var72 = Math.min(1.0, var46 / var71);
               var37.add(
                  new MacPowerSource(
                     var45,
                     var4,
                     var72,
                     var67,
                     var5,
                     var7,
                     var9,
                     var11,
                     var13,
                     var14,
                     var15,
                     var16,
                     var17,
                     var18,
                     var19,
                     var20,
                     var21,
                     var22,
                     var23,
                     var24,
                     var25
                  )
               );
            }
         }
      }

      var34.release();
      var33.release();
      var35.release();
      var36.release();
      var63.release();
      var54.release();
      return (oshi.hardware.PowerSource[])(var37.toArray(new MacPowerSource[0]));
   }
}
