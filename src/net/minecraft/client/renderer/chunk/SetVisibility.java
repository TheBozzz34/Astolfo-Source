package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility {
   private static final int COUNT_FACES = EnumFacing.values().length;
   private final BitSet bitSet = new BitSet(COUNT_FACES * COUNT_FACES);
   private static final long a = 74764413644242L;

   public void setManyVisible(Set<EnumFacing> var1) {
      for (EnumFacing var6 : var1) {
         for (EnumFacing var8 : var1) {
            this.setVisible(var6, var8, true);
         }
      }
   }

   public void setVisible(EnumFacing var1, EnumFacing var2, boolean var3) {
      this.bitSet.set(var1.ordinal() + var2.ordinal() * COUNT_FACES, var3);
      this.bitSet.set(var2.ordinal() + var1.ordinal() * COUNT_FACES, var3);
   }

   public void setAllVisible(boolean var1) {
      this.bitSet.set(0, this.bitSet.size(), var1);
   }

   public boolean isVisible(EnumFacing var1, EnumFacing var2) {
      return this.bitSet.get(var1.ordinal() + var2.ordinal() * COUNT_FACES);
   }

   @Override
   public String toString() {
      StringBuilder var4 = new StringBuilder();
      var4.append(' ');

      for (EnumFacing var8 : EnumFacing.values()) {
         var4.append(' ').append(var8.toString().toUpperCase().charAt(0));
      }

      var4.append('\n');

      for (EnumFacing var17 : EnumFacing.values()) {
         var4.append(var17.toString().toUpperCase().charAt(0));

         for (EnumFacing var12 : EnumFacing.values()) {
            if (var17 == var12) {
               var4.append("  ");
            } else {
               boolean var13 = this.isVisible(var17, var12);
               var4.append(' ').append((char)(var13 ? 'Y' : 'n'));
            }
         }

         var4.append('\n');
      }

      return var4.toString();
   }
}
