package oshi.hardware.common;

import java.util.Arrays;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.util.EdidUtil;

@Immutable
public abstract class AbstractDisplay implements Display {
   private final byte[] edid;

   public AbstractDisplay(byte[] var1) {
      this.edid = Arrays.copyOf(var1, var1.length);
   }

   @Override
   public byte[] getEdid() {
      return Arrays.copyOf(this.edid, this.edid.length);
   }

   @Override
   public String toString() {
      return EdidUtil.toString(this.edid);
   }
}
