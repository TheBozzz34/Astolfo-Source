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
   private Listener<PacketEvent> onPacket = var1x -> {
      long var2 = c ^ 105307080271894L;
      long var10001 = var2 ^ 136644924826719L;
      int var4 = (int)((var2 ^ 136644924826719L) >>> 56);
      int var5 = (int)((var2 ^ 136644924826719L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C02PacketUseEntity) {
         C02PacketUseEntity var8 = (C02PacketUseEntity)var1x.getPacket();
         if (var8.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
            Entity var9 = var8.getEntityFromWorld(mc.theWorld);
            ItemStack var10 = mc.thePlayer.getCurrentEquippedItem();
            Item var11 = var10 == null ? null : var10.getItem();
            if (var9 instanceof EntityPlayer && client.getRelationManager().z(var9)) {
               if (this.swordOnly.getValue() && var11 instanceof ItemSword) {
                  return;
               }

               if (this.notifies.getValue()) {
                  toasts.B("Friend protect", String.format("%s is your friend", var9.getName()));
               }

               var1x.cancel();
            }
         }
      }
   };
   private static final long c = 91048993024276L;

   public FriendProtect(Category var1) {
      super(var1);
      this.addSettings(this.notifies, this.swordOnly);
      this.setHidden(false);
   }
}
