package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility {
   private static final int COUNT_FACES = EnumFacing.values().length;
   private final BitSet bitSet = new BitSet(COUNT_FACES * COUNT_FACES);

   public void setManyVisible(Set<EnumFacing> set) {
      for (EnumFacing enumFacing : set) {
         for (EnumFacing enumFacing2 : set) {
            this.setVisible(enumFacing, enumFacing2, true);
         }
      }
   }

   public void setVisible(EnumFacing facing, EnumFacing facing2, boolean bl) {
      this.bitSet.set(facing.ordinal() + facing2.ordinal() * COUNT_FACES, bl);
      this.bitSet.set(facing2.ordinal() + facing.ordinal() * COUNT_FACES, bl);
   }

   public void setAllVisible(boolean visible) {
      this.bitSet.set(0, this.bitSet.size(), visible);
   }

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.bitSet.get(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(' ');

      for (EnumFacing enumFacing : EnumFacing.values()) {
         stringBuilder.append(' ').append(enumFacing.toString().toUpperCase().charAt(0));
      }

      stringBuilder.append('\n');

      for (EnumFacing enumFacing2 : EnumFacing.values()) {
         stringBuilder.append(enumFacing2.toString().toUpperCase().charAt(0));

         for (EnumFacing enumFacing3 : EnumFacing.values()) {
            if (enumFacing2 == enumFacing3) {
               stringBuilder.append("  ");
            } else {
               boolean bl = this.isVisible(enumFacing2, enumFacing3);
               stringBuilder.append(' ').append((char)(bl ? 'Y' : 'n'));
            }
         }

         stringBuilder.append('\n');
      }

      return stringBuilder.toString();
   }
}
