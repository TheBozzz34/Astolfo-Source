package oshi.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public interface Udev extends Library {
   Udev INSTANCE = Native.load("udev", Udev.class);

   Udev.UdevHandle udev_new();

   void udev_unref(Udev.UdevHandle udevHandle);

   void udev_device_unref(Udev.UdevDevice udevDevice);

   void udev_enumerate_unref(Udev.UdevEnumerate udevEnumerate);

   Udev.UdevEnumerate udev_enumerate_new(Udev.UdevHandle udevHandle);

   Udev.UdevDevice udev_device_get_parent_with_subsystem_devtype(Udev.UdevDevice udevDevice, String string, String string2);

   Udev.UdevDevice udev_device_new_from_syspath(Udev.UdevHandle udevHandle, String string);

   Udev.UdevListEntry udev_list_entry_get_next(Udev.UdevListEntry udevListEntry);

   String udev_device_get_sysattr_value(Udev.UdevDevice udevDevice, String string);

   int udev_enumerate_add_match_subsystem(Udev.UdevEnumerate udevEnumerate, String string);

   int udev_enumerate_scan_devices(Udev.UdevEnumerate udevEnumerate);

   Udev.UdevListEntry udev_enumerate_get_list_entry(Udev.UdevEnumerate udevEnumerate);

   String udev_list_entry_get_name(Udev.UdevListEntry udevListEntry);

   String udev_device_get_devtype(Udev.UdevDevice udevDevice);

   String udev_device_get_devnode(Udev.UdevDevice udevDevice);

   String udev_device_get_syspath(Udev.UdevDevice udevDevice);

   String udev_device_get_property_value(Udev.UdevDevice udevDevice, String string);

   String udev_device_get_sysname(Udev.UdevDevice udevDevice);

   final class UdevDevice extends PointerType {
      public UdevDevice(Pointer address) {
         super(address);
      }

      public UdevDevice() {
      }
   }

   final class UdevEnumerate extends PointerType {
      public UdevEnumerate(Pointer address) {
         super(address);
      }

      public UdevEnumerate() {
      }
   }

   final class UdevHandle extends PointerType {
      public UdevHandle(Pointer address) {
         super(address);
      }

      public UdevHandle() {
      }
   }

   final class UdevListEntry extends PointerType {
      public UdevListEntry(Pointer address) {
         super(address);
      }

      public UdevListEntry() {
      }
   }
}
