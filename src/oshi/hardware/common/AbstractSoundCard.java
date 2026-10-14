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

   public AbstractSoundCard(String kernelVersion, String name, String codec) {
      this.kernelVersion = kernelVersion;
      this.name = name;
      this.codec = codec;
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
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("SoundCard@");
      stringBuilder.append(Integer.toHexString(this.hashCode()));
      stringBuilder.append(" [kernelVersion=");
      stringBuilder.append(this.kernelVersion);
      stringBuilder.append(", name=");
      stringBuilder.append(this.name);
      stringBuilder.append(", codec=");
      stringBuilder.append(this.codec);
      stringBuilder.append((char)((int)k));
      return stringBuilder.toString();
   }
}
