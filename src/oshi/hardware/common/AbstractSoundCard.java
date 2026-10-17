package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;

@Immutable
public abstract class AbstractSoundCard implements SoundCard {
   private String kernelVersion;
   private String name;
   private String codec;

   public AbstractSoundCard(String kernelVersion, String name, String codec) {
      this.kernelVersion = kernelVersion;
      this.name = name;
      this.codec = codec;
   }

   @Override
   public String getDriverVersion() {
      return this.kernelVersion;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getCodec() {
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
      stringBuilder.append(']');
      return stringBuilder.toString();
   }
}
