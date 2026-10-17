package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.DamageBlockEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class FastMine extends Module {
   private final NumberSetting<Double> multiplier = new NumberSetting<>("Multiplier", "Multiplier", 1.25, 0.75, 5.0);
   private final NumberSetting<Integer> hitDelay = new NumberSetting<>("Hit-Delay", "Speed of haste", 0, 0, 4);
   private final BooleanSetting fast = new BooleanSetting("Fast", "Powered by china", false);
   private final BooleanSetting removeFatigue = new BooleanSetting("Remove-Fatigue", "Remove-Fatigue", false);
   private final MapSetting instant = new MapSettingBuilder("Instant")
      .ordered()
      .description("Delay stuff")
      .add("Delay", new NumberSetting<>("Delay", "Delay", 2, 0, 5, " ticks"))
      .add("Enable", new BooleanSetting("Enable", "Enable instant mining", false))
      .build();
   private boolean mining = false;
   private float breakProgress = 0.0F;
   public BlockPos miningPos;
   public EnumFacing miningFacing;
   private final ArrayList<PendingBreak> pendingBreaks = new ArrayList<>();
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (!this.pendingBreaks.isEmpty()) {
         Iterator iterator2 = this.pendingBreaks.iterator();

         while (iterator2.hasNext()) {
            PendingBreak pendingBreak = (PendingBreak)iterator2.next();
            if (pendingBreak.ticksLeft <= 0) {
               mc.thePlayer.sendQueue.u(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pendingBreak.pos, pendingBreak.facing));
               iterator2.remove();
            } else {
               pendingBreak.ticksLeft--;
            }
         }
      }

      if (this.removeFatigue.getValue() && mc.thePlayer.getActivePotionEffect(Potion.digSlowdown) != null) {
         mc.thePlayer.removePotionEffect(Potion.digSlowdown.id);
      }

      if (this.fast.getValue() && this.mining) {
         Block block = mc.theWorld.getBlockState(this.miningPos).getBlock();
         this.breakProgress = this.breakProgress
            + (float)(block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.miningPos) * this.multiplier.asDouble());
         if (this.breakProgress >= 1.0F) {
            mc.theWorld.setBlockState(this.miningPos, Blocks.air.getDefaultState(), 11);
            mc.thePlayer
               .sendQueue
               .getNetworkManager()
               .sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.miningPos, this.miningFacing));
            this.breakProgress = 0.0F;
            this.mining = false;
         }
      }
   };
   @EventHandler(DamageBlockEvent.class)
   private final Listener<DamageBlockEvent> onDamageBlock = damageBlockEvent -> {
      if (!this.fast.getValue()) {
         damageBlockEvent.setDamageMultiplier(this.multiplier.getValue());
      }

      damageBlockEvent.setHitDelay(this.hitDelay.getValue());
   };
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (this.fast.getValue()) {
            if (packetEvent.getPacket() instanceof C07PacketPlayerDigging && !mc.playerController.extendedReach() && mc.playerController != null) {
               C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)packetEvent.getPacket();
               if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                  this.mining = true;
                  this.miningPos = c07PacketPlayerDigging.getPosition();
                  this.miningFacing = c07PacketPlayerDigging.getFacing();
                  this.breakProgress = 0.0F;
               } else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK
                  || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                  this.mining = false;
                  this.miningPos = null;
                  this.miningFacing = null;
               }
            }

            return;
         }

         if (this.instant.getSetting("Enable").asBoolean() && packetEvent.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging c07PacketPlayerDigging2 = (C07PacketPlayerDigging)packetEvent.getPacket();
            if (c07PacketPlayerDigging2.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               mc.thePlayer.sendQueue.u(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, c07PacketPlayerDigging2.getPosition(), c07PacketPlayerDigging2.getFacing()));
               this.pendingBreaks.add(new PendingBreak(this.instant.getSetting("Delay").asInt() + 5, c07PacketPlayerDigging2.getPosition(), c07PacketPlayerDigging2.getFacing()));
               packetEvent.cancel();
            }

            if (c07PacketPlayerDigging2.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
               for (PendingBreak pendingBreak : this.pendingBreaks) {
                  if (pendingBreak.pos.equals(c07PacketPlayerDigging2.getPosition())) {
                     packetEvent.cancel();
                     break;
                  }
               }
            }
         }
      }
   };

   public FastMine(Category category) {
      super(category);
      this.addSettings(this.instant, this.multiplier, this.fast, this.hitDelay, this.removeFatigue);
   }

   @Override
   public void onDisable() {
      if (mc.thePlayer != null) {
         mc.playerController.blockHitDelay = 5;
      }

      super.onDisable();
   }
}
