package bypass.command;

import bypass.util.GameUtil;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class UpgradeshopCommand extends Command {
   public UpgradeshopCommand() {
      super("upgradeshop", "Opens the Bedwars upgrade shop in Hypixel");
      this.setAliases(Arrays.asList("ubw", "bwu"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (GameUtil.isOnHypixel()) {
         for (Entity entity2 : mc.theWorld.loadedEntityList) {
            if (entity2 instanceof EntityArmorStand && entity2.getDisplayName().getFormattedText().contains("UPGRADES")) {
               mc.thePlayer.sendQueue.sendPacketNoEvent(new C02PacketUseEntity(entity2, ItemshopCommand.HIT_VECTOR));
               return;
            }
         }

         toasts.info("Shop entity not found");
      }
   }
}
