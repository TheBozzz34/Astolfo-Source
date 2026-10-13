package bypass.command;

import bypass.util.GameUtil;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;

public final class ItemshopCommand extends Command {
   public static final Vec3 P = new Vec3(0.0, 0.0, 0.0);
   private static final long d = 92869389261183L;

   public ItemshopCommand() {
      super("itemshop", "Opens the Bedwars item shop in Hypixel");
      this.setAliases(Arrays.asList("quickshop", "bwi", "ibw"));
   }

   @Override
   public void runCommand(String[] var1) {
      if (GameUtil.isOnHypixel()) {
         for (Entity var6 : mc.theWorld.loadedEntityList) {
            if (var6 instanceof EntityArmorStand && var6.getDisplayName().getFormattedText().contains("SHOP")) {
               mc.thePlayer.sendQueue.u(new C02PacketUseEntity(var6, P));
               return;
            }
         }

         toasts.D("Shop entity not found");
      }
   }
}
