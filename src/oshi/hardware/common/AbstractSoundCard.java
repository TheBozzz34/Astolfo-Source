package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;

@Immutable
public abstract class AbstractSoundCard implements SoundCard {
   private String kernelVersion;
   private String name;
   private String codec;
   private static final long b = 26119502668402L;
   private static final long k = -3874511876285005731L;

   public AbstractSoundCard(String var1, String var2, String var3) {
      this.kernelVersion = var1;
      this.name = var2;
      this.codec = var3;
   }

   @Override
   public String b() {
      return this.kernelVersion;
   }

   @Override
   public String M() {
      return this.name;
   }

   @Override
   public String g() {
      return this.codec;
   }

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append("SoundCard@");
      var3.append(Integer.toHexString(this.hashCode()));
      var3.append(" [kernelVersion=");
      var3.append(this.kernelVersion);
      var3.append(", name=");
      var3.append(this.name);
      var3.append(", codec=");
      var3.append(this.codec);
      var3.append((char)((int)k));
      return var3.toString();
   }
}
