package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {
   private final List<SoundList.SoundEntry> soundList = Lists.newArrayList();
   private boolean replaceExisting;
   private SoundCategory category;

   public List<SoundList.SoundEntry> getSoundList() {
      return this.soundList;
   }

   public boolean canReplaceExisting() {
      return this.replaceExisting;
   }

   public void setReplaceExisting(boolean var1) {
      this.replaceExisting = var1;
   }

   public SoundCategory getSoundCategory() {
      return this.category;
   }

   public void setSoundCategory(SoundCategory var1) {
      this.category = var1;
   }

   public static class SoundEntry {
      private String name;
      private float volume = 1.0F;
      private float pitch = 1.0F;
      private int weight = 1;
      private SoundList.SoundEntry.Type type = SoundList.SoundEntry.Type.FILE;
      private boolean streaming = false;

      public String getSoundEntryName() {
         return this.name;
      }

      public void setSoundEntryName(String var1) {
         this.name = var1;
      }

      public float q() {
         return this.volume;
      }

      public void setSoundEntryVolume(float var1) {
         this.volume = var1;
      }

      public float y() {
         return this.pitch;
      }

      public void setSoundEntryPitch(float var1) {
         this.pitch = var1;
      }

      public int getSoundEntryWeight() {
         return this.weight;
      }

      public void setSoundEntryWeight(int var1) {
         this.weight = var1;
      }

      public SoundList.SoundEntry.Type getSoundEntryType() {
         return this.type;
      }

      public void setSoundEntryType(SoundList.SoundEntry.Type var1) {
         this.type = var1;
      }

      public boolean isStreaming() {
         return this.streaming;
      }

      public void setStreaming(boolean var1) {
         this.streaming = var1;
      }

      public enum Type {
         FILE("file"),
         SOUND_EVENT("event");

         private final String field_148583_c;
         private static final long a = 77345476550370L;

         Type(String var3) {
            this.field_148583_c = var3;
         }

         public static SoundList.SoundEntry.Type getType(String var0) {
            for (SoundList.SoundEntry.Type var7 : values()) {
               if (var7.field_148583_c.equals(var0)) {
                  return var7;
               }
            }

            return null;
         }
      }
   }
}
