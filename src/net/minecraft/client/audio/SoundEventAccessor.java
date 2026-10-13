package net.minecraft.client.audio;

public class SoundEventAccessor implements ISoundEventAccessor<SoundPoolEntry> {
   private final SoundPoolEntry entry;
   private final int weight;

   public SoundEventAccessor(SoundPoolEntry var1, int var2) {
      this.entry = var1;
      this.weight = var2;
   }

   @Override
   public int getWeight() {
      return this.weight;
   }

   public SoundPoolEntry cloneEntry() {
      return new SoundPoolEntry(this.entry);
   }
}
