package oshi.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface Udev extends Library {
   Udev INSTANCE = Native.load("udev", Udev.class);

   Udev.UdevHandle udev_new();

   void udev_unref(Udev.UdevHandle var1);

   void udev_device_unref(Udev.UdevDevice var1);

   void udev_enumerate_unref(Udev.UdevEnumerate var1);

   Udev.UdevEnumerate udev_enumerate_new(Udev.UdevHandle var1);

   Udev.UdevDevice udev_device_get_parent_with_subsystem_devtype(Udev.UdevDevice var1, String var2, String var3);

   Udev.UdevDevice udev_device_new_from_syspath(Udev.UdevHandle var1, String var2);

   Udev.UdevListEntry udev_list_entry_get_next(Udev.UdevListEntry var1);

   String udev_device_get_sysattr_value(Udev.UdevDevice var1, String var2);

   int udev_enumerate_add_match_subsystem(Udev.UdevEnumerate var1, String var2);

   int udev_enumerate_scan_devices(Udev.UdevEnumerate var1);

   Udev.UdevListEntry udev_enumerate_get_list_entry(Udev.UdevEnumerate var1);

   String udev_list_entry_get_name(Udev.UdevListEntry var1);

   String udev_device_get_devtype(Udev.UdevDevice var1);

   String udev_device_get_devnode(Udev.UdevDevice var1);

   String udev_device_get_syspath(Udev.UdevDevice var1);

   String udev_device_get_property_value(Udev.UdevDevice var1, String var2);

   String udev_device_get_sysname(Udev.UdevDevice var1);

   final class UdevDevice extends PointerType {
      public UdevDevice(Pointer var1) {
         super(var1);
      }

      public UdevDevice() {
      }
   }

   final class UdevEnumerate extends PointerType {
      public UdevEnumerate(Pointer var1) {
         super(var1);
      }

      public UdevEnumerate() {
      }
   }

   final class UdevHandle extends PointerType {
      public UdevHandle(Pointer var1) {
         super(var1);
      }

      public UdevHandle() {
      }
   }

   final class UdevListEntry extends PointerType {
      public UdevListEntry(Pointer var1) {
         super(var1);
      }

      public UdevListEntry() {
      }
   }
}
