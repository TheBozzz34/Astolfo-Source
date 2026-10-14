package bypass.module.hidden;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.UpdateEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.HiddenModule;
import bypass.util.GameUtil;
import java.util.ArrayList;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

public final class BotDetector extends HiddenModule {
   public static final ArrayList<EntityLivingBase> w = new ArrayList<>();
   public static final ArrayList<EntityLivingBase> E = new ArrayList<>();
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      E.clear();
      w.clear();
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (updateEvent.getState() == EventState.PRE) {
         if (!w.isEmpty()) {
            w.clear();
         }

         for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && !w.contains(entity) && entity != mc.thePlayer && this.f((EntityPlayer)entity)) {
               w.add((EntityLivingBase)entity);
            }
         }
      }
   };

   public BotDetector() {
      client.getEventBus().subscribe(this);
   }

   private boolean f(EntityPlayer entityPlayer) {
      if (GameUtil.isOnServer("syuu")) {
         return false;
      }

      if (entityPlayer.getDisplayName().getFormattedText().startsWith("§8[NPC]") && !mc.thePlayer.getDisplayName().getFormattedText().startsWith("§8[NPC]")) {
         return true;
      }

      if (GameUtil.isOnServer("hypixel")) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityPlayer;
         if (abstractClientPlayer.getLocationSkin().toString().equalsIgnoreCase("minecraft:skins/b2f3fc4516774596bfe5c2e16f661106f115ebf651e1287a7cca43399e8c194")) {
            return true;
         }

         if (abstractClientPlayer.getName().contains("§") || abstractClientPlayer.hasCustomName() && abstractClientPlayer.getCustomNameTag().contains(abstractClientPlayer.getName())) {
            return true;
         }

         if (abstractClientPlayer.getPlayerInfo() != null && abstractClientPlayer.getPlayerInfo().getResponseTime() > 1 && !E.contains(abstractClientPlayer)) {
            if (abstractClientPlayer.hurtTime > 0) {
               E.add(abstractClientPlayer);
               return false;
            }

            return true;
         }

         if (GameUtil.scoreboardTitleContains("PIT")
            && mc.thePlayer.getDisplayName().getUnformattedText().contains("BEAST")
            && !entityPlayer.getName().contains("BEAST")
            && entityPlayer.getDisplayName().getUnformattedText().contains("BEAST")) {
            return true;
         }

         if (GameUtil.scoreboardTitleContains("BED WARS") && !entityPlayer.getDisplayName().getUnformattedText().contains("§")) {
            return true;
         }
      }

      if (GameUtil.isOnServer("mineplex") && !Double.isNaN(entityPlayer.getHealth())) {
         return true;
      } else if (entityPlayer.getUniqueID().toString().startsWith(entityPlayer.getName())) {
         return true;
      } else {
         return !StringUtils.stripControlCodes(entityPlayer.getGameProfile().getName()).equals(entityPlayer.getName())
            ? true
            : entityPlayer.getGameProfile().getId() != entityPlayer.getUniqueID();
      }
   }
}
