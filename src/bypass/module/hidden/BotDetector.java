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
   private Listener<WorldLoadEvent> onWorldLoad = var0 -> {
      E.clear();
      w.clear();
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1 -> {
      if (var1.getState() == EventState.PRE) {
         if (!w.isEmpty()) {
            w.clear();
         }

         for (Entity var6 : mc.theWorld.loadedEntityList) {
            if (var6 instanceof EntityPlayer && !w.contains(var6) && var6 != mc.thePlayer && this.f((EntityPlayer)var6)) {
               w.add((EntityLivingBase)var6);
            }
         }
      }
   };
   private static final long d = 78223321542629L;

   public BotDetector() {
      client.getEventBus().subscribe(this);
   }

   private boolean f(EntityPlayer var1) {
      if (GameUtil.isOnServer("syuu")) {
         return false;
      }

      if (var1.getDisplayName().getFormattedText().startsWith("§8[NPC]") && !mc.thePlayer.getDisplayName().getFormattedText().startsWith("§8[NPC]")) {
         return true;
      }

      if (GameUtil.isOnServer("hypixel")) {
         AbstractClientPlayer var5 = (AbstractClientPlayer)var1;
         if (var5.getLocationSkin().toString().equalsIgnoreCase("minecraft:skins/b2f3fc4516774596bfe5c2e16f661106f115ebf651e1287a7cca43399e8c194")) {
            return true;
         }

         if (var5.getName().contains("§") || var5.hasCustomName() && var5.getCustomNameTag().contains(var5.getName())) {
            return true;
         }

         if (var5.getPlayerInfo() != null && var5.getPlayerInfo().getResponseTime() > 1 && !E.contains(var5)) {
            if (var5.hurtTime > 0) {
               E.add(var5);
               return false;
            }

            return true;
         }

         if (GameUtil.scoreboardTitleContains("PIT")
            && mc.thePlayer.getDisplayName().getUnformattedText().contains("BEAST")
            && !var1.getName().contains("BEAST")
            && var1.getDisplayName().getUnformattedText().contains("BEAST")) {
            return true;
         }

         if (GameUtil.scoreboardTitleContains("BED WARS") && !var1.getDisplayName().getUnformattedText().contains("§")) {
            return true;
         }
      }

      if (GameUtil.isOnServer("mineplex") && !Double.isNaN(var1.getHealth())) {
         return true;
      } else if (var1.getUniqueID().toString().startsWith(var1.getName())) {
         return true;
      } else {
         return !StringUtils.stripControlCodes(var1.getGameProfile().getName()).equals(var1.getName())
            ? true
            : var1.getGameProfile().getId() != var1.getUniqueID();
      }
   }
}
