package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.combat.KillAura;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.util.GameUtil;
import bypass.util.RandomUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public final class NoSlow extends Module {
   private final EnumSetting<NoSlowMode> mode = new EnumSetting<>("Mode", "Set your NoSlow mode", NoSlowMode.VANILLA);
   public static final BooleanSetting soulSand = new BooleanSetting("Soul-Sand", "Stops soul sand from slowing you down", true);
   public static final BooleanSetting ladderFix = new BooleanSetting("Ladder-Fix", "Fixes ladders or vines from slowing you down", true);
   private final List<Block> slipperyBlocks = new CopyOnWriteArrayList<>();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> this.updateDisplayName();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      boolean bl = client.getModuleManager().get(KillAura.class).isEnabled();
      switch (motionEvent.getState()) {
         case PRE:
            if (this.mode.getValue() == NoSlowMode.NCP) {
               if (mc.thePlayer.isBlocking() && !bl) {
                  mc.thePlayer
                     .sendQueue
                     .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
               }
            } else if (this.mode.getValue() == NoSlowMode.WATCHDOG) {
               ItemStack itemStack = mc.thePlayer.getItemInUse();
               if (itemStack != null
                  && mc.thePlayer.isUsingItem()
                  && GameUtil.isMoving(mc.thePlayer)
                  && !mc.thePlayer.isBlocking()
                  && GameUtil.isOnHypixel()
                  && mc.thePlayer.ticksExisted % 5 == 0) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.playerController.currentPlayerItem));
               }
            }
            break;
         case POST:
            if (this.mode.getValue() == NoSlowMode.NCP && mc.thePlayer.isBlocking() && !bl) {
               if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.objectMouseOver.entityHit, C02PacketUseEntity.Action.INTERACT));
               }

               mc.thePlayer
                  .sendQueue
                  .addToSendQueue(
                     new C08PacketPlayerBlockPlacement(
                        new BlockPos(RandomUtils.nextDouble(-0.9, -0.3), RandomUtils.nextDouble(-0.9, -0.3), RandomUtils.nextDouble(-0.9, -0.3)),
                        255,
                        mc.thePlayer.inventory.getCurrentItem(),
                        0.0F,
                        0.0F,
                        0.0F
                     )
                  );
            }
      }
   };

   public NoSlow(Category category) {
      super(category);
      this.setModuleColor(-8021543);
      this.addSettings(this.mode, soulSand, ladderFix);
   }

   @Override
   public void updateDisplayName() {
      this.setDisplayName("No Slow " + EnumChatFormatting.GRAY + NoSlowMode.displayNameOf(this.mode.getValue()));
      super.updateDisplayName();
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

         for (Block block : this.slipperyBlocks) {
            block.slipperiness = 0.6F;
         }
      }

      super.onDisable();
   }
}
