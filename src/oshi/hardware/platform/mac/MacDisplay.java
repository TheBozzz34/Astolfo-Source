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
   private static final long a = 90693491687959L;

   public MacDisplay(byte[] var1) {
      super(var1);
   }

   public static Display[] getDisplays() {
      ArrayList var3 = new ArrayList();
      IOIterator var4 = IOKitUtil.getMatchingServices("IODisplayConnect");
      if (var4 != null) {
         CFStringRef var5 = CFStringRef.createCFString("IODisplayEDID");

         for (IORegistryEntry var6 = var4.next(); var6 != null; var6 = var4.next()) {
            IORegistryEntry var7 = var6.getChildEntry("IOService");
            if (var7 != null) {
               CFTypeRef var8 = var7.createCFProperty(var5);
               if (var8 != null) {
                  CFDataRef var9 = new CFDataRef(var8.getPointer());
                  int var10 = var9.getLength();
                  Pointer var11 = var9.getBytePtr();
                  var3.add(new MacDisplay(var11.getByteArray(0L, var10)));
                  var9.release();
               }

               var7.release();
            }

            var6.release();
         }

         var4.release();
         var5.release();
      }

      return (oshi.hardware.Display[])(var3.toArray(new Display[0]));
   }
}
