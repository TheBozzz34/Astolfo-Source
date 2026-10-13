package net.minecraft.src;

import java.util.Comparator;
import org.lwjgl.opengl.DisplayMode;

public class DisplayModeComparator implements Comparator {
   private static final long a = 74186338748623L;

   @Override
   public int compare(Object var1, Object var2) {
      DisplayMode var6 = (DisplayMode)var1;
      DisplayMode var7 = (DisplayMode)var2;
      return var6.getWidth() != var7.getWidth()
         ? var6.getWidth() - var7.getWidth()
         : (
            var6.getHeight() != var7.getHeight()
               ? var6.getHeight() - var7.getHeight()
               : (
                  var6.getBitsPerPixel() != var7.getBitsPerPixel()
                     ? var6.getBitsPerPixel() - var7.getBitsPerPixel()
                     : (var6.getFrequency() != var7.getFrequency() ? var6.getFrequency() - var7.getFrequency() : 0)
               )
         );
   }
}
