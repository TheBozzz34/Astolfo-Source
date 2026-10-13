package net.minecraft.block.state.pattern;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class BlockPattern {
   private final Predicate<BlockWorldState>[][][] blockMatches;
   private final int fingerLength;
   private final int thumbLength;
   private final int palmLength;
   private static final long a = 35711034483539L;

   public BlockPattern(Predicate<BlockWorldState>[][][] var1) {
      this.blockMatches = var1;
      this.fingerLength = var1.length;
      if (this.fingerLength > 0) {
         this.thumbLength = var1[0].length;
         if (this.thumbLength > 0) {
            this.palmLength = var1[0][0].length;
         } else {
            this.palmLength = 0;
         }
      } else {
         this.thumbLength = 0;
         this.palmLength = 0;
      }
   }

   public int getThumbLength() {
      return this.thumbLength;
   }

   public int getPalmLength() {
      return this.palmLength;
   }

   private BlockPattern.PatternHelper checkPatternAt(BlockPos var1, EnumFacing var2, EnumFacing var3, LoadingCache<BlockPos, BlockWorldState> var4) {
      for (int var8 = 0; var8 < this.palmLength; var8++) {
         for (int var9 = 0; var9 < this.thumbLength; var9++) {
            for (int var10 = 0; var10 < this.fingerLength; var10++) {
               if (!this.blockMatches[var10][var9][var8].apply((BlockWorldState)var4.getUnchecked(translateOffset(var1, var2, var3, var8, var9, var10)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(var1, var2, var3, var4, this.palmLength, this.thumbLength, this.fingerLength);
   }

   public BlockPattern.PatternHelper match(World var1, BlockPos var2) {
      LoadingCache var6 = func_181627_a(var1, false);
      int var7 = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);

      for (BlockPos var9 : BlockPos.getAllInBox(var2, var2.add(var7 - 1, var7 - 1, var7 - 1))) {
         for (EnumFacing var13 : EnumFacing.values()) {
            for (EnumFacing var17 : EnumFacing.values()) {
               if (var17 != var13 && var17 != var13.getOpposite()) {
                  BlockPattern.PatternHelper var18 = this.checkPatternAt(var9, var13, var17, var6);
                  if (var18 != null) {
                     return var18;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, BlockWorldState> func_181627_a(World var0, boolean var1) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(var0, var1));
   }

   public static BlockPos translateOffset(BlockPos var0, EnumFacing var1, EnumFacing var2, int var3, int var4, int var5) {
      if (var1 != var2 && var1 != var2.getOpposite()) {
         Vec3i var10 = new Vec3i(var1.getFrontOffsetX(), var1.getFrontOffsetY(), var1.getFrontOffsetZ());
         Vec3i var11 = new Vec3i(var2.getFrontOffsetX(), var2.getFrontOffsetY(), var2.getFrontOffsetZ());
         Vec3i var12 = var10.crossProduct(var11);
         return var0.add(
            var11.getX() * -var4 + var12.getX() * var3 + var10.getX() * var5,
            var11.getY() * -var4 + var12.getY() * var3 + var10.getY() * var5,
            var11.getZ() * -var4 + var12.getZ() * var3 + var10.getZ() * var5
         );
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   public static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, BlockWorldState> {
      private final World world;
      private final boolean field_181626_b;

      public CacheLoader(World var1, boolean var2) {
         this.world = var1;
         this.field_181626_b = var2;
      }

      public BlockWorldState load(BlockPos var1) throws Exception {
         return new BlockWorldState(this.world, var1, this.field_181626_b);
      }
   }

   public static class PatternHelper {
      private final BlockPos pos;
      private final EnumFacing finger;
      private final EnumFacing thumb;
      private final LoadingCache<BlockPos, BlockWorldState> lcache;
      private final int field_181120_e;
      private final int field_181121_f;
      private final int field_181122_g;
      private static final long a = 31604302278869L;

      public PatternHelper(BlockPos var1, EnumFacing var2, EnumFacing var3, LoadingCache<BlockPos, BlockWorldState> var4, int var5, int var6, int var7) {
         this.pos = var1;
         this.finger = var2;
         this.thumb = var3;
         this.lcache = var4;
         this.field_181120_e = var5;
         this.field_181121_f = var6;
         this.field_181122_g = var7;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public EnumFacing getFinger() {
         return this.finger;
      }

      public EnumFacing getThumb() {
         return this.thumb;
      }

      public int func_181118_d() {
         return this.field_181120_e;
      }

      public int func_181119_e() {
         return this.field_181121_f;
      }

      public BlockWorldState translateOffset(int var1, int var2, int var3) {
         return this.lcache.getUnchecked(BlockPattern.translateOffset(this.pos, this.getFinger(), this.getThumb(), var1, var2, var3));
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).add("up", this.thumb).add("forwards", this.finger).add("frontTopLeft", this.pos).toString();
      }
   }
}
