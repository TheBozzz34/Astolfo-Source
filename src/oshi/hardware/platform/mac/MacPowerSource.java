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

   public MacPowerSource(
      String psName,
      String psDeviceName,
      double psRemainingCapacityPercent,
      double psTimeRemainingEstimated,
      double psTimeRemainingInstant,
      double psPowerUsageRate,
      double psVoltage,
      double psAmperage,
      boolean psPowerOnLine,
      boolean psCharging,
      boolean psDischarging,
      PowerSource.CapacityUnits psCapacityUnits,
      int psCurrentCapacity,
      int psMaxCapacity,
      int psDesignCapacity,
      int psCycleCount,
      String psChemistry,
      LocalDate psManufactureDate,
      String psManufacturer,
      String psSerialNumber,
      double psTemperature
   ) {
      super(
         psName,
         psDeviceName,
         psRemainingCapacityPercent,
         psTimeRemainingEstimated,
         psTimeRemainingInstant,
         psPowerUsageRate,
         psVoltage,
         psAmperage,
         psPowerOnLine,
         psCharging,
         psDischarging,
         psCapacityUnits,
         psCurrentCapacity,
         psMaxCapacity,
         psDesignCapacity,
         psCycleCount,
         psChemistry,
         psManufactureDate,
         psManufacturer,
         psSerialNumber,
         psTemperature
      );
   }

   public static PowerSource[] Y() {
      String string = "unknown";
      double d = 0.0;
      double e = 0.0;
      double d2 = -1.0;
      double d3 = 0.0;
      boolean bl = false;
      boolean bl2 = false;
      boolean bl3 = false;
      PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
      int i = 0;
      int j = 1;
      int k = 1;
      int l = -1;
      String string2 = "unknown";
      LocalDate localDate = null;
      String string3 = "unknown";
      String string4 = "unknown";
      double d4 = 0.0;
      IOService ioService = IOKitUtil.getMatchingService("AppleSmartBattery");
      if (ioService != null) {
         String string5 = ioService.getStringProperty("DeviceName");
         if (string5 != null) {
            string = string5;
         }

         string5 = ioService.getStringProperty("Manufacturer");
         if (string5 != null) {
            string3 = string5;
         }

         string5 = ioService.getStringProperty("BatterySerialNumber");
         if (string5 != null) {
            string4 = string5;
         }

         Integer integer = ioService.getIntegerProperty("ManufactureDate");
         if (integer != null) {
            int m = integer & 31;
            int n = integer >> 5 & 15;
            int i2 = integer >> 9 & 127;
            localDate = LocalDate.of(1980 + i2, n, m);
         }

         integer = ioService.getIntegerProperty("DesignCapacity");
         if (integer != null) {
            k = integer;
         }

         integer = ioService.getIntegerProperty("MaxCapacity");
         if (integer != null) {
            j = integer;
         }

         integer = ioService.getIntegerProperty("CurrentCapacity");
         if (integer != null) {
            i = integer;
         }

         capacityUnits = PowerSource.CapacityUnits.MAH;
         integer = ioService.getIntegerProperty("TimeRemaining");
         if (integer != null) {
            d = integer.intValue() * 60.0;
         }

         integer = ioService.getIntegerProperty("CycleCount");
         if (integer != null) {
            l = integer;
         }

         integer = ioService.getIntegerProperty("Temperature");
         if (integer != null) {
            d4 = integer.intValue() / 100.0;
         }

         integer = ioService.getIntegerProperty("Voltage");
         if (integer != null) {
            d2 = integer.intValue() / 1000.0;
         }

         integer = ioService.getIntegerProperty("Amperage");
         if (integer != null) {
            d3 = integer.intValue();
         }

         e = d2 * d3;
         Boolean boolean2 = ioService.getBooleanProperty("ExternalConnected");
         if (boolean2 != null) {
            bl = boolean2;
         }

         boolean2 = ioService.getBooleanProperty("IsCharging");
         if (boolean2 != null) {
            bl2 = boolean2;
         }

         bl3 = !bl2;
         ioService.release();
      }

      CFTypeRef cfTypeRef = IO.IOPSCopyPowerSourcesInfo();
      CFArrayRef cfArrayRef = IO.IOPSCopyPowerSourcesList(cfTypeRef);
      int i3 = cfArrayRef.getCount();
      double d5 = IO.IOPSGetTimeRemainingEstimate();
      CFStringRef cfStringRef = CFStringRef.createCFString("Name");
      CFStringRef cfStringRef2 = CFStringRef.createCFString("Is Present");
      CFStringRef cfStringRef3 = CFStringRef.createCFString("Current Capacity");
      CFStringRef cfStringRef4 = CFStringRef.createCFString("Max Capacity");
      ArrayList arrayList = new ArrayList(i3);

      for (int i4 = 0; i4 < i3; i4++) {
         Pointer pointer = cfArrayRef.getValueAtIndex(i4);
         CFTypeRef cfTypeRef2 = new CFTypeRef();
         cfTypeRef2.setPointer(pointer);
         CFDictionaryRef cfDictionaryRef = IO.IOPSGetPowerSourceDescription(cfTypeRef, cfTypeRef2);
         Pointer pointer2 = cfDictionaryRef.getValue(cfStringRef2);
         if (pointer2 != null) {
            CFBooleanRef cfBooleanRef = new CFBooleanRef(pointer2);
            if (0 != CF.CFBooleanGetValue(cfBooleanRef)) {
               pointer2 = cfDictionaryRef.getValue(cfStringRef);
               CFStringRef cfStringRef5 = new CFStringRef(pointer2);
               String string6 = cfStringRef5.stringValue();
               if (string6 == null) {
                  string6 = "unknown";
               }

               double d6 = 0.0;
               if (cfDictionaryRef.getValueIfPresent(cfStringRef3, null)) {
                  pointer2 = cfDictionaryRef.getValue(cfStringRef3);
                  CFNumberRef cfNumberRef = new CFNumberRef(pointer2);
                  d6 = cfNumberRef.intValue();
               }

               double d7 = 1.0;
               if (cfDictionaryRef.getValueIfPresent(cfStringRef4, null)) {
                  pointer2 = cfDictionaryRef.getValue(cfStringRef4);
                  CFNumberRef cfNumberRef2 = new CFNumberRef(pointer2);
                  d7 = cfNumberRef2.intValue();
               }

               double d8 = Math.min(1.0, d6 / d7);
               arrayList.add(
                  new MacPowerSource(
                     string6,
                     string,
                     d8,
                     d5,
                     d,
                     e,
                     d2,
                     d3,
                     bl,
                     bl2,
                     bl3,
                     capacityUnits,
                     i,
                     j,
                     k,
                     l,
                     string2,
                     localDate,
                     string3,
                     string4,
                     d4
                  )
               );
            }
         }
      }

      cfStringRef2.release();
      cfStringRef.release();
      cfStringRef3.release();
      cfStringRef4.release();
      cfArrayRef.release();
      cfTypeRef.release();
      return (oshi.hardware.PowerSource[])(arrayList.toArray(new MacPowerSource[0]));
   }
}
