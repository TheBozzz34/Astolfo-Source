package net.minecraft.client.renderer.chunk;

import bypass.Astolfo;
import bypass.event.render.SetupTerrainEvent;
import bypass.module.ModuleManager;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;

public class VisGraph {
   private static final int field_178616_a = (int)Math.pow(16.0, 0.0);
   private static final int field_178614_b = (int)Math.pow(16.0, 1.0);
   private static final int field_178615_c = (int)Math.pow(16.0, 2.0);
   private final BitSet field_178612_d = new BitSet(4096);
   private static final int[] field_178613_e = new int[1352];
   private int field_178611_f = 4096;
   private static final long a = 62532722898767L;

   public void func_178606_a(BlockPos var1) {
      SetupTerrainEvent var5 = new SetupTerrainEvent();
      Astolfo.INSTANCE.getEventBus().post(var5);
      if (!var5.isCancelled()) {
         this.field_178612_d.set(getIndex(var1), true);
         this.field_178611_f--;
      }
   }

   private static int getIndex(BlockPos var0) {
      return getIndex(var0.getX() & 15, var0.getY() & 15, var0.getZ() & 15);
   }

   private static int getIndex(int var0, int var1, int var2) {
      return var0 << 0 | var1 << 8 | var2 << 4;
   }

   public SetVisibility computeVisibility() {
      SetVisibility var4 = new SetVisibility();
      if (4096 - this.field_178611_f < 256 || ModuleManager.s.isEnabled() || ModuleManager.t.isEnabled()) {
         var4.setAllVisible(true);
      } else if (this.field_178611_f == 0) {
         var4.setAllVisible(false);
      } else {
         for (int var8 : field_178613_e) {
            if (!this.field_178612_d.get(var8)) {
               var4.setManyVisible(this.func_178604_a(var8));
            }
         }
      }

      return var4;
   }

   public Set<EnumFacing> func_178609_b(BlockPos var1) {
      return this.func_178604_a(getIndex(var1));
   }

   private Set<EnumFacing> func_178604_a(int var1) {
      EnumSet var5 = EnumSet.noneOf(EnumFacing.class);
      ArrayDeque var6 = new ArrayDeque(384);
      var6.add(IntegerCache.getInteger(var1));
      this.field_178612_d.set(var1, true);

      while (!var6.isEmpty()) {
         int var7 = (Integer)var6.poll();
         this.func_178610_a(var7, var5);

         for (EnumFacing var11 : EnumFacing.VALUES) {
            int var12 = this.func_178603_a(var7, var11);
            if (var12 >= 0 && !this.field_178612_d.get(var12)) {
               this.field_178612_d.set(var12, true);
               var6.add(IntegerCache.getInteger(var12));
            }
         }
      }

      return var5;
   }

   private void func_178610_a(int var1, Set<EnumFacing> var2) {
      int var6 = var1 >> 0 & 15;
      if (var6 == 0) {
         var2.add(EnumFacing.WEST);
      } else if (var6 == 15) {
         var2.add(EnumFacing.EAST);
      }

      int var7 = var1 >> 8 & 15;
      if (var7 == 0) {
         var2.add(EnumFacing.DOWN);
      } else if (var7 == 15) {
         var2.add(EnumFacing.UP);
      }

      int var8 = var1 >> 4 & 15;
      if (var8 == 0) {
         var2.add(EnumFacing.NORTH);
      } else if (var8 == 15) {
         var2.add(EnumFacing.SOUTH);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int func_178603_a(int var1, EnumFacing var2) {
      switch (var2) {
         case DOWN:
            if ((var1 >> 8 & 15) == 0) {
               return -1;
            }

            return var1 - field_178615_c;
         case UP:
            if ((var1 >> 8 & 15) == 15) {
               return -1;
            }

            return var1 + field_178615_c;
         case NORTH:
            if ((var1 >> 4 & 15) == 0) {
               return -1;
            }

            return var1 - field_178614_b;
         case SOUTH:
            if ((var1 >> 4 & 15) == 15) {
               return -1;
            }

            return var1 + field_178614_b;
         case WEST:
            if ((var1 >> 0 & 15) == 0) {
               return -1;
            }

            return var1 - field_178616_a;
         case EAST:
            if ((var1 >> 0 & 15) == 15) {
               return -1;
            }

            return var1 + field_178616_a;
         default:
            return -1;
      }
   }

   static {
      int var15 = 0;

      for (int var16 = 0; var16 < 16; var16++) {
         for (int var17 = 0; var17 < 16; var17++) {
            for (int var18 = 0; var18 < 16; var18++) {
               if (var16 == 0 || var16 == 15 || var17 == 0 || var17 == 15 || var18 == 0 || var18 == 15) {
                  field_178613_e[var15++] = getIndex(var16, var17, var18);
               }
            }
         }
      }
   }
}
