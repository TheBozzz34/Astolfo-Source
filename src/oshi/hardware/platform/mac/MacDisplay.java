package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.CoreFoundation.CFDataRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import java.util.ArrayList;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@Immutable
public final class MacDisplay extends AbstractDisplay {
   public MacDisplay(byte[] edid) {
      super(edid);
   }

   public static Display[] getDisplays() {
      ArrayList arrayList = new ArrayList();
      IOIterator ioIterator = IOKitUtil.getMatchingServices("IODisplayConnect");
      if (ioIterator != null) {
         CFStringRef cfStringRef = CFStringRef.createCFString("IODisplayEDID");

         for (IORegistryEntry ioRegistryEntry = ioIterator.next(); ioRegistryEntry != null; ioRegistryEntry = ioIterator.next()) {
            IORegistryEntry ioRegistryEntry2 = ioRegistryEntry.getChildEntry("IOService");
            if (ioRegistryEntry2 != null) {
               CFTypeRef cfTypeRef = ioRegistryEntry2.createCFProperty(cfStringRef);
               if (cfTypeRef != null) {
                  CFDataRef cfDataRef = new CFDataRef(cfTypeRef.getPointer());
                  int i = cfDataRef.getLength();
                  Pointer pointer = cfDataRef.getBytePtr();
                  arrayList.add(new MacDisplay(pointer.getByteArray(0L, i)));
                  cfDataRef.release();
               }

               ioRegistryEntry2.release();
            }

            ioRegistryEntry.release();
         }

         ioIterator.release();
         cfStringRef.release();
      }

      return (oshi.hardware.Display[])(arrayList.toArray(new Display[0]));
   }
}
