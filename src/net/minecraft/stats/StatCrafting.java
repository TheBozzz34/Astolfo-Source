package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase {
   private final Item field_150960_a;

   public StatCrafting(String string, String string2, IChatComponent statNameIn, Item item) {
      super(string + string2, statNameIn);
      this.field_150960_a = item;
      int i = Item.getIdFromItem(item);
      if (i != 0) {
         IScoreObjectiveCriteria.INSTANCES.put(string + i, this.getCriteria());
      }
   }

   public Item func_150959_a() {
      return this.field_150960_a;
   }
}
