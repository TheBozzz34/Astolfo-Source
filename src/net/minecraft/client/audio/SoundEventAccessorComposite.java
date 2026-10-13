package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite implements ISoundEventAccessor<SoundPoolEntry> {
   private final List<ISoundEventAccessor<SoundPoolEntry>> soundPool = Lists.newArrayList();
   private final Random rnd = new Random();
   private final ResourceLocation soundLocation;
   private final SoundCategory category;
   private double eventPitch;
   private double eventVolume;
   private static final long a = 40999934782850L;

   public SoundEventAccessorComposite(ResourceLocation var1, double var2, double var4, SoundCategory var6) {
      this.soundLocation = var1;
      this.eventVolume = var4;
      this.eventPitch = var2;
      this.category = var6;
   }

   @Override
   public int getWeight() {
      int var4 = 0;

      for (ISoundEventAccessor var6 : this.soundPool) {
         var4 += var6.getWeight();
      }

      return var4;
   }

   public SoundPoolEntry cloneEntry() {
      int var4 = this.getWeight();
      if (!this.soundPool.isEmpty() && var4 != 0) {
         int var5 = this.rnd.nextInt(var4);

         for (ISoundEventAccessor var7 : this.soundPool) {
            var5 -= var7.getWeight();
            if (var5 < 0) {
               SoundPoolEntry var8 = (SoundPoolEntry)var7.cloneEntry();
               var8.setPitch(var8.getPitch() * this.eventPitch);
               var8.setVolume(var8.getVolume() * this.eventVolume);
               return var8;
            }
         }

         return SoundHandler.missing_sound;
      } else {
         return SoundHandler.missing_sound;
      }
   }

   public void addSoundToEventPool(ISoundEventAccessor<SoundPoolEntry> var1) {
      this.soundPool.add(var1);
   }

   public ResourceLocation getSoundEventLocation() {
      return this.soundLocation;
   }

   public SoundCategory getSoundCategory() {
      return this.category;
   }
}
