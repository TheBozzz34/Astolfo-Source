package bypass.module.hidden;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

public class PlayerPotionEffects {
   public final Map<Integer, PotionEffect> effects = Maps.newHashMap();
   public final EntityPlayer player;
   public int color;
   private static final long a = 86460945804019L;

   public PlayerPotionEffects(EntityPlayer var1) {
      this.player = var1;
   }

   public void add(PotionEffect var1) {
      this.effects.put(var1.getPotionID(), var1);
      this.color = HypixelGameTracker.getColor(this.effects.values());
   }

   public void remove(PotionEffect var1) {
      this.effects.remove(var1.getPotionID());
      this.color = HypixelGameTracker.getColor(this.effects.values());
   }

   public void set(List<PotionEffect> var1) {
      this.effects.clear();

      for (PotionEffect var6 : var1) {
         this.effects.put(var6.getPotionID(), var6);
      }

      this.color = HypixelGameTracker.getColor(this.effects.values());
   }

   public void clear() {
      this.effects.clear();
      this.color = 0;
   }
}
