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

   public PlayerPotionEffects(EntityPlayer entityPlayer) {
      this.player = entityPlayer;
   }

   public void add(PotionEffect potionEffect) {
      this.effects.put(potionEffect.getPotionID(), potionEffect);
      this.color = HypixelGameTracker.getColor(this.effects.values());
   }

   public void remove(PotionEffect potionEffect) {
      this.effects.remove(potionEffect.getPotionID());
      this.color = HypixelGameTracker.getColor(this.effects.values());
   }

   public void set(List<PotionEffect> list) {
      this.effects.clear();

      for (PotionEffect potionEffect : list) {
         this.effects.put(potionEffect.getPotionID(), potionEffect);
      }

      this.color = HypixelGameTracker.getColor(this.effects.values());
   }

   public void clear() {
      this.effects.clear();
      this.color = 0;
   }
}
