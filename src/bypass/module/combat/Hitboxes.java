package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.HitboxEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.hidden.BotDetector;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;

public final class Hitboxes extends Module {
   public static final NumberSetting<Float> size = new NumberSetting<>("Size", "Hitbox size expansion", 0.3F, 0.1F, 1.0F);
   public static final BooleanSetting wither = new BooleanSetting(
      "Wither", "Disables other players hitboxes if a wither is near and increments the hitbox of it", false
   );
   private final MapSetting checks = new MapSettingBuilder("Checks")
      .ordered()
      .description("entity checks")
      .add("Invisible", new BooleanSetting("Invisible", "Makes invisible players hitbox bigger", false))
      .add("Teams", new BooleanSetting("Teams", "Removes bigger hitboxes from teammates", false))
      .build();
   public static boolean G = false;
   @EventHandler(HitboxEvent.class)
   private final Listener<HitboxEvent> onHitbox = var1x -> {
      if (var1x.entity instanceof EntityPlayer && (!G || !wither.getValue())) {
         if (GameUtil.O(mc.thePlayer, (EntityPlayer)var1x.entity) && this.checks.getSetting("Teams").asBoolean()) {
            return;
         }

         if ((!GameUtil.S(var1x.entity) || this.checks.getSetting("Invisible").asBoolean()) && !BotDetector.w.contains((EntityPlayer)var1x.entity)) {
            var1x.borderSize = size.getValue();
         }
      }
   };
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = var0 -> {
      if (var0.getState() == EventState.PRE && wither.getValue()) {
         G = false;

         for (Entity var5 : mc.theWorld.loadedEntityList) {
            if (var5 instanceof EntityWither
               && mc.thePlayer.getDistanceToEntity(var5) < 3.0
               && (!GameUtil.isBoss(var5) || !GameUtil.O(mc.thePlayer, (EntityLivingBase)var5))) {
               G = true;
               break;
            }
         }
      }
   };
   private static final long c = 73694703082346L;

   public Hitboxes(Category var1) {
      super(var1);
      this.addSettings(size, this.checks, wither);
   }
}
