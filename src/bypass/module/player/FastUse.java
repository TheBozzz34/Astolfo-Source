package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.RiderGod;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.RandomUtils;
import bypass.util.Stopwatch;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.EnumChatFormatting;

public final class FastUse extends Module {
   private final EnumSetting<FastUseMode> mode = new EnumSetting<>("Mode", "Fast use mode", FastUseMode.VANILLA);
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Ticks that will be needed to pass to fast use", 12, 8, 32);
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      this.updateDisplayName();
      boolean bl = RiderGod.isAtLeast1122Protocol() && GameUtil.isOnHypixel();
      if (!bl
         && updateEvent.getState() == EventState.PRE
         && this.mode.getValue() == FastUseMode.VANILLA
         && this.o_boolean()
         && mc.thePlayer.n() >= this.delay.getValue()) {
         int i = 32 - mc.thePlayer.n();

         for (int j = 0; j < i; j++) {
            mc.thePlayer
               .sendQueue
               .addToSendQueue(
                  new C03PacketPlayer.C04PacketPlayerPosition(
                     mc.thePlayer.posX, mc.thePlayer.posY + RandomUtils.nextDouble(1.0E-12, 1.0E-20), mc.thePlayer.posZ, mc.thePlayer.onGround
                  )
               );
         }

         mc.playerController.onStoppedUsingItem(mc.thePlayer);
      }
   };
   private int unusedInt;
   private Stopwatch useTimer = new Stopwatch();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      boolean bl = RiderGod.isAtLeast1122Protocol() && GameUtil.isOnHypixel();
      if (!bl
         && motionEvent.getState() == EventState.PRE
         && this.mode.getValue() == FastUseMode.SENTINEL
         && this.o_boolean()
         && mc.thePlayer.n() >= this.delay.getValue()
         && mc.thePlayer.ticksExisted % 2 == 0) {
         for (int i = 0; i < 2; i++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
         }

         motionEvent.setYaw((float)(motionEvent.getYaw() + Math.random()));
      }

      if (bl
         && motionEvent.getState() == EventState.PRE
         && mc.thePlayer.n() > 0
         && mc.thePlayer.getHeldItem() != null
         && (
            mc.thePlayer.getHeldItem().getItem().getItemUseAction(mc.thePlayer.getHeldItem()) == EnumAction.EAT
               || mc.thePlayer.getHeldItem().getItem().getItemUseAction(mc.thePlayer.getHeldItem()) == EnumAction.DRINK
         )
         && this.useTimer.hasElapsed(250.0F)) {
         sendUseItemPackets(mc.thePlayer.inventory.currentItem, mc.thePlayer.getHeldItem(), false);
         this.useTimer.reset();
      }
   };

   public FastUse(Category category) {
      super(category);
      this.addSettings(this.mode, this.delay);
   }

   public static void sendUseItemPackets(int i, ItemStack itemStack, boolean bl) {
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i));
      mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(itemStack));
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(0));
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(bl ? mc.thePlayer.inventory.currentItem : i));
   }

   @Override
   public void updateDisplayName() {
      boolean bl = RiderGod.isAtLeast1122Protocol() && GameUtil.isOnHypixel();
      if (!bl) {
         this.setDisplayName("Fast Use " + EnumChatFormatting.GRAY + this.delay.getValue());
      } else {
         this.setDisplayName("Fast Use " + EnumChatFormatting.GRAY + "Instant");
      }

      super.updateDisplayName();
   }

   private boolean o_boolean() {
      return mc.thePlayer.getHeldItem() != null
         && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);
   }
}
