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
   private final Listener<HitboxEvent> onHitbox = hitboxEvent -> {
      if (hitboxEvent.entity instanceof EntityPlayer && (!G || !wither.getValue())) {
         if (GameUtil.O(mc.thePlayer, (EntityPlayer)hitboxEvent.entity) && this.checks.getSetting("Teams").asBoolean()) {
            return;
         }

         if ((!GameUtil.S(hitboxEvent.entity) || this.checks.getSetting("Invisible").asBoolean()) && !BotDetector.w.contains((EntityPlayer)hitboxEvent.entity)) {
            hitboxEvent.borderSize = size.getValue();
         }
      }
   };
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE && wither.getValue()) {
         G = false;

         for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityWither
               && mc.thePlayer.getDistanceToEntity(entity) < 3.0
               && (!GameUtil.isBoss(entity) || !GameUtil.O(mc.thePlayer, (EntityLivingBase)entity))) {
               G = true;
               break;
            }
         }
      }
   };
   private static final long c = 73694703082346L;

   public Hitboxes(Category category) {
      super(category);
      this.addSettings(size, this.checks, wither);
   }
}
