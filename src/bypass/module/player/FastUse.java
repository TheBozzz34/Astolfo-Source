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
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      boolean var5 = RiderGod.r() && GameUtil.isOnHypixel();
      if (!var5
         && var1x.getState() == EventState.PRE
         && this.mode.getValue() == FastUseMode.VANILLA
         && this.o_boolean()
         && mc.thePlayer.n() >= this.delay.getValue()) {
         int var6 = 32 - mc.thePlayer.n();

         for (int var7 = 0; var7 < var6; var7++) {
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
   private int z;
   private Stopwatch l = new Stopwatch();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      boolean var5 = RiderGod.r() && GameUtil.isOnHypixel();
      if (!var5
         && var1x.getState() == EventState.PRE
         && this.mode.getValue() == FastUseMode.SENTINEL
         && this.o_boolean()
         && mc.thePlayer.n() >= this.delay.getValue()
         && mc.thePlayer.ticksExisted % 2 == 0) {
         for (int var6 = 0; var6 < 2; var6++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
         }

         var1x.setYaw((float)(var1x.getYaw() + Math.random()));
      }

      if (var5
         && var1x.getState() == EventState.PRE
         && mc.thePlayer.n() > 0
         && mc.thePlayer.getHeldItem() != null
         && (
            mc.thePlayer.getHeldItem().getItem().getItemUseAction(mc.thePlayer.getHeldItem()) == EnumAction.EAT
               || mc.thePlayer.getHeldItem().getItem().getItemUseAction(mc.thePlayer.getHeldItem()) == EnumAction.DRINK
         )
         && this.l.hasElapsed(250.0F)) {
         E(mc.thePlayer.inventory.currentItem, mc.thePlayer.getHeldItem(), false);
         this.l.reset();
      }
   };
   private static final long c = 65051531058969L;

   public FastUse(Category var1) {
      super(var1);
      this.addSettings(this.mode, this.delay);
   }

   public static void E(int var0, ItemStack var1, boolean var2) {
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(var0));
      mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(var1));
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(0));
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(var2 ? mc.thePlayer.inventory.currentItem : var0));
   }

   @Override
   public void p() {
      boolean var4 = RiderGod.r() && GameUtil.isOnHypixel();
      if (!var4) {
         this.setDisplayName("Fast Use " + EnumChatFormatting.GRAY + this.delay.getValue());
      } else {
         this.setDisplayName("Fast Use " + EnumChatFormatting.GRAY + "Instant");
      }

      super.p();
   }

   private boolean o_boolean() {
      return mc.thePlayer.getHeldItem() != null
         && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);
   }
}
