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
   private boolean O = false;
   private float A = 0.0F;
   public BlockPos x;
   public EnumFacing v;
   private final ArrayList<PendingBreak> d = new ArrayList<>();
   @EventHandler(UpdateEvent.class)
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (!this.d.isEmpty()) {
         Iterator iterator2 = this.d.iterator();

         while (iterator2.hasNext()) {
            PendingBreak pendingBreak = (PendingBreak)iterator2.next();
            if (pendingBreak.B <= 0) {
               mc.thePlayer.sendQueue.u(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pendingBreak.f, pendingBreak.e));
               iterator2.remove();
            } else {
               pendingBreak.B--;
            }
         }
      }

      if (this.removeFatigue.getValue() && mc.thePlayer.getActivePotionEffect(Potion.digSlowdown) != null) {
         mc.thePlayer.removePotionEffect(Potion.digSlowdown.id);
      }

      if (this.fast.getValue() && this.O) {
         Block block = mc.theWorld.getBlockState(this.x).getBlock();
         this.A = this.A + (float)(block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.x) * this.multiplier.asDouble());
         if (this.A >= 1.0F) {
            mc.theWorld.setBlockState(this.x, Blocks.air.getDefaultState(), 11);
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.x, this.v));
            this.A = 0.0F;
            this.O = false;
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
            if (packetEvent.getPacket() instanceof C07PacketPlayerDigging && !mc.playerController.L() && mc.playerController != null) {
               C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)packetEvent.getPacket();
               if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                  this.O = true;
                  this.x = c07PacketPlayerDigging.getPosition();
                  this.v = c07PacketPlayerDigging.getFacing();
                  this.A = 0.0F;
               } else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK
                  || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                  this.O = false;
                  this.x = null;
                  this.v = null;
               }
            }

            return;
         }

         if (this.instant.getSetting("Enable").asBoolean() && packetEvent.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging c07PacketPlayerDigging2 = (C07PacketPlayerDigging)packetEvent.getPacket();
            if (c07PacketPlayerDigging2.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               mc.thePlayer.sendQueue.u(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, c07PacketPlayerDigging2.getPosition(), c07PacketPlayerDigging2.getFacing()));
               this.d.add(new PendingBreak(this.instant.getSetting("Delay").asInt() + 5, c07PacketPlayerDigging2.getPosition(), c07PacketPlayerDigging2.getFacing()));
               packetEvent.cancel();
            }

            if (c07PacketPlayerDigging2.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
               for (PendingBreak pendingBreak : this.d) {
                  if (pendingBreak.f.equals(c07PacketPlayerDigging2.getPosition())) {
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
