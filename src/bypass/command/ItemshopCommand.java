package bypass.command;

import bypass.util.GameUtil;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;

public final class ItemshopCommand extends Command {
   public static final Vec3 HIT_VECTOR = new Vec3(0.0, 0.0, 0.0);

   public ItemshopCommand() {
      super("itemshop", "Opens the Bedwars item shop in Hypixel");
      this.setAliases(Arrays.asList("quickshop", "bwi", "ibw"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (GameUtil.isOnHypixel()) {
         for (Entity entity2 : mc.theWorld.loadedEntityList) {
            if (entity2 instanceof EntityArmorStand && entity2.getDisplayName().getFormattedText().contains("SHOP")) {
               mc.thePlayer.sendQueue.u(new C02PacketUseEntity(entity2, HIT_VECTOR));
               return;
            }
         }

         toasts.info("Shop entity not found");
      }
   }
}
