package bypass.command;

import bypass.util.GameUtil;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class UpgradeshopCommand extends Command {
   private static final long d = 4214796333220L;

   public UpgradeshopCommand() {
      super("upgradeshop", "Opens the Bedwars upgrade shop in Hypixel");
      this.setAliases(Arrays.asList("ubw", "bwu"));
   }

   @Override
   public void runCommand(String[] var1) {
      if (GameUtil.isOnHypixel()) {
         for (Entity var6 : mc.theWorld.loadedEntityList) {
            if (var6 instanceof EntityArmorStand && var6.getDisplayName().getFormattedText().contains("UPGRADES")) {
               mc.thePlayer.sendQueue.u(new C02PacketUseEntity(var6, ItemshopCommand.P));
               return;
            }
         }

         toasts.D("Shop entity not found");
      }
   }
}
