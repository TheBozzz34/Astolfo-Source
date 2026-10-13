package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection implements IMetadataSection {
   private final List<AnimationFrame> animationFrames;
   private final int frameWidth;
   private final int frameHeight;
   private final int frameTime;
   private final boolean interpolate;
   private static final long a = 117913957708550L;

   public AnimationMetadataSection(List<AnimationFrame> var1, int var2, int var3, int var4, boolean var5) {
      this.animationFrames = var1;
      this.frameWidth = var2;
      this.frameHeight = var3;
      this.frameTime = var4;
      this.interpolate = var5;
   }

   public int getFrameHeight() {
      return this.frameHeight;
   }

   public int getFrameWidth() {
      return this.frameWidth;
   }

   public int getFrameCount() {
      return this.animationFrames.size();
   }

   public int getFrameTime() {
      return this.frameTime;
   }

   public boolean isInterpolate() {
      return this.interpolate;
   }

   private AnimationFrame getAnimationFrame(int var1) {
      return this.animationFrames.get(var1);
   }

   public int getFrameTimeSingle(int var1) {
      AnimationFrame var5 = this.getAnimationFrame(var1);
      return var5.hasNoTime() ? this.frameTime : var5.getFrameTime();
   }

   public boolean frameHasTime(int var1) {
      return !this.animationFrames.get(var1).hasNoTime();
   }

   public int getFrameIndex(int var1) {
      return this.animationFrames.get(var1).getFrameIndex();
   }

   public Set<Integer> getFrameIndexSet() {
      HashSet var4 = Sets.newHashSet();

      for (AnimationFrame var6 : this.animationFrames) {
         var4.add(var6.getFrameIndex());
      }

      return var4;
   }
}
