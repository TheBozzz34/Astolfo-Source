package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;

public final class FriendProtect extends Module {
   private final BooleanSetting notifies = new BooleanSetting("Notifies", "Notifies you when trying to attack a friend", true);
   private final BooleanSetting swordOnly = new BooleanSetting("Sword-Only", "Only prevents attack with sword", false);
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C02PacketUseEntity) {
         C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
         if (c02PacketUseEntity.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
            Entity entity = c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
            ItemStack itemStack = mc.thePlayer.getCurrentEquippedItem();
            Item item = itemStack == null ? null : itemStack.getItem();
            if (entity instanceof EntityPlayer && client.getRelationManager().isFriend(entity)) {
               if (this.swordOnly.getValue() && item instanceof ItemSword) {
                  return;
               }

               if (this.notifies.getValue()) {
                  toasts.infoMessage("Friend protect", String.format("%s is your friend", entity.getName()));
               }

               packetEvent.cancel();
            }
         }
      }
   };

   public FriendProtect(Category category) {
      super(category);
      this.addSettings(this.notifies, this.swordOnly);
      this.setHidden(false);
   }
}
