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

   public void func_178606_a(BlockPos pos) {
      SetupTerrainEvent setupTerrainEvent = new SetupTerrainEvent();
      Astolfo.INSTANCE.getEventBus().post(setupTerrainEvent);
      if (!setupTerrainEvent.isCancelled()) {
         this.field_178612_d.set(getIndex(pos), true);
         this.field_178611_f--;
      }
   }

   private static int getIndex(BlockPos pos) {
      return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
   }

   private static int getIndex(int x, int y, int z) {
      return x << 0 | y << 8 | z << 4;
   }

   public SetVisibility computeVisibility() {
      SetVisibility setVisibility = new SetVisibility();
      if (4096 - this.field_178611_f < 256 || ModuleManager.xray.isEnabled() || ModuleManager.caveFinder.isEnabled()) {
         setVisibility.setAllVisible(true);
      } else if (this.field_178611_f == 0) {
         setVisibility.setAllVisible(false);
      } else {
         for (int i : field_178613_e) {
            if (!this.field_178612_d.get(i)) {
               setVisibility.setManyVisible(this.func_178604_a(i));
            }
         }
      }

      return setVisibility;
   }

   public Set<EnumFacing> func_178609_b(BlockPos pos) {
      return this.func_178604_a(getIndex(pos));
   }

   private Set<EnumFacing> func_178604_a(int i) {
      EnumSet enumSet = EnumSet.noneOf(EnumFacing.class);
      ArrayDeque arrayDeque = new ArrayDeque(384);
      arrayDeque.add(IntegerCache.getInteger(i));
      this.field_178612_d.set(i, true);

      while (!arrayDeque.isEmpty()) {
         int j = (Integer)arrayDeque.poll();
         this.func_178610_a(j, enumSet);

         for (EnumFacing enumFacing : EnumFacing.VALUES) {
            int k = this.func_178603_a(j, enumFacing);
            if (k >= 0 && !this.field_178612_d.get(k)) {
               this.field_178612_d.set(k, true);
               arrayDeque.add(IntegerCache.getInteger(k));
            }
         }
      }

      return enumSet;
   }

   private void func_178610_a(int i, Set<EnumFacing> set) {
      int j = i >> 0 & 15;
      if (j == 0) {
         set.add(EnumFacing.WEST);
      } else if (j == 15) {
         set.add(EnumFacing.EAST);
      }

      int k = i >> 8 & 15;
      if (k == 0) {
         set.add(EnumFacing.DOWN);
      } else if (k == 15) {
         set.add(EnumFacing.UP);
      }

      int l = i >> 4 & 15;
      if (l == 0) {
         set.add(EnumFacing.NORTH);
      } else if (l == 15) {
         set.add(EnumFacing.SOUTH);
      }
   }

   private int func_178603_a(int i, EnumFacing enumFacing) {
      switch (enumFacing) {
         case DOWN:
            if ((i >> 8 & 15) == 0) {
               return -1;
            }

            return i - field_178615_c;
         case UP:
            if ((i >> 8 & 15) == 15) {
               return -1;
            }

            return i + field_178615_c;
         case NORTH:
            if ((i >> 4 & 15) == 0) {
               return -1;
            }

            return i - field_178614_b;
         case SOUTH:
            if ((i >> 4 & 15) == 15) {
               return -1;
            }

            return i + field_178614_b;
         case WEST:
            if ((i >> 0 & 15) == 0) {
               return -1;
            }

            return i - field_178616_a;
         case EAST:
            if ((i >> 0 & 15) == 15) {
               return -1;
            }

            return i + field_178616_a;
         default:
            return -1;
      }
   }

   static {
      int i = 0;

      for (int j = 0; j < 16; j++) {
         for (int k = 0; k < 16; k++) {
            for (int l = 0; l < 16; l++) {
               if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                  field_178613_e[i++] = getIndex(j, k, l);
               }
            }
         }
      }
   }
}
